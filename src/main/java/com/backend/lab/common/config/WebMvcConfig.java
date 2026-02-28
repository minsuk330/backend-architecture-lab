package com.backend.lab.common.config;

import java.time.format.DateTimeFormatter;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.format.datetime.standard.DateTimeFormatterRegistrar;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

  private static final String DATE_FORMAT = "yyyy-MM-dd";
  private static final String DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

  @Override
  public void addFormatters(FormatterRegistry registry) {
    DateTimeFormatterRegistrar registrar = new DateTimeFormatterRegistrar();

    // Set the same date/time formats as in JacksonConfig
    registrar.setDateFormatter(DateTimeFormatter.ofPattern(DATE_FORMAT));
    registrar.setDateTimeFormatter(DateTimeFormatter.ofPattern(DATETIME_FORMAT));

    registrar.registerFormatters(registry);
  }
}
