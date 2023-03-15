package com.jocoos.mybeautip.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

import lombok.Data;

@Data
@Configuration
@ConfigurationProperties("mybeautip.internal")
public class InternalConfig extends WebSecurityConfigurerAdapter  {

  private String accessToken;
  private boolean debug;
}
