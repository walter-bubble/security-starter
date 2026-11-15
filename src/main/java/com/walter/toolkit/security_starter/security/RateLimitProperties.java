package com.walter.toolkit.security_starter.security;

import jakarta.validation.constraints.Min;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "toolkit.security.rate-limit")
public record RateLimitProperties(
    @DefaultValue("true")
    boolean enabled,
    @Min(value=1,message = "Max request must be at least 1")
    @DefaultValue("10")
    int maxRequests,
    @Min(value=1,message = "Window duration must be at least 1 second")
    @DefaultValue("60")
    long windowSecs
){}
