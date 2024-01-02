package com.bingo.socket.core.cache;

import com.corundumstudio.socketio.SocketIOClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 用户的socket信息
 */
@Slf4j
@Component
public class SocketClientCache {

    //用于存储用户的socket缓存信息
    private static ConcurrentHashMap<String, HashMap<UUID, SocketIOClient>> concurrentHashMap = new ConcurrentHashMap<>();

    //保存用户信息
    public void saveClient(String userId, UUID sessionId, SocketIOClient socketIOClient) {
        HashMap<UUID, SocketIOClient> sessionIdClientCache = concurrentHashMap.get(userId);
        if (sessionIdClientCache == null) {
            sessionIdClientCache = new HashMap<>();
        }
        sessionIdClientCache.put(sessionId, socketIOClient);
        concurrentHashMap.put(userId, sessionIdClientCache);
    }


    //获取用户信息
    public HashMap<UUID, SocketIOClient> getUserClient(String userId) {
        return concurrentHashMap.get(userId);
    }


    //根据用户id和session删除用户某个session信息
    public void deleteSessionClientByUserId(String userId, UUID sessionId) {
        concurrentHashMap.get(userId).remove(sessionId);
    }


    //删除用户缓存信息
    public void deleteUserCacheByUserId(String userId) {
        concurrentHashMap.remove(userId);

    }

    /**
     * 建立连接的检测
     */
    public void preCheckConnect(String userId) {
        HashMap<UUID, SocketIOClient> userClientMap = getUserClient(userId);
        if (!userClientMap.isEmpty()) {
            Iterator<Map.Entry<UUID, SocketIOClient>> iterator = userClientMap.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<UUID, SocketIOClient> entry = iterator.next();
                SocketIOClient socketIOClient = entry.getValue();
                UUID sessionId = entry.getKey();
                if (Objects.nonNull(socketIOClient)) {
                    log.info("===preCheckConnect===userId:{},sessionId:{}", userId, sessionId);

                    //主动关闭连接
                    socketIOClient.disconnect();
                    //删除缓存
                    iterator.remove();
                }
            }
        }
    }

    /**
     * 断开连接
     */
    public void disconnectClient(String userId, UUID sessionId, SocketIOClient socketIOClient) {
        HashMap<UUID, SocketIOClient> userClientMap = getUserClient(userId);
        if (!userClientMap.isEmpty()) {
            log.info("===disconnectClient===userId:{},sessionId:{}", userId, sessionId);
            userClientMap.remove(sessionId);
            socketIOClient.disconnect();
        }
    }


}