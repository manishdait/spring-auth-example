package com.example.spring_authentication.secure_endpoint;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping("/spring-auth-api/v1/private")
@AllArgsConstructor
public class SecureEndpointController {
  private final SecureEndpointService service;

  @GetMapping("/{userId}")
  public ResponseEntity<UserDto> secure(@PathVariable String userId) {
      return ResponseEntity.status(HttpStatus.OK).body(service.getUser(userId));
  }
  
}
