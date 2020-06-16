package ru.lighthouse.auth.security;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.AuthenticationEntryPoint;
import ru.lighthouse.auth.integration.IntegrationServiceAdapter;
import ru.lighthouse.auth.otp.logic.OtpService;

import javax.annotation.Resource;

import static ru.lighthouse.auth.App.HEALTH_URI;

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
    private IntegrationServiceAdapter integrationServiceAdapter;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .formLogin().disable()
                .httpBasic().disable()
                .logout().disable()
                .sessionManagement().disable()
                .addFilter(authenticationFilterObject())
                .exceptionHandling().authenticationEntryPoint(failedAuthenticationEntryPointObject())
                .and()
                .authorizeRequests()
                .antMatchers(HEALTH_URI, otpService.getOtpUri(), jwtService.getAuthUri()).permitAll()
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
        return new OTPAuthenticationProvider(jwtService, otpService, integrationServiceAdapter);
    }

    private AuthenticationEntryPoint failedAuthenticationEntryPointObject() {
        return new OTPAuthenticationProvider.FailedAuthenticationEntryPoint();
    }
}