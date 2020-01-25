package com.ideyatech.opentides.um;

import java.io.IOException;
import java.util.Properties;

import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.exception.VelocityException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.web.HttpMessageConverters;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import org.springframework.core.task.TaskExecutor;
import org.springframework.data.projection.SpelAwareProxyProjectionFactory;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.ResourceHttpMessageConverter;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.ui.velocity.VelocityEngineFactoryBean;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import com.ideyatech.opentides.core.repository.impl.BaseRestResourceProcessor;
import com.ideyatech.opentides.core.service.StorageService;
import com.ideyatech.opentides.core.service.impl.StorageServiceFileSystemImpl;

/**
 * Created by Gino on 8/25/2016.
 */
@Configuration
//@EnableAutoConfiguration
@EntityScan(basePackages = {
        "com.ideyatech.opentides.core.entity",
        "com.ideyatech.opentides.um.entity",
        "com.gamify.elearning.entity",
        "com.ideyatech.opentides.um.repository"
})

@ComponentScan(basePackages = {
        "com.ideyatech.opentides.mongo",
        "com.ideyatech.opentides",
        "com.gamify.elearning"})

//@ComponentScan(basePackages = {
//       /* "com.ideyatech.opentides.mongo",*/
//        "com.ideyatech.opentides.um.controller",
//        "com.gamify.elearning.controller"})
@EnableAsync

public class Application {
	
    private static Class entityIdType;

    @Bean
    public BaseRestResourceProcessor baseRestResourceProcessor() {
        return new BaseRestResourceProcessor();
    }

    @Bean
    public FilterRegistrationBean corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    	CorsConfiguration config = new CorsConfiguration();
    	config.setAllowCredentials(true); // you USUALLY want this
        config.addAllowedOrigin("*");
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");
        source.registerCorsConfiguration("/**", config);

        FilterRegistrationBean bean = new FilterRegistrationBean(new CorsFilter(source));
        bean.setOrder(0);

        return bean;
    }

    @Bean
    public StorageService fileSystemStorageService() {
        return new StorageServiceFileSystemImpl();
    }

    @Bean
    public HttpMessageConverters customConverters() {
        ByteArrayHttpMessageConverter arrayHttpMessageConverter = new ByteArrayHttpMessageConverter();
        ResourceHttpMessageConverter resourceHttpMessageConverter = new ResourceHttpMessageConverter();
        HttpMessageConverters converters = new HttpMessageConverters(arrayHttpMessageConverter, resourceHttpMessageConverter);
        return converters;
    }

    public static Class getEntityIdType() {
        return entityIdType;
    }

    public static void setEntityIdType(Class entityIdType) {
        if(Application.entityIdType == null) {
            Application.entityIdType = entityIdType;
        }
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
    
    @Bean
    public SpelAwareProxyProjectionFactory projectionFactory() {
      return new SpelAwareProxyProjectionFactory();
    }
    
	@Bean
    public JavaMailSender javaMailService() {
        JavaMailSenderImpl javaMailSender = new JavaMailSenderImpl();
        return javaMailSender;
    }

    @Bean
    public SimpleMailMessage simpleMailMessage() {
       SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
       return simpleMailMessage;
    }
    
    @Bean
    public TaskExecutor taskExecutor() {
    	TaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
		((ThreadPoolTaskExecutor)taskExecutor).setCorePoolSize(5);
		((ThreadPoolTaskExecutor)taskExecutor).setMaxPoolSize(10);
		((ThreadPoolTaskExecutor)taskExecutor).setQueueCapacity(25);
		((ThreadPoolTaskExecutor)taskExecutor).setWaitForTasksToCompleteOnShutdown(true);
		return taskExecutor;
    }
    
    @Bean
    public VelocityEngine velocityEngine() throws VelocityException, IOException{
    	VelocityEngineFactoryBean factory = new VelocityEngineFactoryBean();
    	Properties props = new Properties();
    	props.put("resource.loader", "class");
    	props.put("class.resource.loader.class",
    			  "org.apache.velocity.runtime.resource.loader." +
    			  "ClasspathResourceLoader");
    	factory.setVelocityProperties(props);

    	return factory.createVelocityEngine();
    }

}
