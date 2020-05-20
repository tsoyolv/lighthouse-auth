package ru.lighthouse.auth.logic.entity;

import org.springframework.security.core.GrantedAuthority;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.Date;
import java.util.List;

@Entity
@Table(name="USER")
public class User {

    public User() {
    }

    public User(String name, String phonenumber, List<GrantedAuthority> authorities) {
        this.name = name;
        this.phonenumber = phonenumber;
        this.authorities = authorities;
    }

    public User(String phonenumber, String token) {
        this.phonenumber = phonenumber;
        this.token = token;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="NAME", length=50, nullable=false, unique=false)
    private String name;

    @Column(name="TOKEN", length=50, nullable=false, unique=false)
    private String token;

    @Column(name="PHONE_NUMBER", length=50, nullable=false, unique=false)
    private String phonenumber;

    @Column(name="DATE_OF_BIRTH", length=50, nullable=false, unique=false)
    private Date birthDate;

    @Transient
    private Integer age;

    @Transient
    private List<GrantedAuthority> authorities;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getPhonenumber() {
        return phonenumber;
    }

    public void setPhonenumber(String phonenumber) {
        this.phonenumber = phonenumber;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public List<GrantedAuthority> getAuthorities() {
        return authorities;
    }

    public void setAuthorities(List<GrantedAuthority> authorities) {
        this.authorities = authorities;
    }
}
