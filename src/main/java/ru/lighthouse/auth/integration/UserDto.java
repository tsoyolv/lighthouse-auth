package ru.lighthouse.auth.integration;

import java.util.List;

public class UserDto {

    public UserDto(String phoneNumber, List<String> authorities) {
        this.phoneNumber = phoneNumber;
        this.authorities = authorities;
    }

    private String phoneNumber;

    private List<String> authorities;

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public List<String> getAuthorities() {
        return authorities;
    }

    public void setAuthorities(List<String> authorities) {
        this.authorities = authorities;
    }
}
