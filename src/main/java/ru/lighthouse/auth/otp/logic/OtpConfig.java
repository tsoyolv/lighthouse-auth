package ru.lighthouse.auth.otp.logic;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class OtpConfig {
    @Value("${otp.uri}")
    private String uri;

    @Value("${otp.prolongation.seconds}")
    private int prolongationSeconds;

    @Value("${sms.message.pattern}")
    private String smsLoginPattern;

    @Value("${otp.default.password.enabled}")
    private boolean defaultPasswordEnabled;

    @Value("${otp.default.password.value}")
    private String defaultPassword;

    @Value("${otp.next-otp.timeout}")
    private int nextOtpTimeout;
}
