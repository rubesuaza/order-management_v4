package com.example.order_management.domain.exception;

/**
 * Exception thrown when monetary operations involve different currencies.
 */
public class CurrencyMismatchException extends DomainException {
    
    public CurrencyMismatchException(String message) {
        super(message);
    }
}
