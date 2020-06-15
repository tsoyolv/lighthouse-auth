package ru.lighthouse.auth.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

@Service
@Getter
public class JWTService {
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

    @Value("${security.jwt.claims.authorities.claimName}")
    private String authoritiesClaimName;
    @Value("${security.jwt.claims.details.claimName}")
    private String detailsClaimName;

    @Value("${security.jwt.claims.details.userId}")
    private String detailsClaimUserIdClaimName;
    @Value("${security.jwt.claims.details.userFirstName}")
    private String detailsClaimUserFirstName;
    @Value("${security.jwt.claims.details.userSecondName}")
    private String detailsClaimUserSecondName;
    @Value("${security.jwt.claims.details.userLastName}")
    private String detailsClaimUserLastName;
    @Value("${security.jwt.claims.details.userBirthDate}")
    private String detailsClaimUserBirthDate;

    public String createJWTToken(String subject, List<String> authorities, Object details) {
        long now = System.currentTimeMillis();
        String token = Jwts.builder()
                .setSubject(subject)
                .claim(authoritiesClaimName, authorities)
                .claim(detailsClaimName, details)
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(now + getExpiration() * 1000))  // in milliseconds
                .signWith(SignatureAlgorithm.HS512, getSecret().getBytes())
                .compact();
        return getPrefix() + token;
    }

    public LinkedHashMap<String, Object> createDetails(Long userId, Date birthDate, String firstName, String secondName, String lastName) {
        LinkedHashMap<String, Object> details = new LinkedHashMap<>();
        details.put(detailsClaimUserIdClaimName, userId);
        details.put(detailsClaimUserBirthDate, birthDate);
        details.put(detailsClaimUserFirstName, firstName);
        details.put(detailsClaimUserSecondName, secondName);
        details.put(detailsClaimUserLastName, lastName);
        return details;
    }
}
