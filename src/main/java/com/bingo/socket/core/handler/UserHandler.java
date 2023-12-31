package com.bingo.socket.core.handler;

import com.bingo.socket.core.message.MessageDataModel;
import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.annotation.OnEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.Set;

/**
 * Description: 用户基础功能
 * Author: xbb
 * Date: 2023/12/25
 */
@Slf4j
@Component
public class UserHandler {

    @Autowired
    private SocketIOServer socketIOServer;

    @Value("${socketio.defaultRoomId}")
    private String defaultRoomId;

    /**
     * 客户端登录事件
     */
    @OnEvent("login")
    public void userLogin(SocketIOClient client, MessageDataModel data, AckRequest ackRequest) throws JsonProcessingException {
        //默认房间ID为大群
        if (Objects.nonNull(data) && StringUtils.isEmpty(data.getRoomId())) {
            data.setRoomId(defaultRoomId);
        }
        client.joinRoom(data.getRoomId());
        log.info("===userLogin===data:{}", data);
        if (ackRequest.isAckRequested()) {
            ackRequest.sendAckData("userLogin ask", data.getContent());
        }
    }

    /**
     * 客户端登出事件
     */
    @OnEvent("logout")
    public void userLogout(SocketIOClient client, MessageDataModel data, AckRequest ackRequest) throws JsonProcessingException {
        //默认房间ID为大群
        if (Objects.nonNull(data) && StringUtils.isEmpty(data.getRoomId())) {
            data.setRoomId(defaultRoomId);
        }
        client.leaveRoom(data.getRoomId());
        log.info("===userLogout===data:{}", data);
        if (ackRequest.isAckRequested()) {
            ackRequest.sendAckData("userLogout ask", data.getContent());
        }
    }

    /**
     * 接收客户端传的心跳包
     */
    @OnEvent("heartbeat")
    public void heartbeat(SocketIOClient client, MessageDataModel data, AckRequest ackRequest) throws JsonProcessingException {
        log.info("===heartbeat===data:{}", data);

        //更新用户收到的心跳包失败次数重置为0

        if (ackRequest.isAckRequested()) {
            ackRequest.sendAckData("heartbeat ask", data.getContent());
        }
    }


    @OnEvent("sendRoomMessage")
    public void sendRoomMessage(SocketIOClient client, MessageDataModel data, AckRequest ackRequest) throws JsonProcessingException {

        log.info("===sendRoomMessage===data:{}", data);

        String userId = client.getHandshakeData().getSingleUrlParam("userId");

        Set<String> allRooms = client.getAllRooms();

        for (String room : allRooms) {
            log.info("房间：{}", room);
            //发送给指定空间名称以及房间的人，并且排除不发给自己
            socketIOServer.getNamespace("/user").getRoomOperations(room).sendEvent("message", client, data);

            //发送给指定空间名称以及房间的人，包括自己
            //socketIoServer.getNamespace("/user").getRoomOperations(room).sendEvent("message", data);;
        }

        if (ackRequest.isAckRequested()) {
            //返回给客户端，说我接收到了
            ackRequest.sendAckData("发送消息到指定的房间", "成功");
        }
    }

    @OnEvent("sendNamespaceMessage")
    public void sendNamespaceMessage(SocketIOClient client, MessageDataModel data, AckRequest ackRequest) throws JsonProcessingException {

        socketIOServer.getNamespace("/user").getBroadcastOperations().sendEvent("message", client, data);

        if (ackRequest.isAckRequested()) {
            //返回给客户端，说我接收到了
            ackRequest.sendAckData("发送消息到指定的房间", "成功");
        }
    }


}
