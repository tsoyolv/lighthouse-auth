package ru.lighthouse.auth;

import org.junit.jupiter.api.Test;
import ru.lighthouse.auth.sms.AuthUtils;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AuthUtilsTest {

    @Test
    public void isValidPhoneNumber() {
        assertFalse(AuthUtils.isValidPhoneNumber("1"));
        assertFalse(AuthUtils.isValidPhoneNumber("123"));
        assertFalse(AuthUtils.isValidPhoneNumber("123456789012"));
        assertFalse(AuthUtils.isValidPhoneNumber("12345678901"));
        assertTrue(AuthUtils.isValidPhoneNumber("72345678901"));
    }
}