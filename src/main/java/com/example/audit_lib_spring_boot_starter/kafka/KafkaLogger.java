package com.example.audit_lib_spring_boot_starter.kafka;

import com.example.audit_lib_spring_boot_starter.kafka.dto.KafkaMethodLog;
import com.example.audit_lib_spring_boot_starter.kafka.dto.KafkaRequestLog;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.Core;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.springframework.kafka.core.KafkaTemplate;

/**
 * Manages log writing to Kafka topic.
 */
@Plugin(
        name = "KafkaLogger",
        category = Core.CATEGORY_NAME,
        elementType = Appender.ELEMENT_TYPE)
public class KafkaLogger extends AbstractAppender {

    private static String methodTopic;

    private static String requestTopic;

    private static KafkaTemplate<String, Object> kafkaTemplate;

    private final String methodLogger = "AnnotationLogger";

    private final String requestLogger = "HttpLogger";

    protected KafkaLogger(String name, Filter filter) {
        super(name, filter, null, false, null);
    }

    @PluginFactory
    public static KafkaLogger createAppender(
            @PluginAttribute("name") String name,
            @PluginElement("Filter") Filter filter) {
        return new KafkaLogger(name, filter);
    }

    /**
     * Sets KafkaTemplate for sending messages.
     *
     * @param kafkaTemplate
     */
    public static void setKafkaTemplate(KafkaTemplate<String, Object> kafkaTemplate) {
        KafkaLogger.kafkaTemplate = kafkaTemplate;
    }

    /**
     * Sets names of used topics.
     *
     * @param methodTopic
     * @param requestTopic
     */
    public static void setKafkaTopics(String methodTopic, String requestTopic) {
        KafkaLogger.methodTopic = methodTopic;
        KafkaLogger.requestTopic = requestTopic;
    }

    @Override
    public void append(LogEvent event) {
        String topic = getTopic(event.getLoggerName());
        if (topic != null) {
            kafkaTemplate.send(topic, getLog(event));
        }
    }

    /**
     * Maps name of called logger with name of Kafka topic where message will be sent.
     *
     * @param logger
     * @return name of Kafka topic
     */
    private String getTopic(String logger) {
        return switch(logger) {
            case methodLogger -> methodTopic;
            case requestLogger -> requestTopic;
            default -> null;
        };
    }

    /**
     * Returns Annotation/Http log instance depends on logger name.
     *
     * @param event
     * @return KafkaRequestLog or KafkaMethodLog instance depends on name of used logger.
     */
    private Object getLog(LogEvent event) {
        return switch (event.getLoggerName()) {
            case requestLogger -> httpLog(event);
            case methodLogger -> annotationLog(event);
            default -> null;
        };
    }

    /**
     * Assembles message for Kafka from passed LogEvent.
     */
    private KafkaMethodLog annotationLog(LogEvent event) {
        Object[] params = event.getMessage().getParameters();
        return new KafkaMethodLog(
                event.getLevel().toString(),
                (String) params[0],
                (String) params[1],
                (String) params[2],
                (String) params[3]
        );
    }

    /**
     * Assembles message for Kafka from passed LogEvent.
     */
    private KafkaRequestLog httpLog(LogEvent event) {
        Object[] params = event.getMessage().getParameters();
        return new KafkaRequestLog(
                (String) params[0],
                (String) params[1],
                (Integer) params[2],
                (String) params[3],
                (String) params[4],
                (String) params[5]
        );
    }

}
