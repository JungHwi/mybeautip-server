package com.jocoos.mybeautip.config;

import javax.annotation.PostConstruct;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import com.google.common.collect.Lists;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Getter
@Slf4j
@Configuration
@ConfigurationProperties("ignore")
public class IgnoreConfig {

  private List<String> usernames = Lists.newArrayList();

  @PostConstruct
  public void postConstruct() {
    log.debug("usernames: {}", usernames);
  }
}
