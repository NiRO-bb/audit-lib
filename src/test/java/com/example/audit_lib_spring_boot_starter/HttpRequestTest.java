package com.example.audit_lib_spring_boot_starter;

import com.example.audit_lib_spring_boot_starter.controllers.TestController;
import com.example.audit_lib_spring_boot_starter.interceptors.IncomingRequestFilter;
import com.example.audit_lib_spring_boot_starter.interceptors.OutgoingRequestInterceptor;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.RestClient;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestController.class)
@DirtiesContext
@EmbeddedKafka(partitions = 1, brokerProperties = {
        "listeners=PLAINTEXT://localhost:9094", "port=9094"
})
@ExtendWith(OutputCaptureExtension.class)
public class HttpRequestTest {

    @Autowired
    private RestClient restClient;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private IncomingRequestFilter incomingFilter;

    @Autowired
    private OutgoingRequestInterceptor outgoingFilter;

    @Test
    public void testIncomingLoggingSuccess() throws Exception {
        Logger logger = Mockito.mock(Logger.class);
        incomingFilter.setLogger(logger);
        mockMvc.perform(get("/test")).andExpect(status().isOk());
        Mockito.verify(logger).log(
                any(Level.class), anyString(), any(Object.class), any(Object.class), any(Object.class), any(Object.class), any(Object.class), any(Object.class)
        );
    }

    @Test
    public void testOutgoingLoggingSuccess(CapturedOutput output) {
        Logger logger = Mockito.mock(Logger.class);
        outgoingFilter.setLogger(logger);
        restClient.get().uri("https://httpbin.org/get").retrieve().body(String.class);
        Mockito.verify(logger).log(
                any(Level.class), anyString(), any(Object.class), any(Object.class), any(Object.class), any(Object.class), any(Object.class), any(Object.class)
        );
    }

}
