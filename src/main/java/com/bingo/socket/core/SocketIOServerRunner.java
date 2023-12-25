package com.bingo.socket.core;

import com.bingo.socket.core.handler.UserHandler;
import com.corundumstudio.socketio.SocketIOServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * Description:
 * Author: xbb
 * Date: 2023/12/25
 */
@Component
@Slf4j
public class SocketIOServerRunner implements CommandLineRunner, DisposableBean {

    @Autowired
    private SocketIOServer socketIOServer;
    @Autowired
    private UserHandler userHandler;

    @Override
    public void run(String... args) throws Exception {
        //监听自定义Handler
        //namespace分别交给各自的Handler监听,这样就可以隔离，只有客户端指定namespace，才能访问对应Handler
        //根据实际业务添加
        socketIOServer.getNamespace("/user").addListeners(userHandler);

        socketIOServer.start();
        log.info("===SocketIOServerRunner===");
    }

    @Override
    public void destroy() throws Exception {
        socketIOServer.stop();
        log.warn("===socketIOServer destroy===");
    }


}
