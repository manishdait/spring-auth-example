package com.example.spring_authentication.token.forgot_password_token;

import java.util.UUID;
import java.util.Optional;
import java.time.Instant;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.spring_authentication.exception.SpringAuthException;
import com.example.spring_authentication.user.User;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class ForgotPasswordService {
  private ForgotPasswordTokenRepository forgotPasswordTokenRepository;

  @Transactional
  public String generateToken(User user) {
    Optional<ForgotPasswordToken> _token = forgotPasswordTokenRepository.findByUser(user);
    if (_token.isPresent()) {
      return update(user);
    }
    
    String token = UUID.randomUUID().toString();
    ForgotPasswordToken forgotPasswordToken = new ForgotPasswordToken();
    forgotPasswordToken.setToken(token);
    forgotPasswordToken.setUser(user);
    forgotPasswordToken.setExpiration(Instant.now().plusSeconds(1800));

    forgotPasswordTokenRepository.save(forgotPasswordToken);
    return token;
  }

  @Transactional
  public String update(User user) {
    ForgotPasswordToken forgotPasswordToken = forgotPasswordTokenRepository.findByUser(user).orElseThrow();
    String token = UUID.randomUUID().toString();
    forgotPasswordToken.setToken(token);
    forgotPasswordToken.setExpiration(Instant.now().plusSeconds(1800));

    forgotPasswordTokenRepository.save(forgotPasswordToken);
    return token;
  }

  @Transactional
  public boolean validate(User user, String token) {
    ForgotPasswordToken forgotPasswordToken = forgotPasswordTokenRepository.findByUser(user).orElseThrow();
    if (!forgotPasswordToken.getToken().equals(token)) {
      throw new SpringAuthException(
        Instant.now(), 
        HttpStatus.BAD_REQUEST, 
        "Invalid Token", 
        "The provided token is invalid. Please request a new one.", 
        "/spring-auth-api/v1/auth/reset"
      );
    }
    return true;
  }

  @Transactional
  public void delete(User user) {
    ForgotPasswordToken forgotPasswordToken = forgotPasswordTokenRepository.findByUser(user).orElseThrow();
    forgotPasswordTokenRepository.delete(forgotPasswordToken);
  }
}