package com.example.spring_authentication.exception;

import java.time.Instant;

public record SpringAuthDto(Instant timestamp, int status, String error, String message, String path) {
  
}
