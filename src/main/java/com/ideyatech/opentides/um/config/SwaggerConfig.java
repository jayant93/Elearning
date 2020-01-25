package com.ideyatech.opentides.um.config;

import java.util.List;

import org.springframework.context.annotation.Bean;

import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.validation.MessageCodesResolver;
import org.springframework.validation.Validator;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.config.annotation.AsyncSupportConfigurer;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.ViewResolverRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.google.common.base.Predicates;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;




@EnableSwagger2
@Configuration
//@PropertySource("classpath:/swagger.properties")
public class SwaggerConfig implements WebMvcConfigurer {

@Bean
public Docket proposalApis(){
    return new Docket(DocumentationType.SWAGGER_2)
        .select()
           // .apis(RequestHandlerSelectors.any())
        .paths(PathSelectors.any())
            .apis(RequestHandlerSelectors.basePackage("com.ideyatech.opentides.um.controller"))
        .build()
        .apiInfo(testApiInfo());
}

private ApiInfo testApiInfo() {
    ApiInfo apiInfo = new ApiInfoBuilder().title("Elearning APIs").description("GET POST PUT methods are supported ").version("V1").build();
    return apiInfo;
}
@Override
public void addResourceHandlers(ResourceHandlerRegistry registry) {

    registry
            .addResourceHandler("swagger-ui.html")
            .addResourceLocations("classpath:/META-INF/resources/");

    registry
            .addResourceHandler("/webjars/**")
            .addResourceLocations("classpath:/META-INF/resources/webjars/");
}

@Override
public void configurePathMatch(PathMatchConfigurer configurer) {
	// TODO Auto-generated method stub
	
}

@Override
public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
	// TODO Auto-generated method stub
	
}

@Override
public void configureAsyncSupport(AsyncSupportConfigurer configurer) {
	// TODO Auto-generated method stub
	
}

@Override
public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
	// TODO Auto-generated method stub
	
}

@Override
public void addFormatters(FormatterRegistry registry) {
	// TODO Auto-generated method stub
	
}

@Override
public void addInterceptors(InterceptorRegistry registry) {
	// TODO Auto-generated method stub
	
}

@Override
public void addCorsMappings(CorsRegistry registry) {
	// TODO Auto-generated method stub
	
}

@Override
public void addViewControllers(ViewControllerRegistry registry) {
	// TODO Auto-generated method stub
	
}

@Override
public void configureViewResolvers(ViewResolverRegistry registry) {
	// TODO Auto-generated method stub
	
}

@Override
public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
	// TODO Auto-generated method stub
	
}

@Override
public void addReturnValueHandlers(List<HandlerMethodReturnValueHandler> returnValueHandlers) {
	// TODO Auto-generated method stub
	
}

@Override
public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
	// TODO Auto-generated method stub
	
}

@Override
public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
	// TODO Auto-generated method stub
	
}

@Override
public void configureHandlerExceptionResolvers(List<HandlerExceptionResolver> exceptionResolvers) {
	// TODO Auto-generated method stub
	
}

@Override
public void extendHandlerExceptionResolvers(List<HandlerExceptionResolver> exceptionResolvers) {
	// TODO Auto-generated method stub
	
}

@Override
public Validator getValidator() {
	// TODO Auto-generated method stub
	return null;
}

@Override
public MessageCodesResolver getMessageCodesResolver() {
	// TODO Auto-generated method stub
	return null;
}

}