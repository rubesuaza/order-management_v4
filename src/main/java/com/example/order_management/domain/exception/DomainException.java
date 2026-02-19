package com.example.order_management.domain.exception;

/**
 * Base exception for all domain-related errors.
 */
public abstract class DomainException extends RuntimeException {
    
    public DomainException(String message) {
        super(message);
    }
    
    public DomainException(String message, Throwable cause) {
        super(message, cause);
    }
}
