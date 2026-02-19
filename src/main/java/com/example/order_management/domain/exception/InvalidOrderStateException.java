package com.example.order_management.domain.exception;

/**
 * Exception thrown when an illegal state transition is attempted on an Order.
 */
public class InvalidOrderStateException extends DomainException {
    
    public InvalidOrderStateException(String message) {
        super(message);
    }
}
