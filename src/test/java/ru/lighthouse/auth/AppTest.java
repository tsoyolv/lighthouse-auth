package ru.lighthouse.auth;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static ru.lighthouse.auth.App.HEALTH_RESPONSE;
import static ru.lighthouse.auth.App.HEALTH_URI;

@SpringBootTest(classes = App.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class AppTest {

    @Autowired
    private MockMvc mvc;

    @Test
    public void helloGradle() throws Exception {
        mvc.perform(get(HEALTH_URI))
                .andExpect(status().isOk())
                .andExpect(content().string(HEALTH_RESPONSE));
    }
}