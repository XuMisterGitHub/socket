package com.bingo.socket.core.cache;

import com.bingo.socket.enums.UserStatusEnum;
import com.corundumstudio.socketio.SocketIOClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Description: 用户状态
 * Author: xbb
 * Date: 2023/12/26
 */
@Slf4j
@Component
public class UserStatusCache {


    // 用户在线状态
    private static ConcurrentHashMap<String, Integer> onlineUserMap = new ConcurrentHashMap<>();

    /**
     * 建立连接
     */
    public void establishConnection(String userId, Integer status) {
        log.info("===establishConnection===userId:{}", userId);

        onlineUserMap.put(userId, status);
        //同步到缓存

        //同步上传日志

    }

    /**
     * 建立心跳
     */
    public void heartbeatConnection(String userId, Integer status) {
        onlineUserMap.put(userId, status);
        //同步到缓存

        //同步上传日志

    }

    /**
     * 断开连接
     */
    public void disconnect(String userId, Integer status, SocketIOClient socketIOClient, UUID sessionId) {
        log.info("===disconnect===userId:{},sessionId:{}", userId, sessionId);

        onlineUserMap.remove(userId);

        //同步用户在线状态

        //保存会话记录

        //断开连接
        socketIOClient.disconnect();
    }

    /**
     * 心跳超时
     */
    public void heartbeatTimeout(String userId, Integer status) {
        log.info("===heartbeatTimeout===userId:{}", userId);

        onlineUserMap.remove(userId);
        //同步到缓存

        //同步上传日志

    }

    public void deleteUser(String userId, Integer status) {
        log.info("===deleteUser===userId:{}", userId);

        onlineUserMap.remove(userId);

    }

    public Map<String, Integer> getOnlineUsers() {
        if (onlineUserMap == null) {
            onlineUserMap = new ConcurrentHashMap<>();
        }
        return onlineUserMap;
    }

    public Integer getOnlineUser(String userId) {
        if (!onlineUserMap.isEmpty() && onlineUserMap.contains(userId)) {
            return onlineUserMap.get(userId);
        }
        return UserStatusEnum.DISCONNECT.getStatus();
    }
}