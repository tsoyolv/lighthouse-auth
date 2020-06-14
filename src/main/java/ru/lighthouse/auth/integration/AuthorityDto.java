package ru.lighthouse.auth.integration;

public class AuthorityDto {
    public AuthorityDto(String name, String systemName) {
        this.name = name;
        this.systemName = systemName;
    }

    private String name;
    private String systemName;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSystemName() {
        return systemName;
    }

    public void setSystemName(String systemName) {
        this.systemName = systemName;
    }
}
