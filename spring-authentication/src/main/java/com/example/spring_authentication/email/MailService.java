package com.example.spring_authentication.email;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class MailService {
  private final JavaMailSender javaMailSender;
  private final MailContextBuilder mailContextBuilder;

  public MailService(JavaMailSender javaMailSender, MailContextBuilder mailContextBuilder) {
    this.javaMailSender = javaMailSender;
    this.mailContextBuilder = mailContextBuilder;
  }

  @Value("${spring.mail.username}")
  private String sender;

  @Async
  public void send(Mail mail) {
    MimeMessage message = javaMailSender.createMimeMessage();
    MimeMessageHelper messageHelper = new MimeMessageHelper(message, "UTF-8");

    try {
      messageHelper.setFrom(sender);
      messageHelper.setTo(mail.recipent());
      messageHelper.setSubject(mail.subject());
      messageHelper.setText(mailContextBuilder.build(mail.body()), true);
    } catch (MessagingException e) {
      log.error("Unable to build message", e);
    }
    
    try {
      javaMailSender.send(message);
    } catch (Exception e) {
      log.error("UNable to Send mail", e);
    }
  }
}
