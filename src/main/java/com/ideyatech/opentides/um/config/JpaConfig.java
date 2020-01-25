package com.ideyatech.opentides.um.config;

import com.ideyatech.opentides.core.repository.BaseEntityRepositoryHelper;
import com.ideyatech.opentides.core.repository.impl.BaseEntityRepositoryJpaImpl;
import com.ideyatech.opentides.core.repository.jpa.BaseEntityRepositoryHelperJpaImpl;
import com.ideyatech.opentides.um.Application;
import com.ideyatech.opentides.um.security.UsernamePasswordAuthenticationProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Configuration when using Spring Data JPA
 *
 * @author Gino
 *
 */
@Configuration
@Profile("jpa")
@EnableJpaRepositories(basePackages = {
        "com.ideyatech.opentides.core.repository.jpa",
        "com.ideyatech.opentides.um.repository.jpa",
        "com.gamify.elearning.repository.jpa"
}, repositoryImplementationPostfix = "CustomImpl",
        repositoryBaseClass = BaseEntityRepositoryJpaImpl.class)
public class JpaConfig {

    @Bean
    public UsernamePasswordAuthenticationProvider usernamePasswordAuthenticationProvider() {
        return new UsernamePasswordAuthenticationProvider();
    }

    public JpaConfig() {
        Application.setEntityIdType(String.class);
    }

    @Bean
    public BaseEntityRepositoryHelper baseEntityRepositoryHelper() {
        return new BaseEntityRepositoryHelperJpaImpl();
    }

}
