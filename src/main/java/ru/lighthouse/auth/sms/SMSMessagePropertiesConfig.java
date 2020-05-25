package ru.lighthouse.auth.sms;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:sms.properties")
public class SMSMessagePropertiesConfig { }
