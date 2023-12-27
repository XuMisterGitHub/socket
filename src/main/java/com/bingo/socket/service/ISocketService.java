package com.bingo.socket.service;

import com.bingo.socket.core.params.SendMsgParam;
import com.bingo.socket.vo.UserStatusVo;

import java.util.List;

public interface ISocketService {


    List<UserStatusVo> getOnlineUsers();

    UserStatusVo getOnlineOneUser(String userId);

    List<UserStatusVo> getOnlineUsersByNameSpace(String namespaceId);

    Boolean sendMsgOnlineUsers(SendMsgParam sendMsgParam);

    Boolean sendMsgOnlineOneUser(SendMsgParam sendMsgParam);

    Boolean sendMsgOnlineOneNameSpaceUsers(String namespaceId);

}
