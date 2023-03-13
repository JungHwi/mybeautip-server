package com.jocoos.mybeautip.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.jocoos.mybeautip.security.InternalAuthenticationFilter;

@Configuration
public class InternalFilterConfig {

  @Autowired
  private InternalAuthenticationFilter internalAuthenticationFilter;

  @Bean
  public FilterRegistrationBean<InternalAuthenticationFilter> filter() {
    FilterRegistrationBean registration = new FilterRegistrationBean();
    registration.setFilter(internalAuthenticationFilter);
    registration.addUrlPatterns("/internal/*");
    registration.setName("internalAuthFilter");
    registration.setOrder(1);
    return registration;
  }
}
