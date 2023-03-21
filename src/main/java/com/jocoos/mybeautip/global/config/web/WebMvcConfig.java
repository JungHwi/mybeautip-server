package com.jocoos.mybeautip.global.config.web;

import com.jocoos.mybeautip.domain.broadcast.annotation.service.CurrentViewerArgumentResolver;
import com.jocoos.mybeautip.global.config.web.formatter.StringToZonedDatedTime;
import com.jocoos.mybeautip.global.resolver.AuthenticationArgumentResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@RequiredArgsConstructor
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    private final AuthenticationArgumentResolver authenticationArgumentResolver;
    private final CurrentViewerArgumentResolver currentViewerArgumentResolver;

    @Override
    public void addFormatters(FormatterRegistry formatterRegistry) {
        formatterRegistry.addFormatter(new StringToZonedDatedTime());
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(authenticationArgumentResolver);
        resolvers.add(currentViewerArgumentResolver);
    }
}
