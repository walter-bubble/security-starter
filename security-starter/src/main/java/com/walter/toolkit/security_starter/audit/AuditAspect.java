package com.walter.toolkit.security_starter.audit;

import com.walter.toolkit.security_starter.dto.AuditEvent;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Aspect
@Component
public class AuditAspect {
    private static final Logger log = LoggerFactory.getLogger(AuditAspect.class);
    private final AuditSink auditSink;

    public AuditAspect(ObjectProvider<AuditSink>customSinkProvider) {
        this.auditSink = customSinkProvider.getIfAvailable(()->event->log.info("AUDIT | Actor: {} | Action: {} | Status: {} | Method: {} | Time: {}",
                event.actor(),event.action(),event.status(),event.methodName(),event.timeStamp()));
    }
    @Around("@annotation(auditable)")
    public Object captureAuditTrail(ProceedingJoinPoint joinPoint,Auditable auditable)throws Throwable{
        String actor = resolveCurrentActor();
        String methodName = joinPoint.getSignature().toShortString();
        try{
            Object result = joinPoint.proceed();
            auditSink.record(new AuditEvent(actor, auditable.action(), methodName,"Success","Action completed successfully", Instant.now()));
            return result;
        }catch (Throwable exception){
            auditSink.record(new AuditEvent(actor,auditable.action(),methodName,"Failed",exception.getMessage(),Instant.now()));
            throw exception;
        }
    }
    private String resolveCurrentActor(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())){
            return "SYSTEM_ OR_ANONYMOUS";
        }
        return authentication.getName();
    }
}
