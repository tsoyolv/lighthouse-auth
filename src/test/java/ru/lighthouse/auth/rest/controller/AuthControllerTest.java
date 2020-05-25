package ru.lighthouse.auth.rest.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import ru.lighthouse.auth.App;
import ru.lighthouse.auth.security.JWTService;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.lighthouse.auth.Uri.CHECK_AUTH_URI;
import static ru.lighthouse.auth.Uri.OTP_URI;

@SpringBootTest(classes = App.class)
@AutoConfigureMockMvc
public class AuthControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private JWTService jwtService;

    @Test
    public void testAuthorizedUser() throws Exception {
        String defaultPhone = "79779873676";
        mvc.perform(post(OTP_URI).param("phoneNumber", defaultPhone))
                .andExpect(status().isOk());
        String defaultOtp = "1234";
        String token = mvc.perform(post(jwtService.getAuthUri()).param("phoneNumber", defaultPhone).param("otp", defaultOtp))
                .andExpect(status().isOk())
                .andReturn().getResponse().getHeader(jwtService.getHeader());
        assertNotNull(token);
        assertTrue(token.contains(jwtService.getPrefix()));
    }

    @Test
    public void testUnauthorizedUser() throws Exception {
        mvc.perform(get(CHECK_AUTH_URI))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testAuthorizedWithWrongHeaderUser() throws Exception {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(jwtService.getHeader(), jwtService.getPrefix() + "wrongheader");
        MockHttpServletRequestBuilder requestBuilder = get(CHECK_AUTH_URI).headers(httpHeaders);
        mvc.perform(requestBuilder)
                .andExpect(status().isUnauthorized());
    }
}