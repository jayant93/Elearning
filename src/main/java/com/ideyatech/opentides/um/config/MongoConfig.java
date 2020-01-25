package com.ideyatech.opentides.um.config;

import com.ideyatech.opentides.mongo.converter.AuditLogConverter;
import com.ideyatech.opentides.mongo.converter.StringToClassConverter;
import com.ideyatech.opentides.mongo.repository.impl.BaseEntityMongoRepositoryHelper;
import com.ideyatech.opentides.mongo.repository.impl.BaseEntityRepositoryMongoImpl;
import com.ideyatech.opentides.um.Application;
import com.ideyatech.opentides.um.security.UsernamePasswordAuthenticationProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.support.GenericConversionService;
import org.springframework.data.mongodb.core.convert.CustomConversions;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import java.util.ArrayList;
import java.util.List;

/**
 * Configuration for mongo database.
 *
 * @author Gino
 */
@Configuration
@Profile("mongo")
@EnableMongoRepositories(basePackages = {
        "com.ideyatech.opentides.mongo.repository",
        "com.ideyatech.opentides.um.repository.mongo"
}, repositoryImplementationPostfix = "CustomImpl",
        repositoryBaseClass = BaseEntityRepositoryMongoImpl.class
)
public class MongoConfig {
   // private MongoDatabase db;
   private final List<Converter<?, ?>> converters = new ArrayList<Converter<?, ?>>();

    @Autowired
    @Qualifier("defaultConversionService")
    private GenericConversionService genericConversionService;

    public MongoConfig() {
        System.out.println("INITIALIZING MONGO DB!");
        Application.setEntityIdType(String.class);
    }

    @Bean
    public BaseEntityMongoRepositoryHelper baseEntityMongoRepositoryHelper() {
        return new BaseEntityMongoRepositoryHelper();
    }

    @Bean
   public CustomConversions customConversions(){
        converters.add(new AuditLogConverter());
        //converters.add(new StringToClassConverter());
        return new CustomConversions(converters);
    }

    @Bean
    public UsernamePasswordAuthenticationProvider usernamePasswordAuthenticationProvider() {
        UsernamePasswordAuthenticationProvider authenticationProvider =
                new UsernamePasswordAuthenticationProvider();
        authenticationProvider.setLoadGroupsFromDb(true);
        return authenticationProvider;
    }

    @Bean
    public StringToClassConverter stringToClassConverter() {
        System.out.println("Mongo DB Post Construct");
        StringToClassConverter converter = new StringToClassConverter();
        genericConversionService.addConverter(converter);
        return converter;
    }


}
