package ru.lighthouse.auth.otp;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Entity
@Table(name="OTP")
@NoArgsConstructor @Getter @Setter
public class Otp {
    public Otp(String phoneNumberOtp, Date prolongationDate) {
        this.phoneNumberOtp = phoneNumberOtp;
        this.prolongationDate = prolongationDate;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="PHONE_NUMBER_OTP")
    private String phoneNumberOtp;

    @Column(name="PROLONGATION_DATE")
    private Date prolongationDate;
}
