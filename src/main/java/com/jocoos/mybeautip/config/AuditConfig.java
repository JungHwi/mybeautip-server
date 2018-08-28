package com.jocoos.mybeautip.config;

import java.util.Optional;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;

import com.jocoos.mybeautip.audit.AuditorAwareImpl;
import com.jocoos.mybeautip.member.Member;

@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorAware")
public class AuditConfig {

  @Bean
  public AuditorAware<Member> auditorAware() {
    return new AuditorAwareImpl();
  }
}
