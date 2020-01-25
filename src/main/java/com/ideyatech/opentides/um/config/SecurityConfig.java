package com.ideyatech.opentides.um.config;

import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.ideyatech.opentides.core.security.JwtAuthenticationEntryPoint;
import com.ideyatech.opentides.core.security.JwtAuthenticationFilter;
import com.ideyatech.opentides.core.security.JwtAuthenticationProvider;
import com.ideyatech.opentides.core.security.JwtAuthenticationSuccessHandler;
import com.ideyatech.opentides.um.validator.Ot4RulesPasswordValidator;
import com.ideyatech.opentides.um.validator.PasswordValidator;

/**
 * @author Gino
 */
@Configuration
@EnableWebSecurity
@EnableAutoConfiguration
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter
{

    private static final Logger LOGGER = LoggerFactory.getLogger(SecurityConfig.class);

    @Autowired
    private JwtAuthenticationEntryPoint unauthorizedHandler;

    @Autowired
    private JwtAuthenticationProvider authenticationProvider;
    
    @Autowired
    private ApplicationContext applicationContext;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManager() throws Exception {
        return new ProviderManager(Arrays.asList(authenticationProvider));
    }

    @Bean
    public PasswordValidator passwordValidator() {
        return new Ot4RulesPasswordValidator();
    }

    //@Bean
    public JwtAuthenticationFilter authenticationFilterBean() throws Exception {
    	JwtAuthenticationFilter authenticationFilter = new JwtAuthenticationFilter();
        authenticationFilter.setAuthenticationManager(authenticationManager());
        authenticationFilter.setAuthenticationSuccessHandler(new JwtAuthenticationSuccessHandler());
        return authenticationFilter;
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web
            .ignoring()
            .antMatchers("/images/**")
            .antMatchers("/", "/csrf", "/v2/api-docs", "/swagger-resources/configuration/ui", 
            		"/configuration/ui", "/swagger-resources", 
            		"/swagger-resources/configuration/security", 
            		"/configuration/security", "/swagger-ui.html", "/webjars/**","/swagger.json")
             //Open resources for front end
            .antMatchers("/api/google","/api/user/registerByUser","/api/authority/create-authz","/api/authority/**","/api/company/**",
            		"api/usergroup/usergroup-create","/api/user/**")
            .antMatchers("/api/course/add","/api/course/**")
            .antMatchers("/api/video/**","/uploadFile","/upload-vimeo","/changeDescription","/GetUnitFile")
            .antMatchers(HttpMethod.GET, "/", "/home", "/**.html", "/css/**", "/images/**", "/js/**", "/main/**" )
            .antMatchers(HttpMethod.POST, "/api/application")
            .antMatchers(HttpMethod.OPTIONS, "/**") //TODO Think of a better way to handle preflights.
            .antMatchers("/api", "/api/login", "/api/login/fb", "/api/logout", "/api/*/search",
                    "/api/user/reset-password", "/um-ws/**", "/api/file-info/get/*", "/api/login/google",
                    "/api/user/activateByUser/**", "/api/user/register","/api/user/registerByUser", "/api/user/register/**", "/api/refreshToken", "/api/recaptcha");

    }

	/*
	 * @Override protected void configure(HttpSecurity http) throws Exception { http
	 * .csrf().disable()
	 * 
	 * .authorizeRequests() .antMatchers(HttpMethod.GET, "/api/application/{id}")
	 * .hasAuthority("MANAGE_APPLICATION") .antMatchers(HttpMethod.POST,
	 * "/api/user") .hasAuthority("MANAGE_USER") .antMatchers(HttpMethod.PUT,
	 * "/api/user") .hasAuthority("MANAGE_USER") .anyRequest().authenticated()
	 * .and() .exceptionHandling().authenticationEntryPoint(unauthorizedHandler)
	 * .and()
	 * .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
	 * 
	 * http.addFilterBefore(authenticationFilterBean(),
	 * UsernamePasswordAuthenticationFilter.class);
	 * 
	 * http.headers().cacheControl(); }
	 */
}
