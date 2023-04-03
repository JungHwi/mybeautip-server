package com.jocoos.mybeautip.client.flipfloplite.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.jocoos.mybeautip.global.config.feign.FeignClientConfig;
import feign.RequestInterceptor;
import feign.codec.Encoder;
import feign.codec.ErrorDecoder;
import feign.form.spring.SpringFormEncoder;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.openfeign.FeignFormatterRegistrar;
import org.springframework.cloud.openfeign.support.SpringEncoder;
import org.springframework.context.annotation.Bean;
import org.springframework.format.datetime.standard.DateTimeFormatterRegistrar;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import java.text.SimpleDateFormat;

import static com.jocoos.mybeautip.global.constant.LocalDateTimeConstant.FFL_ZONE_DATE_TIME_FORMAT;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

public class FlipFlopLiteClientConfig extends FeignClientConfig {

    @Bean
    public FeignFormatterRegistrar zonedDateFeignFormatterRegister() {
        return registry -> {
            DateTimeFormatterRegistrar registrar = new DateTimeFormatterRegistrar();
            registrar.setUseIsoFormat(true);
            registrar.registerFormatters(registry);
        };
    }

//    @Bean
//    public FlipFlopLiteErrorDecoder fflDecoder() {
//        return new FlipFlopLiteErrorDecoder();
//    }

    @Bean
    public ErrorDecoder errorDecoder() {
        return new FlipFlopLiteErrorDecoder();
    }

    @Bean
    public RequestInterceptor requestInterceptor() throws InterruptedException {
        return requestTemplate -> requestTemplate.header(AUTHORIZATION, FlipFlopLiteProperties.getBasicToken());
    }
//
//    @Bean
//    public Decoder FflDecoder() {
//        HttpMessageConverter jacksonConverter = new MappingJackson2HttpMessageConverter(customObjectMapper());
//        ObjectFactory<HttpMessageConverters> objectFactory = () -> new HttpMessageConverters(jacksonConverter);
//        return new ResponseEntityDecoder(new SpringDecoder(objectFactory));
//    }
//
    @Bean
    public Encoder FflEncoder() {
        HttpMessageConverter jacksonConverter = new MappingJackson2HttpMessageConverter(customObjectMapper());
        ObjectFactory<HttpMessageConverters> objectFactory = () -> new HttpMessageConverters(jacksonConverter);
        return new SpringEncoder(new SpringFormEncoder(), objectFactory);
    }

    public ObjectMapper customObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setPropertyNamingStrategy(PropertyNamingStrategies.LOWER_CAMEL_CASE);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.setDateFormat(new SimpleDateFormat(FFL_ZONE_DATE_TIME_FORMAT));
        return objectMapper;
    }
}
