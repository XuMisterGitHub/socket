package com.bingo.socket.config;

import com.corundumstudio.socketio.SocketConfig;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.annotation.SpringAnnotationScanner;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.Optional;

@Configuration
public class SocketIOConfig {

    @Value("${socketio.host}")
    private String host;

    @Value("${socketio.port}")
    private Integer port;

    @Value("${socketio.bossCount}")
    private int bossCount;

    @Value("${socketio.workCount}")
    private int workCount;

    @Value("${socketio.allowCustomRequests}")
    private boolean allowCustomRequests;

    @Value("${socketio.upgradeTimeout}")
    private int upgradeTimeout;

    @Value("${socketio.pingTimeout}")
    private int pingTimeout;

    @Value("${socketio.pingInterval}")
    private int pingInterval;

    @Value("${socketio.namespaces}")
    private String[] namespaces;

    @Value("${schedule.heartbeat.delay}")
    public int scheduleHeartbeatDelay;

    @Value("${schedule.heartbeat.batchSize}")
    public int scheduleHeartbeatBatchSize;

    @Bean
    public SocketIOServer socketIOServer() {
        SocketConfig socketConfig = new SocketConfig();
        socketConfig.setTcpNoDelay(true);
        socketConfig.setSoLinger(0);
        com.corundumstudio.socketio.Configuration config = new com.corundumstudio.socketio.Configuration();
        config.setSocketConfig(socketConfig);
        config.setHostname(host);
        config.setPort(port);
        config.setBossThreads(bossCount);
        config.setWorkerThreads(workCount);
        config.setAllowCustomRequests(allowCustomRequests);
        config.setUpgradeTimeout(upgradeTimeout);
        config.setPingTimeout(pingTimeout);
        config.setPingInterval(pingInterval);

        //鉴权
        config.setAuthorizationListener(handshakeData -> {
            // 获取socket链接发来的token参数
            String token = handshakeData.getSingleUrlParam("token");
            // 如果验签通过就是true,否则false, false的话就不允许建立socket链接
            return true;
        });


        //服务端
        final SocketIOServer server = new SocketIOServer(config);

        //添加命名空间（如果你不需要命名空间，下面的代码可以去掉）
        Optional.ofNullable(namespaces).ifPresent(nss ->
                Arrays.stream(nss).forEach(server::addNamespace));


        return server;
    }

    //这个对象是用来扫描socketio的注解，比如 @OnConnect、@OnEvent
    @Bean
    public SpringAnnotationScanner springAnnotationScanner() {

        return new SpringAnnotationScanner(socketIOServer());
    }
}


