package com.example.spring_authentication.token.json_web_token;

import java.security.Key;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {
  @Value("${jwt.secret-key}")
  private String SECRET_KEY;
  @Value("${jwt.expiration-sec}")
  private Integer EXPIRATION_SECONDS;

  public String generateToken(String username) {
    return Jwts.builder()
      .setClaims(new HashMap<>())
      .setSubject(username)
      .setIssuedAt(Date.from(Instant.now()))
      .setExpiration(Date.from(Instant.now().plusSeconds(EXPIRATION_SECONDS)))
      .signWith(getKey(), SignatureAlgorithm.HS256)
      .compact();
  }

  public String getUsername(String token) {
    return getClaims(token).getSubject();
  }

  public boolean validate(UserDetails userDetails, String token) {
    return getUsername(token).equals(userDetails.getUsername()) && !isExpire(token);
  }

  private Key getKey() {
    byte[] decodedKey = Decoders.BASE64.decode(SECRET_KEY);
    return Keys.hmacShaKeyFor(decodedKey);
  }

  private Claims getClaims(String token) {
    return Jwts.parserBuilder()
      .setSigningKey(getKey())
      .build()
      .parseClaimsJws(token)
      .getBody();
  }

  private boolean isExpire(String token) {
    return getClaims(token).getExpiration().before(new Date());
  }
}
