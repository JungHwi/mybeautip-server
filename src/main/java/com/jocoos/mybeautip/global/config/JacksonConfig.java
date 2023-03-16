package com.jocoos.mybeautip.global.config;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.jocoos.mybeautip.global.config.web.RecordNamingStrategyPatchModule;
import lombok.RequiredArgsConstructor;
import org.openapitools.jackson.nullable.JsonNullableModule;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

@RequiredArgsConstructor
@Configuration
public class JacksonConfig {

    private final ObjectMapper objectMapper;

    @PostConstruct
    ObjectMapper jacksonObjectMapper() {
        objectMapper.registerModule(new JsonNullableModule());
        objectMapper.registerModule(new RecordNamingStrategyPatchModule());
        return objectMapper;
    }
}
