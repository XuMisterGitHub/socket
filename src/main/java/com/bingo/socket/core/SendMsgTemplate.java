package com.bingo.socket.core;

import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;
import java.util.UUID;

/**
 * Description:
 * Author: xbb
 * Date: 2023/12/25
 */
public class SendMsgTemplate {

    @Autowired
    private SocketIOServer socketIOServer;

    @Autowired
    private ClientCache clientCache;

    public void sendMessageOne(String userId, String namespace) throws JsonProcessingException {
        Map<UUID, SocketIOClient> userClient = clientCache.getUserClient(userId);
        for (UUID sessionId : userClient.keySet()) {
            socketIOServer.getNamespace("/" + namespace).getClient(sessionId).sendEvent("message", "这是点对点发送");
        }
    }

}