package com.walter.toolkit.security_starter.config;

import com.walter.toolkit.security_starter.security.CorsProperties;
import com.walter.toolkit.security_starter.security.JwtProperties;
import com.walter.toolkit.security_starter.security.RateLimitProperties;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;

@AutoConfiguration
@ComponentScan(basePackages = "com.walter.toolkit.security_starter")
@EnableConfigurationProperties({JwtProperties.class,RateLimitProperties.class, CorsProperties.class})
public class SecurityToolkitAutoConfiguration {
}
