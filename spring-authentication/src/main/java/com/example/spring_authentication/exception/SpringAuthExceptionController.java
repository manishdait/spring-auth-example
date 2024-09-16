package com.example.spring_authentication.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class SpringAuthExceptionController {
  @ExceptionHandler(SpringAuthException.class)
  public ResponseEntity<SpringAuthDto> handleException(SpringAuthException e) {
    return ResponseEntity.status(e.getStatus()).body(
      new SpringAuthDto(e.getTimestamp(), e.getStatus().value(), e.getError(), e.getMessage(), e.getPath())
    );
  }
}
