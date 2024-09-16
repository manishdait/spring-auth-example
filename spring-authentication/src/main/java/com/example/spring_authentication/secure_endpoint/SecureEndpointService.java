package com.example.spring_authentication.secure_endpoint;

import org.springframework.stereotype.Service;

import com.example.spring_authentication.user.User;
import com.example.spring_authentication.user.UserRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class SecureEndpointService {
  private final UserRepository userRepository;

  public UserDto getUser(String userId) {
    User user = userRepository.findById(userId).orElseThrow();
    return new UserDto(user.getUsername(), user.getRole(), user.getEmail());
  }
}
