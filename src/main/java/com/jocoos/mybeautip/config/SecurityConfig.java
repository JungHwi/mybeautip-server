package com.jocoos.mybeautip.config;

import com.jocoos.mybeautip.security.MybeautipUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.springframework.http.HttpMethod.OPTIONS;

@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

  @Autowired
  private MybeautipUserDetailsService userDetailsService;

  @Autowired
  private PasswordEncoder passwordEncoder;

  @Bean(name = BeanIds.AUTHENTICATION_MANAGER)

  @Override
  public AuthenticationManager authenticationManagerBean() throws Exception {
    return super.authenticationManagerBean();
  }

  @Override
  protected void configure(AuthenticationManagerBuilder auth) throws Exception {
    auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder);
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http
        .requestMatchers()
        .antMatchers(OPTIONS, "/api/1/token")
        .and()
        .authorizeRequests()
        .antMatchers(OPTIONS, "/api/1/token").permitAll()
        .and()
        .csrf().disable();
  }
}
