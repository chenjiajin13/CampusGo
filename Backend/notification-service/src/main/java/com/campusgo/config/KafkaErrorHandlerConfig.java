package com.campusgo.config;

import org.apache.kafka.common.TopicPartition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.FixedBackOff;

@Configuration
public class KafkaErrorHandlerConfig {

    @Bean
    public DefaultErrorHandler kafkaErrorHandler(KafkaTemplate<Object, Object> template) {
        var backoff = new FixedBackOff(2000L, 5L);
        var recoverer = new DeadLetterPublishingRecoverer(
                template, (r, e) -> new TopicPartition(r.topic() + ".DLT", r.partition())
        );
        return new DefaultErrorHandler(recoverer, backoff);
    }
}
