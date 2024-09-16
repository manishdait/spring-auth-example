package com.example.spring_authentication.token.refresh;

import java.time.Instant;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.spring_authentication.exception.SpringAuthException;
import com.example.spring_authentication.user.User;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class RefreshTokenService {
  private final RefreshTokenRepository refreshTokenRepository;

  @Transactional
  public String genetateToken(User user) {
    String token = UUID.randomUUID().toString();
    RefreshToken refreshToken = new RefreshToken();
    refreshToken.setToken(token);
    refreshToken.setUser(user);
    refreshToken.setExpiration(Instant.now().plusSeconds(86400));
    
    refreshTokenRepository.save(refreshToken);
    return token;
  }

  @Transactional
  public boolean validate(String userId, String token) {
    RefreshToken refreshToken = refreshTokenRepository.findByToken(token).orElseThrow(
      () -> new SpringAuthException(
        Instant.now(), 
        HttpStatus.BAD_REQUEST, 
        "Invalid Token", 
        "The provided token is invalid. Please request a new one.", 
        "/spring-auth-api/v1/auth/refresh"
      )
    );

    if (!userId.equals(refreshToken.getUser().getId())) {
      return false;
    }

    return true;
  }

  @Transactional
  public void delete(String token) {
    RefreshToken refreshToken = refreshTokenRepository.findByToken(token).orElseThrow();
    refreshTokenRepository.delete(refreshToken);
  }
}
