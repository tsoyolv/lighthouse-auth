package ru.lighthouse.auth.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:sms.properties")
public class SMSMessagePropertiesConfig { }
