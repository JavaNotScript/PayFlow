package com.payflow.Auth_servce.services;

import com.payflow.Auth_servce.security.UserPrincipal;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;
import java.util.function.Function;

@Service
public class JwtService {
    private final String secretKey;
    private final long expiration;

    public JwtService( @Value("${jwt.secret}") String secretKey, @Value("${jwt.expiration-ms}") long expiration) {


        this.secretKey = secretKey;
        this.expiration = expiration;
    }

    private SecretKey getSecretKey() {
        byte[] bytes = Base64.getDecoder().decode(secretKey);
        return Keys.hmacShaKeyFor(bytes);
    }

    public String generateToken(UserPrincipal userDetails) {
        String role = userDetails.getAuthorities()
                .stream()
                .findFirst()
                .map(GrantedAuthority::getAuthority)
                .orElse("ROLE_USER");


       return Jwts.builder()
                .subject(userDetails.getUsername())
                .claims()
                .add("userId", userDetails.getUserId())
                .add("role", role)
                .setIssuer("auth-servce")
                .setAudience("wallet-service")
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .and()
                .signWith(getSecretKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSecretKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();

    }

    private <T> T extractClaim(String token, Function<Claims, T> function) {
        Claims claims = extractAllClaims(token);
        return function.apply(claims);
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public boolean validate(UserDetails userDetails,String token) {
        final String email = extractUsername(token);
        return ((userDetails.getUsername().equals(email)) &&!isTokenExpired(token));
    }
}
