package com.bingo.socket.core;

import com.corundumstudio.socketio.SocketIOClient;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 用户的socket信息
 */
@Component
public class ClientCache {

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

}