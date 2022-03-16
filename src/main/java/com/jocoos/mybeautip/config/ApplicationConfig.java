package com.jocoos.mybeautip.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.xml.MappingJackson2XmlHttpMessageConverter;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.filter.CommonsRequestLoggingFilter;

import java.util.Properties;

import com.jocoos.mybeautip.support.RestTemplateResponseErrorHandler;
import com.jocoos.mybeautip.support.S3StorageService;
import com.jocoos.mybeautip.support.StorageService;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.xml.ser.ToXmlGenerator;
import lombok.extern.slf4j.Slf4j;
import org.h2.server.web.WebServlet;

@Slf4j
@Configuration
public class ApplicationConfig {
  @Value("${mybeautip.smtp.user}")
  private String mailUser;

  @Value("${mybeautip.smtp.pass}")
  private String mailPass;

  private static final int CONNECTION_TIMEOUT = 60 * 1000;

  @Autowired
  private RestTemplateResponseErrorHandler restTemplateResponseErrorHandler;

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
    return new ServletRegistrationBean<>(new WebServlet(), "/console/*");
  }

  @Bean
  public RestTemplate restTemplate() {
    RestTemplate restTemplate = new RestTemplate();
    HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
    requestFactory.setConnectTimeout(CONNECTION_TIMEOUT);
    requestFactory.setReadTimeout(CONNECTION_TIMEOUT);
    restTemplate.setRequestFactory(requestFactory);
    restTemplate.setErrorHandler(restTemplateResponseErrorHandler);
    return restTemplate;
  }

  @Bean
  public CommonsRequestLoggingFilter requestLoggingFilter() {
    CommonsRequestLoggingFilter requestLoggingFilter = new CommonsRequestLoggingFilter();
    requestLoggingFilter.setIncludeClientInfo(true);
    requestLoggingFilter.setIncludePayload(true);
    requestLoggingFilter.setIncludeQueryString(true);
    return requestLoggingFilter;
  }

  @Bean
  public JavaMailSender getJavaMailSender() {
    JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
    mailSender.setHost("smtp.gmail.com");
    mailSender.setPort(587);

    mailSender.setUsername(mailUser);
    mailSender.setPassword(mailPass);

    Properties props = mailSender.getJavaMailProperties();
    props.put("mail.transport.protocol", "smtp");
    props.put("mail.smtp.auth", "true");
    props.put("mail.smtp.starttls.required", "true");
    props.put("mail.smtp.starttls.enable", "true");
    props.put("mail.debug", "false");

    return mailSender;
  }

  @Bean
  public StorageService storageService() {
    return new S3StorageService();
  }
}
