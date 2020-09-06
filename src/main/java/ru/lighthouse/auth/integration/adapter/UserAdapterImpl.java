package ru.lighthouse.auth.integration.adapter;

import eu.bitwalker.useragentutils.UserAgent;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;
import ru.lighthouse.auth.integration.crm.CrmIntegrationService;
import ru.lighthouse.auth.integration.dto.UserDto;
import ru.lighthouse.auth.integration.dto.UserType;
import ru.lighthouse.auth.integration.mobile.MobileIntegrationService;
import ru.lighthouse.auth.otp.logic.OtpService;
import ru.lighthouse.auth.security.OTPAuthenticationProvider;

import static eu.bitwalker.useragentutils.DeviceType.MOBILE;
import static eu.bitwalker.useragentutils.Manufacturer.APPLE;
import static java.util.Objects.nonNull;

@Service
@RequiredArgsConstructor
public class UserAdapterImpl implements UserAdapter {
    private static final Logger logger = LoggerFactory.getLogger(UserAdapterImpl.class);
    private final CrmIntegrationService crmIntegrationService;
    private final MobileIntegrationService mobileIntegrationService;
    private final OtpService otpService;

    @Override
    public UserDto retrieveUser(String phoneNumber, String otp, String userAgentStr, UserType userType) throws AuthenticationException {
        try {
            validateParams(phoneNumber, otp);
            userType = resolveUserType(userType, userAgentStr);
            if (UserType.CRM == userType) {
                return crmIntegrationService.getUser(new UserDto(phoneNumber, userAgentStr));
            } else {
                return mobileIntegrationService.getOrCreateUser(new UserDto(phoneNumber, userAgentStr));
            }
        } catch (AuthenticationException aEx) {
            logger.error("Auth error: {}", aEx.getMessage());
            throw aEx;
        } catch (Exception e) {
            logger.error("Integration error", e);
            throw new AuthenticationServiceException(OTPAuthenticationProvider.ExceptionMessage.INTEGRATION_ERROR);
        }
    }

    private void validateParams(String phoneNumber, String otp) {
        if (otpService.isNotValidPhoneNumber(phoneNumber)) {
            throw new AuthenticationServiceException(OTPAuthenticationProvider.ExceptionMessage.PHONE_NUMBER_INVALID);
        }
        if (otpService.isOtpNotValid(phoneNumber, otp)) {
            throw new AuthenticationServiceException(OTPAuthenticationProvider.ExceptionMessage.SMS_CODE_INVALID);
        }
    }

    private UserType resolveUserType(UserType userType, String userAgentStr) {
        if (nonNull(userType)) {
            return userType;
        }
        return UserType.CRM;
        /*UserAgent userAgent = UserAgent.parseUserAgentString(userAgentStr);
        if (MOBILE == userAgent.getOperatingSystem().getDeviceType()) {
            if (APPLE == userAgent.getOperatingSystem().getManufacturer()) {
                userType = UserType.MOBILE;
            } else {
                userType = UserType.MOBILE;
            }
        } else {
            userType = UserType.CRM;
        }
        return userType;*/
    }
}
