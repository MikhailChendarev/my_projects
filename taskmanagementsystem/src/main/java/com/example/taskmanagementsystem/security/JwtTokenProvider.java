package com.example.taskmanagementsystem.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.util.Date;

/**
 * Provider for generating and validating JWT tokens.
 */
@Slf4j
@Component
public class JwtTokenProvider {

    private SecretKey jwtSecretKey;
    private int jwtExpiration;

    @Autowired
    public JwtTokenProvider(@Value("${jwt.secret}") String jwtSecret, @Value("${jwt.expiration}") int jwtExpiration) {
        this.jwtSecretKey = Keys.hmacShaKeyFor(jwtSecret.getBytes());
        this.jwtExpiration = jwtExpiration;
    }

    /**
     * Generates a JWT token for the authenticated user.
     *
     * @param authentication authentication object
     * @return generated JWT token
     */
    public String generateToken(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpiration);
        String token = Jwts.builder()
                .setSubject(userDetails.getUsername())
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(jwtSecretKey, SignatureAlgorithm.HS256)
                .compact();
        log.info("Generated JWT Token for user: {}", userDetails.getUsername());
        return token;
    }

    /**
     * Extracts the user's email from the JWT token.
     *
     * @param token JWT token
     * @return user's email
     */
    public String getUserEmailFromJwt(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(jwtSecretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
        String username = claims.getSubject();
        log.info("Extracted username from JWT Token: {}", username);
        return username;
    }

    /**
     * Validates the JWT token.
     *
     * @param authToken JWT token
     * @return true if valid, false otherwise
     */
    public boolean validateToken(String authToken) {
        try {
            Jwts.parserBuilder().setSigningKey(jwtSecretKey).build().parseClaimsJws(authToken);
            log.info("JWT Token is valid");
            return true;
        } catch (Exception e) {
            log.error("JWT Token validation failed", e);
            return false;
        }
    }
}
