package com.example.audit_lib_spring_boot_starter;

import com.example.audit_lib_spring_boot_starter.aspects.AnnotationLogAspect;
import com.example.audit_lib_spring_boot_starter.utils.LoggingUtil;
import org.aspectj.lang.ProceedingJoinPoint;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;

@SpringBootTest
@DirtiesContext
@EmbeddedKafka(partitions = 1, brokerProperties = {
        "listeners=PLAINTEXT://localhost:9094", "port=9094"
})
public class AspectTest {

    private final ProceedingJoinPoint joinPoint = Mockito.mock(ProceedingJoinPoint.class);

    @Autowired
    private AnnotationLogAspect aspect;

    @Test
    public void testAdviceSuccess() throws Throwable {
        try (MockedStatic<LoggingUtil> util = Mockito.mockStatic(LoggingUtil.class)) {
            Mockito.when(joinPoint.proceed()).thenReturn(new Object());
            Mockito.when(joinPoint.getArgs()).thenReturn(new Object[] {});
            util.when(() -> LoggingUtil.getMethodName(joinPoint)).thenReturn("");
            Assertions.assertDoesNotThrow(() -> aspect.adviceAnnotation(joinPoint));
        }
    }

    @Test
    public void testAdviceFailure() throws Throwable {
        Mockito.when(joinPoint.proceed()).thenThrow(new Throwable());
        Assertions.assertThrows(Throwable.class, () -> aspect.adviceAnnotation(joinPoint));
    }

}
