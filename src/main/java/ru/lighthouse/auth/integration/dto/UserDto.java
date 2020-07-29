package ru.lighthouse.auth.integration.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.Set;

@NoArgsConstructor
@Getter @Setter
public class UserDto {
    public UserDto(String phoneNumber, Set<AuthorityDto> authorities) {
        this.authorities = authorities;
        this.phoneNumber = phoneNumber;
    }
    public UserDto(String phoneNumber, String userAgent) {
        this.phoneNumber = phoneNumber;
        this.userAgent = userAgent;
    }
    private Long id;
    private String userAgent;
    private Set<AuthorityDto> authorities;
    private String phoneNumber;
    private Boolean enabled = Boolean.TRUE;
    private Boolean accountNonLocked = Boolean.TRUE;
    private String firstName;
    private String secondName;
    private String lastName;
    private Date birthDate;
    private Date registrationDate = new Date();
    private Date lastLogin = new Date();
}
