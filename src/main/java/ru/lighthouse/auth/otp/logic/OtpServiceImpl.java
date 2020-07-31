package ru.lighthouse.auth.otp.logic;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.stereotype.Service;
import ru.lighthouse.auth.sms.SMSMessageService;

import javax.transaction.Transactional;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Comparator;
import java.util.Date;
import java.util.Optional;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class OtpServiceImpl implements OtpService {
    private static final Pattern PHONE_NUMBER_PATTERN = Pattern.compile("7\\d{10}");
    private static final Pattern OTP_PATTERN = Pattern.compile("\\d{4}");

    private final OtpConfig config;
    private final SMSMessageService smsMessageService;
    private final OtpRepository otpRepository;

    @Override
    public void createAndSendOtp(String phoneNumber) {
        String password = generatePassword();
        Date prolongationOtp = DateUtils.addSeconds(new Date(), config.getProlongationSeconds());
        Otp otp = new Otp(phoneNumber + password, prolongationOtp);
        otpRepository.save(otp);
        smsMessageService.sendSmsImmediately(phoneNumber, createMessage(password), 0);
    }

    @Override
    public boolean isValidPhoneNumber(String phoneNumber) {
        return PHONE_NUMBER_PATTERN.matcher(phoneNumber).matches();
    }

    @Override
    public boolean isNotValidPhoneNumber(String phoneNumber) {
        return !isValidPhoneNumber(phoneNumber);
    }

    @Override
    @Transactional
    public boolean isOtpValid(String phoneNumber, String otpStr) {
        if (!isValidPhoneNumber(phoneNumber) || !isOtp(otpStr)) {
            return false;
        }
        String phoneNumberOtp = phoneNumber + otpStr;
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
    public String getOtpUri() {
        return config.getUri();
    }

    private boolean isOtp(String otp) {
        return OTP_PATTERN.matcher(otp).matches();
    }

    private String createMessage(String password) {
        ByteBuffer buffer = StandardCharsets.UTF_8.encode(config.getSmsLoginPattern());
        String utf8EncodedString = StandardCharsets.UTF_8.decode(buffer).toString();
        return String.format(utf8EncodedString, password);
    }

    private String generatePassword() {
        return config.isDefaultPasswordEnabled() ? config.getDefaultPassword() : String.valueOf(RandomUtils.nextInt(1000, 10000));
    }
}
