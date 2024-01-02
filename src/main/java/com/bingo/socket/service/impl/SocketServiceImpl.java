package com.bingo.socket.service.impl;

import com.bingo.socket.core.SocketTemplate;
import com.bingo.socket.core.params.SendMsgParam;
import com.bingo.socket.service.ISocketService;
import com.bingo.socket.vo.UserStatusVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class SocketServiceImpl implements ISocketService {

    @Autowired
    private SocketTemplate socketTemplate;

    @Override
    public List<UserStatusVo> getOnlineUsers() {
        List<UserStatusVo> userStatusVoList = new ArrayList<>();
        Map<String, Integer> onlineUsersMap = socketTemplate.getOnlineUsers();
        if (!onlineUsersMap.isEmpty()) {
            userStatusVoList = onlineUsersMap.entrySet().stream().map(entry -> new UserStatusVo(entry.getKey(), entry.getValue())).collect(Collectors.toList());
        }
        return userStatusVoList;
    }

    @Override
    public UserStatusVo getOnlineOneUser(String userId) {
        UserStatusVo userStatusVo = new UserStatusVo();
        Integer status = socketTemplate.getOnlineUser(userId);
        userStatusVo.setUserId(userId);
        userStatusVo.setStatus(status);
        return userStatusVo;
    }

    @Override
    public List<UserStatusVo> getOnlineUsersByNameSpace(String namespaceId) {
        List<UserStatusVo> userStatusVoList = new ArrayList<>();
        return userStatusVoList;
    }

    @Override
    public Boolean sendMsgOnlineUsers(SendMsgParam sendMsgParam) {
        return true;
    }

    @Override
    public Boolean sendMsgOnlineOneUser(SendMsgParam sendMsgParam) {
        try {
            return socketTemplate.sendMsgOnlineOneUser(sendMsgParam.getToUserId(), sendMsgParam.getContent());
        } catch (Exception e) {
            log.error("===sendMsgToUserError===toUserId:" + sendMsgParam.getToUserId() + ",content:" + sendMsgParam.getContent());
            return false;
        }
    }

    @Override
    public Boolean sendMsgOnlineOneNameSpaceUsers(String namespaceId) {
        return true;
    }

}