package com.example.spring_authentication.auth;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.spring_authentication.token.TokenDto;
import com.example.spring_authentication.token.forgot_password_token.ForgotPasswordDto;

import lombok.AllArgsConstructor;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/spring-auth-api/v1/auth")
@AllArgsConstructor
public class AuthController {
  private final AuthService authService;

  @PostMapping("/signup")
  public ResponseEntity<Map<String, String>> signup(@RequestBody AuthRequestDto request) {
    return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("user", authService.signup(request)));
  }

  @PostMapping("/login")
  public ResponseEntity<AuthResponseDto> login(@RequestBody AuthRequestDto request) {
    return ResponseEntity.status(HttpStatus.OK).body(authService.login(request));
  }

  @PostMapping("/verify/{userId}")
  public ResponseEntity<Void> signup(@PathVariable String userId, @RequestBody TokenDto request, @RequestParam(defaultValue = "false") boolean update) {
    authService.verify(userId, request, update);
    return ResponseEntity.status(HttpStatus.OK).body(null);
  }

  @PostMapping("/refresh/{userId}")
  public ResponseEntity<AuthResponseDto> refresh(@PathVariable String userId, @RequestBody TokenDto request) {
    return ResponseEntity.status(HttpStatus.OK).body(authService.refresh(userId, request));
  }

  @PostMapping("/forgot")
  public ResponseEntity<Void> forgot(@RequestParam String userId) {
    authService.forgot(userId);
    return ResponseEntity.status(HttpStatus.OK).body(null);
  }
  
  @PatchMapping("/reset/{userId}")
  public ResponseEntity<Void> reset(@PathVariable String userId, @RequestBody ForgotPasswordDto result, @RequestParam(required = true) String tokenId, @RequestParam(defaultValue = "false") boolean update) {
    authService.reset(userId, tokenId, result, update);
    return ResponseEntity.status(HttpStatus.OK).body(null);
  }

  @PutMapping("/logout/{tokenId}")
  public ResponseEntity<Void> logout(@PathVariable String tokenId) {
    authService.logout(tokenId);
    return ResponseEntity.status(HttpStatus.OK).body(null);
  }
}
