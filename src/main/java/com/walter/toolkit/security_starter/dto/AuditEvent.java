package com.walter.toolkit.security_starter.dto;

import java.time.Instant;

public record AuditEvent(
        String actor,
        String action,
        String methodName,
        String status,
        String details,
        Instant timeStamp) {
}
