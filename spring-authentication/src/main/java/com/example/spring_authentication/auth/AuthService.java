package com.example.spring_authentication.auth;

import java.time.Instant;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.spring_authentication.email.Mail;
import com.example.spring_authentication.email.MailService;
import com.example.spring_authentication.exception.SpringAuthException;
import com.example.spring_authentication.token.TokenDto;
import com.example.spring_authentication.token.forgot_password_token.ForgotPasswordDto;
import com.example.spring_authentication.token.forgot_password_token.ForgotPasswordService;
import com.example.spring_authentication.token.json_web_token.JwtService;
import com.example.spring_authentication.token.refresh.RefreshTokenService;
import com.example.spring_authentication.token.verification.VerificationTokenService;
import com.example.spring_authentication.user.User;
import com.example.spring_authentication.user.UserRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class AuthService {
  private final UserRepository userRepository;

  private final AuthenticationManager authenticationManager;
  private final PasswordEncoder passwordEncoder;

  private final JwtService jwtService;
  private final RefreshTokenService refreshTokenService;
  private final VerificationTokenService verificationTokenService;
  private final ForgotPasswordService forgotPasswordService;
  private final MailService mailService;

  public String signup(AuthRequestDto request) {
    Optional<User> _user = userRepository.findByUsernameOrEmail(request.username(), request.email());
    if (_user.isPresent()) {
      if (_user.get().getEmail().equals(request.email())) {
        throw new SpringAuthException(
          Instant.now(), 
          HttpStatus.CONFLICT, 
          "Duplicate Email", 
          "An account is already registered with that email address.", 
          "/spring-auth-api/v1/auth/signup"
        );
      } else {
        throw new SpringAuthException(
          Instant.now(), 
          HttpStatus.CONFLICT, 
          "Duplicate Username", 
          "A user with that username already exists.", 
          "/spring-auth-api/v1/auth/signup"
        );
      }
    }

    User user  = User.builder()
      .username(request.username())
      .password(passwordEncoder.encode(request.password()))
      .email(request.email())
      .role("ROLE_USER")
      .enable(false)
      .build();

    user = userRepository.save(user);

    String token = verificationTokenService.generateToken(user);
    Mail mail = new Mail(
      user.getEmail(), 
      "Activate Your Account Now!",
      String.format(
        "Hi %s,\nTo complete your registration and activate your account, please click on the link below:"
        + "\nhttp://localhost:4200/verify/%s\n"
        + "\nCode: %s\n"
        + "Once you click on the link, a new window will open. Please enter the activation code provided in the window to activate your account.",
        user.getUsername(), user.getId(), token
      )
    );

    mailService.send(mail);
    return user.getId();
  }

  public AuthResponseDto login(AuthRequestDto request) {
    User user = userRepository.findByUsername(request.username()).orElseThrow(
      () -> new SpringAuthException(
        Instant.now(), 
        HttpStatus.FORBIDDEN, 
        "Invalid Username", 
        "The provided username is invalid. Please check for typos or formatting errors.", 
        "/spring-auth-api/v1/auth/login"
      )
    );

    Authentication authentication;
    try {
      authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.username(), request.password()));
    } catch (Exception e) {
      throw new SpringAuthException(
        Instant.now(), 
        HttpStatus.FORBIDDEN, 
        "Invalid Password", 
        "The password you entered is incorrect. Please try again.", 
        "/spring-auth-api/v1/auth/login"
      );
    }

    user = (User)authentication.getPrincipal();

    return new AuthResponseDto(user.getId(), jwtService.generateToken(user.getUsername()), refreshTokenService.genetateToken(user));
  }

  public void verify(String userId, TokenDto request, boolean update) {
    User user = userRepository.findById(userId).orElseThrow(
      () -> new SpringAuthException(
        Instant.now(), 
        HttpStatus.BAD_REQUEST, 
        "Invalid Verification Url", 
        "The provided url is invalid. Please check for typos or formatting errors.", 
        "/spring-auth-api/v1/auth/verify"
      )
    );
    if (update) {
      updateVerificationToken(user, request.token());
      return;
    }

    if (verificationTokenService.validate(user, request.token())) {
      user.setEnable(true);
      userRepository.save(user);
      verificationTokenService.delete(request.token());
    }
  }

  private void updateVerificationToken(User user, String token) {
    String _token = verificationTokenService.update(user);

    Mail mail = new Mail(
      user.getEmail(), 
      "Activate Your Account Now!",
      String.format(
        "Hi %s,\nTo complete your registration and activate your account, please click on the link below:"
        + "\nhttp://localhost:4200/verify/%s\n"
        + "\nCode: %s\n"
        + "Once you click on the link, a new window will open. Please enter the activation code provided in the window to activate your account.",
        user.getUsername(), user.getId(), _token
      )
    );

    mailService.send(mail);
  }

  public AuthResponseDto refresh(String userId, TokenDto request) {
    User user = userRepository.findById(userId).orElseThrow(
      () -> new SpringAuthException(
        Instant.now(), 
        HttpStatus.FORBIDDEN, 
        "Invalid User", 
        "The provided user is invalid. Please check for typos or formatting errors.", 
        "/spring-auth-api/v1/auth/refresh"
      )
    );

    if (!refreshTokenService.validate(userId, request.token())) {
      return null;
    }

    refreshTokenService.delete(request.token());
    String authToken = jwtService.generateToken(user.getUsername());
    String refreshToken = refreshTokenService.genetateToken(user);

    return new AuthResponseDto(userId, authToken, refreshToken);
  }

  public void forgot(String userId) {
    User user = userRepository.findByUsername(userId).orElseThrow(
      () -> new SpringAuthException(
        Instant.now(), 
        HttpStatus.NOT_FOUND, 
        "User Not Found", 
        "No user was found with that username or email address.", 
        "/spring-auth-api/v1/auth/forgot"
      )
    );

    String token = forgotPasswordService.generateToken(user);
    Mail mail = new Mail(
      user.getEmail(), 
      "Reset Your Password",
      String.format(
        "Hi %s,\nWe've received a request to reset your password for your account, If you requested this password reset, please click on the link below to reset your password:"
        + "\nhttp://localhost:4200/reset/%s?tokenId=%s\n"
        + "If you did not request this password reset, please disregard this email. Your password remains unchanged.",
        user.getUsername(), user.getId(), token
      )
    );

    mailService.send(mail);
  }

  public void reset(String userId, String token, ForgotPasswordDto request, boolean update) {
    User user = userRepository.findById(userId).orElseThrow(
      () -> new SpringAuthException(
        Instant.now(), 
        HttpStatus.BAD_REQUEST, 
        "Invalid Verification Url", 
        "The provided url is invalid. Please check for typos or formatting errors.", 
        "/spring-auth-api/v1/auth/reset"
      )
    );

    if (update) {
      forgot(user.getUsername());
      return;
    }

    if (!forgotPasswordService.validate(user, token)) {
      return;
    }

    user.setPassword(passwordEncoder.encode(request.password()));
    userRepository.save(user);
    forgotPasswordService.delete(user);

    Mail mail = new Mail(
      user.getEmail(), 
      "Your Password Has Been Changed",
      String.format(
        "Dear %s,\nWe are writing to inform you that your password for your account has been successfully changed.",
        user.getUsername()
      )
    );

    mailService.send(mail);
  }

  public void logout(String tokenId) {
    refreshTokenService.delete(tokenId);
  }
}
