package com.example.audit_lib_spring_boot_starter;

import com.example.audit_lib_spring_boot_starter.controllers.TestController;
import com.example.audit_lib_spring_boot_starter.kafka.KafkaLogger;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.RestClient;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestController.class)
@DirtiesContext
@EmbeddedKafka(partitions = 1, brokerProperties = {
        "listeners=PLAINTEXT://localhost:9094", "port=9094"
})
public class HttpRequestTest {

    @Autowired
    private RestClient restClient;

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private KafkaLogger logger;

    @Test
    public void testIncomingLoggingSuccess() throws Exception {
        mockMvc.perform(get("/test")).andExpect(status().isOk());
        Mockito.verify(logger).log(
                any(String.class), any(String.class), any(Integer.class), any(String.class), any(String.class), any(String.class));
    }

    @Test
    public void testOutgoingLoggingSuccess() {
        restClient.get().uri("https://httpbin.org/get").retrieve().body(String.class);
        Mockito.verify(logger).log(
                any(String.class), any(String.class), any(Integer.class), any(String.class), any(String.class), any(String.class));
    }

}
