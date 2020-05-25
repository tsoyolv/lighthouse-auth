package ru.lighthouse.auth.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class JWTService {

    public static final String AUTHORITIES_CLAIM_NAME = "authorities";

    @Value("${security.jwt.uri}")
    private String authUri;

    @Value("${security.jwt.header:Authorization}")
    private String header;

    @Value("${security.jwt.prefix:Basic }")
    private String prefix;

    @Value("${security.jwt.expiration}")
    private int expiration;

    @Value("${security.jwt.secret}")
    private String secret;

    public String createJWTToken(String subject, List<String> authorities) {
        long now = System.currentTimeMillis();
        String token = Jwts.builder()
                .setSubject(subject)
                .claim(AUTHORITIES_CLAIM_NAME, authorities)
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(now + getExpiration() * 1000))  // in milliseconds
                .signWith(SignatureAlgorithm.HS512, getSecret().getBytes())
                .compact();
        return getPrefix() + token;
    }
    
    public String getAuthUri() {
        return authUri;
    }

    public String getHeader() {
        return header;
    }

    public String getPrefix() {
        return prefix;
    }

    public int getExpiration() {
        return expiration;
    }

    public String getSecret() {
        return secret;
    }
}
