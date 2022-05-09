package com.jocoos.mybeautip.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMethod;
//import springfox.documentation.builders.ApiInfoBuilder;
//import springfox.documentation.builders.PathSelectors;
//import springfox.documentation.builders.RequestHandlerSelectors;
//import springfox.documentation.builders.ResponseMessageBuilder;
//import springfox.documentation.service.ApiInfo;
//import springfox.documentation.spi.DocumentationType;
//import springfox.documentation.spring.web.plugins.Docket;
//import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * Created by tekhun on 2016. 3. 31..
 */
@Configuration
//@EnableSwagger2
public class SwaggerConfig {

//  @Bean
//  public Docket api() {
//    return new Docket(DocumentationType.SWAGGER_2)
//        .select()
//        .apis(RequestHandlerSelectors.basePackage("com.jocoos.mybeautip"))
//        .paths(PathSelectors.any())
//        .build()
//        .genericModelSubstitutes(ResponseEntity.class)
//        .apiInfo(apiInfo())
//        .useDefaultResponseMessages(false)
//        .globalResponseMessage(RequestMethod.POST,
//            Arrays.asList(
//                new ResponseMessageBuilder().code(400).message("Bad Request").build(),
//                new ResponseMessageBuilder().code(500).message("Internal Server Error").build()));
//  }
//
//  private ApiInfo apiInfo() {
//    return new ApiInfoBuilder()
//        .title("Transcoder-server REST API")
//        .description("This documents describes about mybeautip-server api.")
//        .version("2.0")
//        .build();
//  }
}
