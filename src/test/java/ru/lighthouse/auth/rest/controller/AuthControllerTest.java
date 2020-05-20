package ru.lighthouse.auth.rest.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import ru.lighthouse.auth.App;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = App.class)
@AutoConfigureMockMvc
public class AuthControllerTest {

    @Autowired
    private MockMvc mvc;

    @Test
    public void testAuthorizedUser() throws Exception {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("X-AUTH-TOKEN", "tsoyolv");
        MockHttpServletRequestBuilder requestBuilder = get("/api").headers(httpHeaders);
        mvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(content().string("Api"));
    }

    @Test
    public void testUnauthorizedUser() throws Exception {
        mvc.perform(get("/api"))
                .andExpect(status().isForbidden());
    }

    @Test
    public void testAuthorizedWithWrongHeaderUser() throws Exception {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("X-AUTH-TOKEN", "wrongheader");
        MockHttpServletRequestBuilder requestBuilder = get("/api").headers(httpHeaders);
        mvc.perform(requestBuilder)
                .andExpect(status().isForbidden());
    }
}