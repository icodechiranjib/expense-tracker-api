package com.expensetracker.util;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {

    // Use a long secret, Base64 encoded. You can generate one at https://www.allkeysgenerator.com/
    // openssl rand -base64 32
    private final String SECRET_KEY = "2f884c53b5c849bfa41aef05c5f60aab0c91a1bc482e4365bff94e5e1f17fdd7";

    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateToken(String email) {
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10)) // 10 hrs
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }
}
