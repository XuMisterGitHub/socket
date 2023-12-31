package com.bingo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Description: socket程序启动类
 * Author: xbb
 * Date: 2023/12/25
 */
@SpringBootApplication
@EnableScheduling
public class SocketIoServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(SocketIoServerApplication.class, args);
    }
}
