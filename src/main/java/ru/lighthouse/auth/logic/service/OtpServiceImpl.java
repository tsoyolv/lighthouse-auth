package ru.lighthouse.auth.logic.service;

import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.lighthouse.auth.logic.entity.Otp;
import ru.lighthouse.auth.logic.repository.OtpRepository;
import ru.lighthouse.auth.message.SMSMessageService;

import java.util.Date;
import java.util.Optional;

@Service
public class OtpServiceImpl implements OtpService {

    private final SMSMessageService smsMessageService;

    private final OtpRepository otpRepository;

    public OtpServiceImpl(SMSMessageService smsMessageService, OtpRepository otpRepository) {
        this.smsMessageService = smsMessageService;
        this.otpRepository = otpRepository;
    }

    @Value("${otp.prolongation.seconds}")
    private int prolongationSeconds;

    @Value("${sms.message.pattern.login}")
    private String smsLoginPattern;

    @Override
    public void createOtp(String phoneNumber) {
        String password = generatePassword(phoneNumber);
        Date prolongationOtp = DateUtils.addSeconds(new Date(), prolongationSeconds);
        Otp otp = new Otp(phoneNumber + password, prolongationOtp);
        otpRepository.save(otp);
        smsMessageService.sendSmsImmediately(phoneNumber, String.format(smsLoginPattern, password), 0);
    }

    @Override
    public boolean checkOtp(String phoneNumber, String password) {
        String phoneNumberOtp = phoneNumber + password;
        Optional<Otp> otpOptional = otpRepository.findByPhoneNumberOtp(phoneNumberOtp);
        if (otpOptional.isPresent()) {
            Otp otp = otpOptional.get();
            Date now = new Date();
            if (now.before(otp.getProlongationDate())) {
                otpRepository.delete(otp);
                return true;
            }
        }
        return false;
    }

    private String generatePassword(String phoneNumber) {
        return "12345";
    }
}
