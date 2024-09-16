package com.example.spring_authentication.token.forgot_password_token;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.spring_authentication.user.User;

@Repository
public interface ForgotPasswordTokenRepository extends JpaRepository<ForgotPasswordToken, Long> { 
  Optional<ForgotPasswordToken> findByToken(String token);
  Optional<ForgotPasswordToken> findByUser(User user);
}
