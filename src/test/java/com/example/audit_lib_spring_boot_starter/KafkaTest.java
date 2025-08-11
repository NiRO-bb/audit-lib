package com.example.audit_lib_spring_boot_starter;

import com.example.audit_lib_spring_boot_starter.configs.TestConfig;
import com.example.audit_lib_spring_boot_starter.kafka.dto.KafkaAnnotationLog;
import com.example.audit_lib_spring_boot_starter.utils.KafkaConsumer;
import com.example.audit_lib_spring_boot_starter.utils.LogLevels;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.core.KafkaTemplate;

import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@SpringBootTest
@DirtiesContext
@EmbeddedKafka(partitions = 1, brokerProperties = {
        "listeners=PLAINTEXT://localhost:9094", "port=9094"
})
@Import(TestConfig.class)
public class KafkaTest {

    private final String topic = "test_topic";

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Autowired
    private KafkaConsumer kafkaConsumer;

    @Test
    public void testSendAnnotationSuccess() throws InterruptedException {
        KafkaAnnotationLog log = new KafkaAnnotationLog(
                LogLevels.INFO,
                "START",
                UUID.randomUUID().toString(),
                "Class.method",
                "{ \"field\":\"value\" }"
        );
        kafkaTemplate.send("test_topic", log);
        boolean isConsumed = kafkaConsumer.getLatch().await(10, TimeUnit.SECONDS);

        Assertions.assertTrue(isConsumed);
        Assertions.assertEquals("Class.method", kafkaConsumer.getLog().getMethodName());
    }

}
