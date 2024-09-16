package com.example.spring_authentication.exception;

import java.time.Instant;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public class SpringAuthException extends RuntimeException {
  private Instant timestamp;
  private HttpStatus status;
  private String error;
  private String message;
  private String path;

  public SpringAuthException(Instant timestamp, HttpStatus status, String error, String message, String path) {
    super(message);
    this.timestamp = timestamp;
    this.status = status;
    this.error = error;
    this.message = message;
    this.path = path;
  }
}
