package com.example.management.domain.exception;

/**
 * Excepción de dominio cuando un pedido o sus partes violan invariantes de negocio.
 */
public class InvalidOrderException extends RuntimeException {

    public InvalidOrderException(String message) {
        super(message);
    }

    public InvalidOrderException(String message, Throwable cause) {
        super(message, cause);
    }
}
