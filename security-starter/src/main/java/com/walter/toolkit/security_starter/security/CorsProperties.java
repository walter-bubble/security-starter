package com.walter.toolkit.security_starter.security;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

import java.util.List;

@ConfigurationProperties(prefix = "toolkit.security.cors")
public record CorsProperties(
        @DefaultValue({"http://localhost:3000", "http://localhost:5173"})
        List<String> allowedOrigins
) {}
