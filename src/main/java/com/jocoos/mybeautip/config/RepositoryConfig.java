package com.jocoos.mybeautip.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurerAdapter;

import com.jocoos.mybeautip.post.Post;
import com.jocoos.mybeautip.post.Trend;

@Configuration
public class RepositoryConfig extends RepositoryRestConfigurerAdapter {

  @Override
  public void configureRepositoryRestConfiguration(RepositoryRestConfiguration config) {
    config.exposeIdsFor(Post.class, Trend.class);
  }
}
