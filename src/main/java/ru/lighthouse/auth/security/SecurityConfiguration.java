package ru.lighthouse.auth.security;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.AuthenticationEntryPoint;
import ru.lighthouse.auth.integration.MainServiceAdapter;
import ru.lighthouse.auth.otp.OtpService;

import javax.annotation.Resource;

import static ru.lighthouse.auth.Uri.OTP_URI;
import static ru.lighthouse.auth.Uri.OTP_VIEW_URI;
import static ru.lighthouse.auth.Uri.TEST_SERVICE_URI;

@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Value("${spring.h2.console.path}")
    private String h2ConsoleUri;

    // it has to be a Resource or Autowired, either it will be a cycle: SecurityConfiguratio -> UserDetailsService
    @Resource
    private JWTService jwtService;
    @Resource
    private OtpService otpService;
    @Resource
    private MainServiceAdapter mainServiceAdapter;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .formLogin().disable()
                .httpBasic().disable()
                .logout().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .addFilter(authenticationFilterObject())
                .exceptionHandling().authenticationEntryPoint(failedAuthenticationEntryPointObject())
                .and()
                .authorizeRequests()
                .antMatchers(OTP_URI, jwtService.getAuthUri(), TEST_SERVICE_URI, OTP_VIEW_URI).permitAll()
                .anyRequest().authenticated();
    }

    @Override
    public void configure(final WebSecurity webSecurity) {
        webSecurity.ignoring().antMatchers(h2ConsoleUri + "/**");
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) {
        auth.authenticationProvider(authenticationProviderObject());
    }

    private JwtAuthenticationFilter authenticationFilterObject() throws Exception {
        return new JwtAuthenticationFilter(authenticationManager(), jwtService);
    }

    private OTPAuthenticationProvider authenticationProviderObject() {
        return new OTPAuthenticationProvider(jwtService, otpService, mainServiceAdapter);
    }

    private AuthenticationEntryPoint failedAuthenticationEntryPointObject() {
        return new OTPAuthenticationProvider.FailedAuthenticationEntryPoint();
    }
}