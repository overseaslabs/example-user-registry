package com.overseaslabs.examples.ureg;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.IOException;

import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class RedisMessagePublisherTest {
    @MockBean
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private RedisMessagePublisher publisher;

    @Configuration
    static class Config {

        @Bean
        RedisMessagePublisher get() {
            return new RedisMessagePublisher();
        }

        @Bean("web")
        ChannelTopic webTopic() {
            return new ChannelTopic("example:web");
        }
    }

    @Test
    void testPublish() throws IOException {
        Object o = new Object();

        publisher.publish(o);

        verify(redisTemplate, times(1)).convertAndSend(anyString(), eq(o));
    }
}
