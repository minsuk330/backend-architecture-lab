package com.backend.lab.common.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

//  @Override
//  public void addCorsMappings(CorsRegistry registry) {
//    registry.addMapping("/**")
//        .allowedOrigins("http://localhost:3000", "http://localhost:8080",
//            "https://api.kmorgan.co.kr", "https://www.kmorgan.co.kr", "https://kmorgan.co.kr")
//        .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")
//        .allowedHeaders("*")
//        .allowCredentials(true)
//        .maxAge(3600);
//  }
}
