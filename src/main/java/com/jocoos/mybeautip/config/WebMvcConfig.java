package com.jocoos.mybeautip.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

  @Override
  public void addCorsMappings(CorsRegistry registry) {
    registry
       .addMapping("/api/admin")
       .allowedOrigins("localhost:3000")
       .allowedMethods("*")
       .allowedHeaders("Authorization", "Content-Type")
       .allowCredentials(false)
       .maxAge(3600);
  }
}
