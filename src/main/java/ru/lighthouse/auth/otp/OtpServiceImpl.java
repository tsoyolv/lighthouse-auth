package ru.lighthouse.auth.otp;

import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.lighthouse.auth.sms.SMSMessageService;

import javax.transaction.Transactional;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
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

    @Value("${sms.message.pattern}")
    private String smsLoginPattern;

    @Value("${otp.default.password.enabled}")
    private boolean defaultPasswordEnabled;

    @Value("${otp.default.password.value}")
    private String defaultPassword;

    @Value("${otp.next-otp.timeout}")
    private int nextOtpTimeout;

    @Override
    public void createAndSendOtp(String phoneNumber) {
        String password = generatePassword();
        Date prolongationOtp = DateUtils.addSeconds(new Date(), prolongationSeconds);
        Otp otp = new Otp(phoneNumber + password, prolongationOtp);
        otpRepository.save(otp);
        smsMessageService.sendSmsImmediately(phoneNumber, createMessage(password), 0);
    }

    @Override
    @Transactional
    public boolean isOtpValid(String phoneNumber, String password) {
        String phoneNumberOtp = phoneNumber + password;
        Optional<Otp> otpOptional = otpRepository.findAllByPhoneNumberOtp(phoneNumberOtp).max(Comparator.comparingLong(Otp::getId));
        if (otpOptional.isPresent()) {
            Otp otp = otpOptional.get();
            Date now = new Date();
            if (now.before(otp.getProlongationDate())) {
                otpRepository.deleteByPhoneNumberOtpIsStartingWith(phoneNumber);
                return true;
            }
        }
        return false;
    }

    @Override
    @Transactional
    public boolean isOtpNotValid(String phoneNumber, String otp) {
        return !isOtpValid(phoneNumber, otp);
    }

    @Override
    public List<Otp> getAll() {
        return otpRepository.findAll();
    }

    private String createMessage(String password) {
        ByteBuffer buffer = StandardCharsets.UTF_8.encode(smsLoginPattern);
        String utf8EncodedString = StandardCharsets.UTF_8.decode(buffer).toString();
        return String.format(utf8EncodedString, password);
    }

    private String generatePassword() {
        return defaultPasswordEnabled ? defaultPassword : String.valueOf(RandomUtils.nextInt(1000, 10000));
    }
}
