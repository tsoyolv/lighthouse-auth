package ru.lighthouse.auth.integration;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter @Setter
public class AuthorityDto {
    public AuthorityDto(String name, String systemName) {
        this.name = name;
        this.systemName = systemName;
    }
    private String name;
    private String systemName;
}
