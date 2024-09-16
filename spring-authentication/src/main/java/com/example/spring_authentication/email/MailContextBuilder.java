package com.example.spring_authentication.email;

import org.springframework.stereotype.Controller;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import lombok.AllArgsConstructor;

@Controller
@AllArgsConstructor
public class MailContextBuilder {
  private final TemplateEngine templateEngine;

  public String build(String body) {
    Context context = new Context();
    context.setVariable("body", body);
    return templateEngine.process("notification_template.html", context);
  }
}
