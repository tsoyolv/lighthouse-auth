package ru.lighthouse.auth.logic.service;

import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.lighthouse.auth.logic.entity.Otp;
import ru.lighthouse.auth.logic.repository.OtpRepository;
import ru.lighthouse.auth.message.SMSMessageService;

import java.nio.charset.StandardCharsets;
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

    @Value("${otp.default.password.enabled}")
    private boolean defaultPasswordEnabled;

    @Value("${otp.default.password.value}")
    private String defaultPassword;

    @Override
    public void createOtp(String phoneNumber) {
        String password = generatePassword(phoneNumber);
        Date prolongationOtp = DateUtils.addSeconds(new Date(), prolongationSeconds);
        Otp otp = new Otp(phoneNumber + password, prolongationOtp);
        otpRepository.save(otp);
        smsMessageService.sendSmsImmediately(phoneNumber, createMessage(password), 0);
    }

    private String createMessage(String password) {
        //return String.format(smsLoginPattern, password); todo
        return String.format("Ваш пароль от LightHouse: %s. С праздничком!", password);
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
        if (defaultPasswordEnabled) {
            return defaultPassword;
        } else {
            return String.valueOf(RandomUtils.nextInt(0000, 10000));
        }
    }
}
