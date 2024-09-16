package com.example.spring_authentication.token.verification;

import java.time.Instant;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.spring_authentication.exception.SpringAuthException;
import com.example.spring_authentication.user.User;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class VerificationTokenService {
  private final VerificationTokenRepository verificationTokenRepository;

  @Transactional
  public String generateToken(User user) {
    String token = generateToken();
    VerificationToken verificationToken = new VerificationToken();
    verificationToken.setToken(token);
    verificationToken.setUser(user);
    verificationToken.setExpiration(Instant.now().plusSeconds(30));

    verificationTokenRepository.save(verificationToken);
    return token;
  }

  public boolean validate(User user, String token) {
    VerificationToken verificationToken = getVerificationToken(token);

    if (!user.getId().equals(verificationToken.getUser().getId())) {
      throw new SpringAuthException(
        Instant.now(), 
        HttpStatus.BAD_REQUEST, 
        "Invalid Token", 
        "The provided token is invalid. Please request a new one.", 
        "/spring-auth-api/v1/auth/verify"
      );
    }

    if (isExpire(verificationToken.getExpiration())) {
      throw new SpringAuthException(
        Instant.now(), 
        HttpStatus.BAD_REQUEST, 
        "Token Expired", 
        "The provided token has expired. Please request a new one.", 
        "/spring-auth-api/v1/auth/verify"
      );
    }

    return true;
  }

  @Transactional
  public String update(User user) {
    VerificationToken verificationToken = verificationTokenRepository.findByUser(user).orElseThrow();

    String _token = generateToken();
    verificationToken.setToken(_token);
    verificationToken.setExpiration(Instant.now().plusSeconds(1800));
    verificationTokenRepository.save(verificationToken);

    return _token;
  }

  @Transactional
  public void delete(String token) {
    VerificationToken verificationToken = getVerificationToken(token);
    verificationTokenRepository.delete(verificationToken);
  }

  private String generateToken() {
    StringBuilder token = new StringBuilder();
    String charset = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890";
    int len = 6;

    for (int i = 0; i < len; i++) {
      int random = (int )(Math.random() * charset.length());
      token.append(charset.charAt(random));
    }

    return token.toString();
  }

  private boolean isExpire(Instant expiration) {
    return expiration.isBefore(Instant.now());
  }

  @Transactional
  private VerificationToken getVerificationToken(String token) {
    return verificationTokenRepository.findByToken(token).orElseThrow(
      () -> new SpringAuthException(
        Instant.now(), 
        HttpStatus.BAD_REQUEST, 
        "Invalid Token", 
        "The provided token is invalid. Please request a new one.", 
        "/spring-auth-api/v1/auth/verify"
      )
    );
  }
}
