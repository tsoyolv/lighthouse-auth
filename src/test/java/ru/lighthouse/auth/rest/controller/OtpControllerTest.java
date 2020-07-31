package ru.lighthouse.auth.rest.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockserver.client.MockServerClient;
import org.mockserver.matchers.MatchType;
import org.mockserver.model.MediaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import ru.lighthouse.auth.App;
import ru.lighthouse.auth.integration.dto.AuthorityDto;
import ru.lighthouse.auth.integration.dto.UserDto;
import ru.lighthouse.auth.otp.logic.OtpConfig;
import ru.lighthouse.auth.security.JWTService;

import java.util.Collections;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockserver.integration.ClientAndServer.startClientAndServer;
import static org.mockserver.matchers.Times.exactly;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;
import static org.mockserver.model.JsonBody.json;
import static org.mockserver.model.StringBody.exact;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.lighthouse.auth.rest.controller.CheckAuthController.CHECK_AUTH_URI;


@SpringBootTest(classes = App.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class OtpControllerTest {
    private static final String DEFAULT_PHONE = "79779873676";

    @Autowired
    private MockMvc mvc;
    @Autowired
    private JWTService jwtService;
    @Autowired
    private OtpConfig otpConfig;

    @Test
    public void testAuthorizedUser() throws Exception {
        mvc.perform(post(otpConfig.getUri()).param("phoneNumber", DEFAULT_PHONE)).andExpect(status().isOk());
        String token = mvc.perform(post(jwtService.getAuthUri()).param("phoneNumber", DEFAULT_PHONE).param("otp", "1234"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getHeader(jwtService.getHeader());
        assertNotNull(token);
        assertTrue(token.contains(jwtService.getPrefix()));
    }

    @Test
    public void testUnauthorizedUser() throws Exception {
        mvc.perform(get(CHECK_AUTH_URI)).andExpect(status().isUnauthorized());
    }

    @Test
    public void testAuthorizedWithWrongHeaderUser() throws Exception {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(jwtService.getHeader(), jwtService.getPrefix() + "wrongheader");
        MockHttpServletRequestBuilder requestBuilder = get(CHECK_AUTH_URI).headers(httpHeaders);
        mvc.perform(requestBuilder).andExpect(status().isUnauthorized());
    }

    private static MockServerClient mockServer;

    @BeforeAll
    private static void startMockServer() throws JsonProcessingException {
        UserDto userDto = new UserDto(DEFAULT_PHONE, Collections.singleton(new AuthorityDto("IOS", "ROLE_IOS")));
        userDto.setId(1L);
        final String response = new ObjectMapper().writeValueAsString(userDto);
        mockServer = startClientAndServer(8004);
        mockServer.when(
                request()
                        .withMethod("POST")
                        .withPath("/crm-backend/integration/user")
                        .withBody(json("{\"id\":null,\"authorities\":null,\"phoneNumber\":\"79779873676\",\"enabled\":true,\"accountNonLocked\":true,\"firstName\":null,\"secondName\":null,\"lastName\":null,\"birthDate\":null}"
                                , MatchType.ONLY_MATCHING_FIELDS)),
                exactly(1))
                .respond(
                        response()
                                .withStatusCode(HttpStatus.OK.value())
                                .withBody(exact(response, MediaType.APPLICATION_JSON))
                                .withDelay(TimeUnit.SECONDS, 1)
                );
    }

    @AfterAll
    private static void stopMockServer() {
        mockServer.stop();
    }
}