package com.bingo.socket.core;

import com.bingo.socket.core.cache.SocketClientCache;
import com.bingo.socket.core.cache.UserStatusCache;
import com.bingo.socket.enums.UserStatusEnum;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * Description: 操作socket公共模板
 * Author: xbb
 * Date: 2023/12/25
 */
@Slf4j
@Component
public class SocketTemplate {

    @Autowired
    private SocketIOServer socketIOServer;
    @Autowired
    private SocketClientCache socketClientCache;
    @Autowired
    private UserStatusCache userStatusCache;

    /**
     * 设置在线用户
     */
    public boolean setUserStatus(String userId, UserStatusEnum userStatusEnum, SocketIOClient socketIOClient, UUID sessionId) {

        if (Objects.isNull(userStatusEnum)) {
            return false;
        }

        switch (userStatusEnum) {
            case ESTABLISH_CONNECTION:
                //检测用户是否有其他连接
                socketClientCache.preCheckConnect(userId);
                //建立连接
                socketClientCache.saveClient(userId, sessionId, socketIOClient);
                //设置用户的状态-在线
                userStatusCache.establishConnection(userId, userStatusEnum.getStatus());
                break;
            case HEARTBEAT_CONNECTION:
                //建立心跳
                userStatusCache.heartbeatConnection(userId, userStatusEnum.getStatus());
                break;
            case DISCONNECT:
                //断开连接
                socketClientCache.disconnectClient(userId, sessionId, socketIOClient);
                userStatusCache.disconnect(userId, userStatusEnum.getStatus(), socketIOClient, sessionId);
                break;
            case HEARTBEAT_TIMEOUT:
                //心跳超时
                userStatusCache.heartbeatTimeout(userId, userStatusEnum.getStatus());
                break;
            default:
                break;
        }
        return false;
    }

    /**
     * 获取所有在线用户
     */
    public Map<String, Integer> getOnlineUsers() {
        return userStatusCache.getOnlineUsers();
    }

    public Integer getOnlineUser(String userId) {
        return userStatusCache.getOnlineUser(userId);
    }

    /**
     * 单发消息
     */
    public boolean sendMsgOnlineOneUser(String toUserId, String content) {
        Map<UUID, SocketIOClient> userClientMap = socketClientCache.getUserClient(toUserId);
        if (!userClientMap.isEmpty()) {
            for (UUID sessionId : userClientMap.keySet()) {
                try {
                    SocketIOClient socketIOClient = userClientMap.get(sessionId);
                    log.info("==sendMsgOnlineOneUser===sessionId:{},content:{}", sessionId, content);

                    //单发,event自定义为message
                    socketIOClient.sendEvent("message", content);
                } catch (Exception e) {
                    log.error("==sendMsgOnlineOneUserError===sessionId" + sessionId + ",content" + content + "===e:" + e);
                }
            }
            return true;
        }
        return false;
    }

    /**
     * 广播消息
     */
    public boolean sendMsgBroadcast(String toUserId, String content) {
        Map<UUID, SocketIOClient> userClientMap = socketClientCache.getUserClient(toUserId);
        if (!userClientMap.isEmpty()) {
            for (UUID sessionId : userClientMap.keySet()) {
                try {
                    log.info("==sendMsgBroadcast===sessionId:{},content:{}", sessionId, content);

                    //广播，event自定义为message
                    socketIOServer.getBroadcastOperations().sendEvent("message", content);
                } catch (Exception e) {
                    log.error("==sendMsgBroadcastError===sessionId" + sessionId + ",content" + content + "===e:" + e);
                }
            }
            return true;
        }
        return false;
    }

}