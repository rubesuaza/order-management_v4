package com.example.order_management.domain.exception;

/**
 * Exception thrown when an OrderItem has invalid quantity or price.
 */
public class InvalidItemException extends DomainException {
    
    public InvalidItemException(String message) {
        super(message);
    }
}
