package ru.lighthouse.auth.security;


import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import ru.lighthouse.auth.api.service.OtpService;
import ru.lighthouse.auth.api.service.UserService;

import javax.annotation.Resource;

@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    // it has to be a Resource or Autowired, either it will be a cycle: SecurityConfiguratio -> UserDetailsService
    @Resource
    private UserDetailsService userDetailsService;
    @Resource
    private JWTService jwtService;
    @Resource
    private OtpService otpService;
    @Resource
    private UserService userService;

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
                .antMatchers("/otp", jwtService.getConfiguration().getAuthUri(), "/testservice", "/instance-id").permitAll()
                .anyRequest().authenticated();
    }

    @Override
    public void configure(final WebSecurity webSecurity) {
        webSecurity.ignoring().antMatchers("/h2/**");
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
    }

    @Bean
    public JWTConfiguration jwtConfig() {
        return new JWTConfiguration();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    private JwtAuthenticationFilter authenticationFilterObject() throws Exception {
        return new JwtAuthenticationFilter(authenticationManager(), jwtService, otpService, userService, passwordEncoder());
    }

    private AuthenticationEntryPoint failedAuthenticationEntryPointObject() {
        return new JwtAuthenticationFilter.FailedAuthenticationEntryPoint();
    }
}