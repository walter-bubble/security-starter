package com.walter.toolkit.security_starter.security;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "toolkit.security.jwt")
public record JwtProperties(
    @NotBlank(message = "Jwt secret must be configured")
    String secret,
    @NotBlank(message = "Jwt issuer must be configured")
    @DefaultValue("backend-toolkit")
    String issuer,
    @Min(value=60,message="jwt token expiration must at least be 60 seconds")
    @DefaultValue("900")
    Long accessTokenExpiration,
    @Min(value=0,message="clock skew cannot be negative")
    @DefaultValue("30")
    Long clockSkewSeconds
    ){
}
