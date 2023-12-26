package com.bingo.socket.core;

import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.fasterxml.jackson.core.JsonProcessingException;
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
@Component
public class SocketTemplate {

    @Autowired
    private SocketIOServer socketIOServer;
    @Autowired
    private ClientCache clientCache;
    @Autowired
    private UserStatusCache userStatusCache;

    /**
     * 给某个命名空间下的某个用户发消息
     */
    public void sendMessageOne(String userId, String namespace) throws JsonProcessingException {
        Map<UUID, SocketIOClient> userClient = clientCache.getUserClient(userId);
        for (UUID sessionId : userClient.keySet()) {
            socketIOServer.getNamespace("/" + namespace).getClient(sessionId).sendEvent("message", "这是点对点发送");
        }
    }

    /**
     * 设置在线用户
     */
    public boolean setUserStatus(String userId, UserStatusEnum userStatusEnum) {

        if (Objects.isNull(userStatusEnum)) {
            return false;
        }

        switch (userStatusEnum) {
            case ESTABLISH_CONNECTION:
                //建立连接
                userStatusCache.establishConnection(userId, userStatusEnum.getStatus());
                break;
            case HEARTBEAT_CONNECTION:
                //建立心跳
                userStatusCache.heartbeatConnection(userId, userStatusEnum.getStatus());
                break;
            case DISCONNECT:
                //断开连接
                userStatusCache.disconnect(userId, userStatusEnum.getStatus());
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

}