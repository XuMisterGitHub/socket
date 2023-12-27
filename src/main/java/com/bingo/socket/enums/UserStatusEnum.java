package com.bingo.socket.enums;

public enum UserStatusEnum {

    ESTABLISH_CONNECTION(1, "建立连接"),
    HEARTBEAT_CONNECTION(1, "建立心跳"),
    DISCONNECT(0, "断开连接"),
    HEARTBEAT_TIMEOUT(-1, "心跳超时"),

    ;
    private Integer status;
    private String desc;

    UserStatusEnum(Integer status, String desc) {
        this.status = status;
        this.desc = desc;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }


}