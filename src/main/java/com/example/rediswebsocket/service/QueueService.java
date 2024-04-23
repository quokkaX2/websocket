package com.example.rediswebsocket.service;

import com.example.rediswebsocket.domain.dto.RedisQueueResponseDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@Slf4j(topic = "대기열 서비스")
@RequiredArgsConstructor
public class QueueService {
    private final String ADD_QUEUE = "queue";
    private final String SUCCESS = "success";
    private final String FAIL = "fail";

    private final String SUCCESS_MESSAGE = "true";
    private final String FAIL_MESSAGE = "false";

    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;
    private final SimpMessageSendingOperations simpMessageSendingOperations;
    private final int FIRST_INDEX = 0;
    private final int LAST_INDEX = -1;

    //실시간으로 프론트에게 대기열의 상태를 전달함
    //스케줄러를 통해 5초에 한번씩 대기열의 상태를 전달함
    public void getQueue() {
        Set<String> queue = redisTemplate.opsForZSet().range(ADD_QUEUE, FIRST_INDEX, LAST_INDEX);
        assert queue != null;
        queue.parallelStream().forEach(info -> {
            Long rank = redisTemplate.opsForZSet().rank(ADD_QUEUE, info);
            try {
                RedisQueueResponseDto redisQueueResponseDto = objectMapper.readValue(info, RedisQueueResponseDto.class);
                assert rank != null;
                simpMessageSendingOperations.convertAndSend("/sub/" + redisQueueResponseDto.getStudentId(), rank);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        });
    }

    //실시간으로 프론트에게 대기열의 결과를 전달함
    //스케줄러를 통해 0.1 ~ 1초에 한번씩 신청 결과를 전달함
    public void getSucessResult() {
        Set<String> sucessQueue = redisTemplate.opsForZSet().range(SUCCESS, FIRST_INDEX, LAST_INDEX);
        assert sucessQueue != null;
        sucessQueue.parallelStream().forEach(info -> {
            redisTemplate.opsForZSet().remove(SUCCESS, info);
            try {
                RedisQueueResponseDto redisQueueResponseDto = objectMapper.readValue(info, RedisQueueResponseDto.class);
                simpMessageSendingOperations.convertAndSend("/sub/" + redisQueueResponseDto.getStudentId(), SUCCESS_MESSAGE);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        });

    }

    public void getFailResult() {
        Set<String> failQueue = redisTemplate.opsForZSet().range(FAIL, FIRST_INDEX, LAST_INDEX);
        assert failQueue != null;
        failQueue.parallelStream().forEach(info -> {
            redisTemplate.opsForZSet().remove(FAIL, info);
            try {
                RedisQueueResponseDto redisQueueResponseDto = objectMapper.readValue(info, RedisQueueResponseDto.class);
                simpMessageSendingOperations.convertAndSend("/sub/" + redisQueueResponseDto.getStudentId(), FAIL_MESSAGE);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        });
    }

}
