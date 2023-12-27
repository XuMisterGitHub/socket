package com.bingo.socket.core.params;

import lombok.Data;

@Data
public class SendMsgParam {
    /**
     * 接收消息用户ID
     */
    private String toUserId;

    private String content;

}
