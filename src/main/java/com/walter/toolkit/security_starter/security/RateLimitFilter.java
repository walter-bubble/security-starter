package com.walter.toolkit.security_starter.security;

import com.walter.toolkit.security_starter.exception.ApiException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RateLimitFilter extends OncePerRequestFilter{
    private final RateLimitProperties rateLimitProperties;
    private final HandlerExceptionResolver handlerExceptionResolver;

    private final Map<String ,RequestWindow> requestCounts = new ConcurrentHashMap<>();

    public RateLimitFilter(RateLimitProperties rateLimitProperties, HandlerExceptionResolver handlerExceptionResolver) {
        this.rateLimitProperties = rateLimitProperties;
        this.handlerExceptionResolver = handlerExceptionResolver;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        //skip if it is auth or if ratelimiter is off
        if(!rateLimitProperties.enabled() || !request.getServletPath().startsWith("/api/auth")){
            filterChain.doFilter(request,response);
            return;
        }
        //we check the senders ip
        String clientIp = resolveClientIp(request);
        long now = Instant.now().getEpochSecond();

        //check and update callers counter
        RequestWindow window = requestCounts.compute(clientIp,(ip,existing)->{
            if(existing == null || now-existing.windowStart()>= rateLimitProperties.windowSecs()){
                return new RequestWindow(now,1);
            }
            return new RequestWindow(existing.windowStart(),existing.count()+1);
        });

        //block request if the limit is exceeded
        if(window.count()>rateLimitProperties.maxRequests()){
            ApiException tooManyRequests = new ApiException(HttpStatus.TOO_MANY_REQUESTS,"ERR-RATE-LIMIT-EXCEEDED","Too many authentication attempts. Please try again later.");
            handlerExceptionResolver.resolveException(request,response,null,tooManyRequests);
            return;
        }
        filterChain.doFilter(request,response);
    }
    private String resolveClientIp(HttpServletRequest request){
        String forwarded = request.getHeader("X-Forwarded-For");
        if(forwarded != null && !forwarded.isBlank()){
            return forwarded.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
    private record RequestWindow(long windowStart,int count){}
}
