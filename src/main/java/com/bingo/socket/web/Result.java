package com.bingo.socket.web;

import com.bingo.socket.enums.ResultCodeEnum;
import lombok.Data;

@Data
public  class Result<T> {

    private Integer code;

    private String message;

    private  T data;
    private Result(){
        this.code = 200;
        this.message = "ok";
        this.data = null;
    }
    private Result(ResultCodeEnum resultCodeEnum, T data){
        this.code = resultCodeEnum.getCode();
        this.message = resultCodeEnum.getMessage();
        this.data = data;
    }

    /**
     * 成功
     */
    public static <E> Result success(E data){
        return new Result<E>(ResultCodeEnum.SUCCESS,data);
    }

    /**
     * 失败
     */
    public static <E> Result fail(E data){
        return new Result<E>(ResultCodeEnum.FAIL,data);
    }

}