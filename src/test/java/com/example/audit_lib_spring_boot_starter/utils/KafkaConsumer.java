package com.example.audit_lib_spring_boot_starter.utils;

import com.example.audit_lib_spring_boot_starter.kafka.dto.KafkaMethodLog;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.concurrent.CountDownLatch;

@Component
public class KafkaConsumer {

    private CountDownLatch latch = new CountDownLatch(1);

    private KafkaMethodLog log;

    @KafkaListener(topics = "test_topic")
    public void listener(ConsumerRecord<String, Object> record) {
        log = (KafkaMethodLog) record.value();
        latch.countDown();
    }

    public void resetLatch() {
        latch = new CountDownLatch(1);
    }

    public CountDownLatch getLatch() {
        return latch;
    }

    public KafkaMethodLog getLog() {
        return log;
    }

}
