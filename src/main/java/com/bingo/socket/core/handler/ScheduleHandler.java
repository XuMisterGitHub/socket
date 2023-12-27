package com.bingo.socket.core.handler;

import com.bingo.socket.config.SocketIOConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 定时任务
 */
@Component
@Slf4j
public class ScheduleHandler {

    @Autowired
    private SocketIOConfig config;

    private ExecutorService executorService;
    private int delay = 0;
    private int threads = 0;

    /**
     * 心跳检测，长时间未收到心跳断开连接
     */
    @Scheduled(cron = "*/1 * * * * ?")
    public void heartbeat() {
//        long begin = System.currentTimeMillis();
//        log.info("heartbeat...");
//        for (int i = 0; i < 1_0000; i++) {
//            log.info("i = " + i);
//        }
//        log.info("heartbeat..." + (System.currentTimeMillis() - begin));

//        List<String> dataList = Arrays.asList("apple", "banana", "orange", "grape", "melon");
//
//        // 使用并行流处理
//        dataList.parallelStream().forEach(item -> {
//            // 在这里执行你的处理逻辑
//            System.out.println("Thread: " + Thread.currentThread().getName() + ", Item: " + item);
//        });

        if (++delay < config.scheduleHeartbeatDelay) return;

        delay = 0;

        List<String> dataList = Arrays.asList("apple", "banana", "orange", "grape", "melon", "cherry", "pear", "kiwi");

        // 定义线程池
//        ExecutorService executorService = Executors.newFixedThreadPool(config.scheduleHeartbeatThreadPool);

        int batchSize = config.scheduleHeartbeatBatchSize;
        if (batchSize < 1) {
            batchSize = 100;
        }
        int dataSize = dataList.size();

        int newThreads = dataSize / batchSize + 1;
        if (newThreads != threads) {
            threads = newThreads;
            executorService = Executors.newFixedThreadPool(threads);
        }

        for (int i = 0; i < dataSize; i += batchSize) {
            final int startIndex = i;
            final int endIndex = Math.min(i + batchSize, dataSize);

            // 提交任务到线程池
            executorService.submit(() -> {
                for (int j = startIndex; j < endIndex; j++) {
                    // 在这里执行你的处理逻辑
                    log.info("Thread: " + Thread.currentThread().getName() + ", Item: " + dataList.get(j));
                    // TODO 判断是否超时，已超时处理逻辑
                }
            });
        }

        // 关闭线程池
        executorService.shutdown();
    }
}
