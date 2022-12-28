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
    public void configure(ResourceServerSecurityConfigurer resources) {
        resources.resourceId(RESOURCE_ID).stateless(false);
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/test/**").permitAll()
                .antMatchers("/admin/**").hasRole("ADMIN")
                .antMatchers("/batch/**").hasRole("ADMIN")
                .antMatchers(POST, "/api/*/member/signup/**").hasAnyRole("GUEST")
                .antMatchers(PATCH, "/api/*/video/*/view-count").hasAnyRole("GUEST", "USER", "ADMIN")
                .antMatchers(PATCH, "/api/*/member/wakeup").hasAnyRole("GUEST")
                .antMatchers(GET, "/api/*/words").hasAnyRole("USER", "ADMIN")
                .antMatchers("/api/**/notification").hasAnyRole("USER", "ADMIN")
                .antMatchers("/api/admin/**").hasAnyRole("STORE", "ADMIN")
                .antMatchers(GET, "/api/*/notices").permitAll()
                .antMatchers(GET, "/api/*/popup").permitAll()
                .antMatchers(GET, "/api/*/my/**").hasAnyRole("USER", "ADMIN")
                .antMatchers("/api/*/point/**").hasAnyRole("USER", "ADMIN")
                .antMatchers(POST, "/api/*/members/me/carts/now").hasAnyRole("GUEST", "USER", "ADMIN")
                .antMatchers(GET, "/api/*/orders/complete").permitAll()
                .antMatchers(GET, "/api/*/members/me/**").hasAnyRole("USER", "ADMIN")
                .antMatchers(GET, "/api/*/keys/**").hasAnyRole("USER", "ADMIN")
                .antMatchers(GET, "/api/**").hasAnyRole("GUEST", "USER", "ADMIN")
                .antMatchers(POST, "/api/*/posts/**/view_count").hasAnyRole("GUEST", "USER", "ADMIN")
                .antMatchers(POST, "/api/*/videos/**/view_count").hasAnyRole("GUEST", "USER", "ADMIN")
                .antMatchers(POST, "/api/*/videos/**/watches").hasAnyRole("GUEST", "USER", "ADMIN")
                .antMatchers(POST, "/api/**").hasAnyRole("USER", "ADMIN")
                .antMatchers(GET, "/api/*/posts").hasAnyRole("GUEST", "USER", "ADMIN")
                .antMatchers(GET, "/api/*/banners/**").hasAnyRole("GUEST", "USER", "ADMIN")
                .antMatchers(PATCH, "/api/*/videos/**/watches").hasAnyRole("GUEST", "USER", "ADMIN")
                .antMatchers(PATCH, "/api/*/categories/**").hasRole("ADMIN")
                .antMatchers(PATCH, "/api/*/stores/**").hasRole("ADMIN")
                .antMatchers(PATCH, "/api/**").hasAnyRole("USER", "ADMIN")
                .antMatchers(PUT, "/api/*/devices").hasAnyRole("GUEST", "USER", "ADMIN")
                .antMatchers(PUT, "/api/**").hasAnyRole("USER", "ADMIN")
                .antMatchers(DELETE, "/api/*/videos/**/watches").hasAnyRole("GUEST", "USER", "ADMIN")
                .antMatchers(DELETE, "/api/**").hasAnyRole("USER", "ADMIN")
                .antMatchers(GET, "/api/*/community/**").hasAnyRole("GUEST", "USER")
                .antMatchers(POST, "/api/*/community/**").hasAnyRole("USER")
                .antMatchers(PUT, "/api/*/community/**").hasAnyRole("USER")
                .antMatchers(PATCH, "/api/*/community/**").hasAnyRole("USER")
                .antMatchers(DELETE, "/api/*/community/**").hasAnyRole("USER")
                .antMatchers("/api/*/callbacks/video").hasRole("ADMIN");
    }
}
