package com.walter.toolkit.security_starter.config;

import com.walter.toolkit.security_starter.auth.AuthController;
import com.walter.toolkit.security_starter.auth.AuthService;
import com.walter.toolkit.security_starter.security.CorsProperties;
import com.walter.toolkit.security_starter.security.JwtProperties;
import com.walter.toolkit.security_starter.security.JwtService;
import com.walter.toolkit.security_starter.security.RateLimitProperties;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.security.authentication.AuthenticationManager;

@AutoConfiguration
@ComponentScan(basePackages = "com.walter.toolkit.security_starter")
@EnableConfigurationProperties({JwtProperties.class,RateLimitProperties.class, CorsProperties.class})
public class SecurityToolkitAutoConfiguration {
    @Bean
    @ConditionalOnMissingBean(name = "authService")
    public AuthService authService(AuthenticationManager authenticationManager, JwtService jwtService) {
        return new AuthService(authenticationManager, jwtService);
    }

    @Bean
    @ConditionalOnMissingBean(name = "authController")
    public AuthController authController(AuthService authService) {
        return new AuthController(authService);
    }
}

