package ru.lighthouse.auth.logic.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Entity
@Table(name="OTP")
public class Otp {

    public Otp() {
    }

    public Otp(String phoneNumberOtp, Date prolongationDate) {
        this.phoneNumberOtp = phoneNumberOtp;
        this.prolongationDate = prolongationDate;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="PHONE_NUMBER_OTP", length=100, unique=false)
    private String phoneNumberOtp;

    @Column(name="PROLONGATION_DATE")
    private Date prolongationDate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPhoneNumberOtp() {
        return phoneNumberOtp;
    }

    public void setPhoneNumberOtp(String phoneNumberOtp) {
        this.phoneNumberOtp = phoneNumberOtp;
    }

    public Date getProlongationDate() {
        return prolongationDate;
    }

    public void setProlongationDate(Date prolongationDate) {
        this.prolongationDate = prolongationDate;
    }
}
