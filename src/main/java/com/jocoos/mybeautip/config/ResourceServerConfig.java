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
        .regexMatchers(GET, "/api/1/members/\\d+").permitAll()
        .regexMatchers(GET, "/api/1/members/\\d+/broadcasts.*").permitAll()
        .antMatchers(PUT, "/api/1/devices").permitAll()
        .antMatchers(GET, "/api/1/members/**/followings").permitAll()
        .antMatchers(GET, "/api/1/members/**/followers").permitAll()
        .antMatchers(GET, "/api/1/members/**/products").permitAll()
        .antMatchers(GET, "/api/1/members/**/news").permitAll()
        .antMatchers(GET, "/api/1/members/**/broadcast_count").permitAll()
        .antMatchers(GET, "/api/1/broadcasts", "/api/1/broadcasts/**").permitAll()
        .antMatchers(GET, "/api/1/advertisements", "/api/1/advertisements/**").permitAll()
        .antMatchers(GET, "/api/1/commercials", "/api/1/commercials/**").permitAll()
        .antMatchers(GET, "/api/1/goods", "/api/1/goods/**").permitAll()
        .antMatchers(GET, "/api/1/categories", "/api/1/categories/**").permitAll()
        .antMatchers(GET, "/api/1/payments", "/api/1/payments/**").permitAll()
        .antMatchers(GET, "/api/1/orders", "/api/1/orders/**").permitAll()
        .antMatchers(GET, "/api/1/recommendations", "/api/1/recommendations/**").permitAll()
        .antMatchers(GET, "/api/1/searches", "/api/1/searches/**").permitAll()
        .antMatchers(GET, "/api/1/news", "/api/1/news/**").permitAll()
        .antMatchers(POST, "/api/1/payments", "/api/1/payments/**").permitAll()
        .antMatchers("/api/admin/**").access("#oauth2.hasScope('admin')")
        .antMatchers(GET, "/api/**").access("#oauth2.hasScope('read')")
        .antMatchers(POST, "/api/**").access("#oauth2.hasScope('write')")
        .antMatchers(PATCH, "/api/**").access("#oauth2.hasScope('write')")
        .antMatchers(PUT, "/api/**").access("#oauth2.hasScope('write')")
        .antMatchers(DELETE, "/api/**").access("#oauth2.hasScope('write')");
  }
}
