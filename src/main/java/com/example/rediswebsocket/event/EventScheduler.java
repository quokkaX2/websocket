package com.example.rediswebsocket.event;

import com.example.rediswebsocket.service.QueueService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j(topic = "스케줄러")
@Component
@RequiredArgsConstructor
public class EventScheduler {
    private final QueueService queueService;

    @Scheduled(fixedDelay = 5000)
    public void periodicQueue() {
        log.info("대기열 가져오기");
        queueService.getQueue();
    }

    @Scheduled(fixedDelay = 5000)
    public void periodicSuccess() {
        log.info("성공 가져오기");
        queueService.getSucessResult();
    }

    @Scheduled(fixedDelay = 5000)
    public void periodicFail() {
        log.info("실패 가져오기");
        queueService.getFailResult();
    }


}
