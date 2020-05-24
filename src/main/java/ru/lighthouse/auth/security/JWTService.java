package ru.lighthouse.auth.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

import static ru.lighthouse.auth.security.JWTConfiguration.AUTHORITIES_CLAIM_NAME;

@Service
public class JWTService {

    // it has to be a Resource or Autowired, either it will be a cycle: SecurityConfiguratio -> JWTConfiguration
    @Resource
    private JWTConfiguration configuration;

    public String createJWTToken(String subject, List<String> authorities) {
        long now = System.currentTimeMillis();
        String token = Jwts.builder()
                .setSubject(subject)
                .claim(AUTHORITIES_CLAIM_NAME, authorities)
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(now + configuration.getExpiration() * 1000))  // in milliseconds
                .signWith(SignatureAlgorithm.HS512, configuration.getSecret().getBytes())
                .compact();
        return configuration.getPrefix() + token;
    }

    public Claims validateAndGetClaims(String token) throws Exception {
        return Jwts.parser()
                .setSigningKey(configuration.getSecret().getBytes())
                .parseClaimsJws(token)
                .getBody();
    }

    public JWTConfiguration getConfiguration() {
        return configuration;
    }
}
