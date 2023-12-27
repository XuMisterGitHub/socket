package com.bingo.socket.web;

import com.bingo.socket.core.params.SendMsgParam;
import com.bingo.socket.service.ISocketService;
import com.bingo.socket.vo.UserStatusVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Description: 外部查询用户在线数据
 * Author: xbb
 * Date: 2023/12/26
 */
@RestController
@RequestMapping("/api/socket")
public class SocketApiController {

    @Autowired
    private ISocketService socketService;

    /**
     * 获取所有在线用户数据
     */
    @GetMapping("/onlineUsers")
    public Result<List<UserStatusVo>> getOnlineUsers() {
        return Result.success(socketService.getOnlineUsers());
    }

    /**
     * 获取某个在线用户数据
     */
    @GetMapping("/onlineOneUser/{userId}")
    public Result<UserStatusVo> onlineOneUser(@PathVariable String userId) {
        return Result.success(socketService.getOnlineOneUser(userId));
    }

    /**
     * 获取某个房间的在线用户
     */
    @GetMapping("/onlineOneNameSpaceUsers/{namespaceId}")
    public Result<List<UserStatusVo>> onlineOneNameSpaceUsers(@PathVariable String namespaceId) {
        return Result.success(socketService.getOnlineUsersByNameSpace(namespaceId));
    }


    /**
     * 给所有在线用户发消息
     */
    @PostMapping("/sendMsgOnlineUsers")
    public Result<Boolean> sendMsgOnlineUsers(@RequestBody SendMsgParam sendMsgParam) {
        return Result.success(socketService.sendMsgOnlineUsers(sendMsgParam));
    }

    /**
     * 给指定在线用户发消息
     */
    @PostMapping("/sendMsgOnlineOneUser")
    public Result<Boolean> sendMsgOnlineOneUser(@RequestBody SendMsgParam sendMsgParam) {
        return Result.success(socketService.sendMsgOnlineOneUser(sendMsgParam));
    }

    /**
     * 给指定房间的在线用户发消息
     */
    @PostMapping("/sendMsgOnlineOneNameSpaceUsers/{namespaceId}")
    public Result<Boolean> sendMsgOnlineOneNameSpaceUsers(@PathVariable String namespaceId) {
        return Result.success(socketService.sendMsgOnlineOneNameSpaceUsers(namespaceId));
    }

}