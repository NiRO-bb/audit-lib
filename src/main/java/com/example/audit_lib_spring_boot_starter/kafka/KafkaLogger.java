package com.example.audit_lib_spring_boot_starter.kafka;

import com.example.audit_lib_spring_boot_starter.kafka.dto.KafkaAnnotationLog;
import com.example.audit_lib_spring_boot_starter.kafka.dto.KafkaHttpLog;
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

    private static String topic;

    private static KafkaTemplate<String, Object> kafkaTemplate;

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
     * Sets KafkaTemplate for sending messages and topic where messages will be sent.
     *
     * @param kafkaTemplate
     * @param topic
     */
    public static void setKafkaTemplate(KafkaTemplate<String, Object> kafkaTemplate, String topic) {
        KafkaLogger.kafkaTemplate = kafkaTemplate;
        KafkaLogger.topic = topic;
    }

    @Override
    public void append(LogEvent event) {
        Object log = getLog(event);
        kafkaTemplate.send(topic, log);
    }

    /**
     * Returns Annotation/Http log instance depends on logger name.
     *
     * @param event
     * @return
     */
    private Object getLog(LogEvent event) {
        return switch (event.getLoggerName()) {
            case "HttpLogger" -> httpLog(event);
            case "AnnotationLogger" -> annotationLog(event);
            default -> null;
        };
    }

    /**
     * Assembles message for Kafka from passed LogEvent.
     */
    private KafkaAnnotationLog annotationLog(LogEvent event) {
        Object[] params = event.getMessage().getParameters();
        return new KafkaAnnotationLog(
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
    private KafkaHttpLog httpLog(LogEvent event) {
        Object[] params = event.getMessage().getParameters();
        return new KafkaHttpLog(
                (String) params[0],
                (String) params[1],
                (Integer) params[2],
                (String) params[3],
                (String) params[4],
                (String) params[5]
        );
    }

}
