package ru.lighthouse.auth.integration;

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
    private Long id;
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
