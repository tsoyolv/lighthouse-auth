package ru.lighthouse.auth.api.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Entity
@Table(name="USER")
public class User {

    public User() {
    }

    public User(String phoneNumber, String lastOtp) {
        this.phoneNumber = phoneNumber;
        this.lastOtp = lastOtp;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="PHONE_NUMBER", length=50, nullable=false, unique=true)
    private String phoneNumber;

    @Column(name="LAST_OTP", length=255)
    private String lastOtp;

    @Column(name="ROLES", length=500)
    private String roles;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getLastOtp() {
        return lastOtp;
    }

    public void setLastOtp(String lastOtp) {
        this.lastOtp = lastOtp;
    }

    public String getRoles() {
        return roles;
    }

    public void setRoles(String roles) {
        this.roles = roles;
    }
}
