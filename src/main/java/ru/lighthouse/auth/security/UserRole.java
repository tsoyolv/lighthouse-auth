package ru.lighthouse.auth.security;

public enum UserRole {
    USER("ROLE_USER"), ADMIN("ROLE_ADMIN"), IOS_SELLER("ROLE_IOS_SELLER"), IOS("ROLE_IOS");

    private String springRole;

    UserRole(String springRole) {
        this.springRole = springRole;
    }

    public String getSpringRole() {
        return springRole;
    }
}
