package com.jocoos.mybeautip.config;

import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.xml.MappingJackson2XmlHttpMessageConverter;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.filter.CommonsRequestLoggingFilter;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.xml.ser.ToXmlGenerator;
import lombok.extern.slf4j.Slf4j;
import org.h2.server.web.WebServlet;

@Configuration
@Slf4j
public class ApplicationConfig {

  private static final int CONNECTION_TIMEOUT = 1 * 1000;

  @Bean
  public MappingJackson2XmlHttpMessageConverter xmlHttpMessageConverter() {
    MappingJackson2XmlHttpMessageConverter xmlHttpMessageConverter = new MappingJackson2XmlHttpMessageConverter(xmlMapper());
    xmlHttpMessageConverter.setPrettyPrint(true);
    return xmlHttpMessageConverter;
  }

  private XmlMapper xmlMapper() {
    XmlMapper xmlMapper = new XmlMapper();
    xmlMapper.configure(ToXmlGenerator.Feature.WRITE_XML_DECLARATION, true);
    xmlMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    return xmlMapper;
  }

  @Bean
  public ServletRegistrationBean h2servletRegistration(){
    return new ServletRegistrationBean<WebServlet>(new WebServlet(), "/console/*");
  }

  @Bean
  public RestTemplate restTemplate() {
    RestTemplate restTemplate = new RestTemplate();
    HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
    requestFactory.setConnectTimeout(CONNECTION_TIMEOUT);
    requestFactory.setReadTimeout(CONNECTION_TIMEOUT);
    restTemplate.setRequestFactory(requestFactory);

    return restTemplate;
  }

  @Bean
  public CommonsRequestLoggingFilter requestLoggingFilter() {
    CommonsRequestLoggingFilter reqeustLoggingFilter = new CommonsRequestLoggingFilter();
    reqeustLoggingFilter.setIncludeClientInfo(true);
    reqeustLoggingFilter.setIncludePayload(true);
    reqeustLoggingFilter.setIncludeQueryString(true);
    return reqeustLoggingFilter;
  }
}
