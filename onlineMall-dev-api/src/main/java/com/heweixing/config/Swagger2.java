package com.heweixing.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class Swagger2 {

    //配置swagger核心配置docket
    @Bean
    public Docket createRestApi(){
        return new Docket(DocumentationType.SWAGGER_2)
                    .apiInfo(apiInfo())
                    .select()
                    .apis(RequestHandlerSelectors.basePackage("com.heweixing.controller"))   //扫描controller
                    .paths(PathSelectors.any())    //所有controller
                    .build();
    }



    public ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("天天吃货Api文档接口")
                .contact(new Contact("heweixing", "https://www.imooc.com", "abc.@heweixng.com"))
                .description("专为天天吃货提供API文档")
                .version("1.0.1")
                .termsOfServiceUrl("https://www.imooc.com")
                .build();
    }

}
