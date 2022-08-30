package com.jocoos.mybeautip.global.config.web;

import com.jocoos.mybeautip.global.config.web.formatter.StringToZonedDatedTime;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addFormatters(FormatterRegistry formatterRegistry) {
        formatterRegistry.addFormatter(new StringToZonedDatedTime());
    }
}
