package com.bingo.socket.core;

import com.bingo.socket.enums.UserStatusEnum;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.annotation.OnConnect;
import com.corundumstudio.socketio.annotation.OnDisconnect;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * 用于监听客户端的建立连接请求和关闭连接请求
 */
@Slf4j
@Component
public class SocketIOServerHandler {

    @Autowired
    private SocketTemplate socketTemplate;

    /**
     * 建立连接
     *
     * @param client 客户端的SocketIO
     */
    @OnConnect
    public void onConnect(SocketIOClient client) {
        //客户端请求 http://localhost:端口号/命名空间?userId=12345
        String userId = client.getHandshakeData().getSingleUrlParam("userId");
        if (StringUtils.isEmpty(userId)) {
            log.warn("===SocketIOServerHandler connectFail===");
            return;
        }
        //同一个页面session一样的
        UUID sessionId = client.getSessionId();
        //用户建立连接
        boolean setUserStatus = socketTemplate.setUserStatus(userId, UserStatusEnum.ESTABLISH_CONNECTION, client, sessionId);

        log.info("===SocketIOServerHandler connectSuccess===userId:{},sessionId:{},result:{}", userId, sessionId, setUserStatus);
    }

    /**
     * 关闭连接
     *
     * @param client 客户端的SocketIO
     */
    @OnDisconnect
    public void onDisconnect(SocketIOClient client) {
        String userId = client.getHandshakeData().getSingleUrlParam("userId");
        UUID sessionId = client.getSessionId();
        boolean setUserStatus = socketTemplate.setUserStatus(userId, UserStatusEnum.DISCONNECT, client, sessionId);

        log.info("===SocketIOServerHandler disconnect===userId:{},sessionId:{},result:{}", userId, sessionId, setUserStatus);
    }


}