package com.example.order_management.domain.exception;

/**
 * Thrown when order input data is invalid (e.g. empty items).
 */
public class InvalidOrderDataException extends DomainException {

    public InvalidOrderDataException(String message) {
        super(message);
    }
}
