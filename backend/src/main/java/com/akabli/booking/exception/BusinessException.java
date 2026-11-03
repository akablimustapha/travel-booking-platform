package com.akabli.booking.exception;

/** Erreur métier (règle non respectée) : renvoyée en HTTP 409. */
public class BusinessException extends RuntimeException {
    public BusinessException(String message) {
        super(message);
    }
}
