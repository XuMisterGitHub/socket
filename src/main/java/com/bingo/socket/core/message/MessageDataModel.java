package com.bingo.socket.core.message;

import lombok.Data;
import org.springframework.stereotype.Component;

/**
 * Description: 消息模型
 * Author: xbb
 * Date: 2023/12/25
 */
@Data
@Component
public class MessageDataModel {

    //消息类型
    private String msgType;

    //消息内容
    private Byte[] msgContent;

}
