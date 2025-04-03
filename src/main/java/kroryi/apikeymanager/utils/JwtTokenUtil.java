package kroryi.apikeymanager.utils;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import kroryi.apikeymanager.entity.ApiKeyEntity;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.ZoneId;
import java.util.Date;

@Component
public class JwtTokenUtil {

    @Value("${kroryi.jwt.secret}")
    private String secret;

    private SecretKey secretKey;

    @PostConstruct
    public void init() {
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        if (keyBytes.length < 32) {
            throw new IllegalArgumentException("JWT secret must be at least 32 bytes");
        }
        this.secretKey = Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateToken(ApiKeyEntity key) {
        Date now = new Date();
        Date expiry = Date.from(key.getExpiresAt().atZone(ZoneId.systemDefault()).toInstant());

        return Jwts.builder()
                .setSubject(key.getId().toString())
                .claim("name", key.getName())
                .claim("allowedIp", key.getAllowedIp())
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public Claims parseToken(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            throw new JwtException("JWT expired", e);
        } catch (JwtException e) {
            throw new JwtException("Invalid JWT", e);
        }
    }
}
