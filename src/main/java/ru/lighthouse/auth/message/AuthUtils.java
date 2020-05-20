package ru.lighthouse.auth.message;

import java.util.regex.Pattern;

public class AuthUtils {
    private static final Pattern PHONE_NUMBER_PATTERN = Pattern.compile("7\\d{10}");

    public static boolean isValidPhoneNumber(String phoneNumber) {
        return PHONE_NUMBER_PATTERN.matcher(phoneNumber).matches();
    }
}
