package com.jocoos.mybeautip.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;

import static org.springframework.http.HttpMethod.*;

@Configuration
@EnableResourceServer
public class ResourceServerConfig extends ResourceServerConfigurerAdapter {

  static final String RESOURCE_ID = "mybeautip-resource-server";

  @Bean
  public BCryptPasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Override
  public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
    resources.resourceId(RESOURCE_ID).stateless(false);
  }

  @Override
  public void configure(HttpSecurity http) throws Exception {
    http.authorizeRequests()
      .antMatchers("/api/admin/**").hasRole("ADMIN")
      .antMatchers(GET, "/api/1/orders/complete").permitAll()
      .antMatchers(GET, "/api/1/members/me/**").hasAnyRole("USER", "ADMIN")
      .antMatchers(GET, "/api/1/keys/**").hasAnyRole("USER", "ADMIN")
      .antMatchers(GET, "/api/**").hasAnyRole("GUEST", "USER", "ADMIN")
      .antMatchers(POST, "/api/1/posts/**/view_count").hasAnyRole("GUEST", "USER", "ADMIN")
      .antMatchers(POST, "/api/1/videos/**/view_count").hasAnyRole("GUEST", "USER", "ADMIN")
      .antMatchers(POST, "/api/1/videos/**/watches").hasAnyRole("GUEST", "USER", "ADMIN")
      .antMatchers(POST, "/api/**").hasAnyRole("USER", "ADMIN")
      .antMatchers(PATCH, "/api/1/videos/**/watches").hasAnyRole("GUEST", "USER", "ADMIN")
      .antMatchers(PATCH, "/api/1/categories/**").hasRole("ADMIN")
      .antMatchers(PATCH, "/api/1/stores/**").hasRole("ADMIN")
      .antMatchers(PATCH, "/api/**").hasAnyRole("USER", "ADMIN")
      .antMatchers(PUT, "/api/1/devices").hasAnyRole("GUEST", "USER", "ADMIN")
      .antMatchers(PUT, "/api/**").hasAnyRole("USER", "ADMIN")
      .antMatchers(DELETE, "/api/1/videos/**/watches").hasAnyRole("GUEST", "USER", "ADMIN")
      .antMatchers(DELETE, "/api/**").hasAnyRole("USER", "ADMIN")
      .antMatchers("/api/1/callbacks/video").hasRole("ADMIN");
  }
}
