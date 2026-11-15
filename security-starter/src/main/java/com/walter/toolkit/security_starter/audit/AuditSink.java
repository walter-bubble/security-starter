package com.walter.toolkit.security_starter.audit;

import com.walter.toolkit.security_starter.dto.AuditEvent;

@FunctionalInterface
public interface AuditSink {
    void record(AuditEvent auditEvent);
}
