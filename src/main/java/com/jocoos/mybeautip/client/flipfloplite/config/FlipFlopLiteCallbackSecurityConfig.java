package com.jocoos.mybeautip.client.flipfloplite.config;

import com.jocoos.mybeautip.client.flipfloplite.filter.FFLCallbackFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FlipFlopLiteCallbackSecurityConfig {

    @Bean
    public FilterRegistrationBean<FFLCallbackFilter> fflCallbackFilterFilterRegistrationBean(@Autowired FFLCallbackFilter fflCallbackFilter) {
        FilterRegistrationBean<FFLCallbackFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(fflCallbackFilter);
        registrationBean.addUrlPatterns("/callback/ffl");
        return registrationBean;
    }

}
