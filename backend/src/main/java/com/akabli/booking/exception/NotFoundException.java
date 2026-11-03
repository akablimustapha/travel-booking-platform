package com.akabli.booking.exception;

/** Ressource introuvable : renvoyée en HTTP 404. */
public class NotFoundException extends RuntimeException {
    public NotFoundException(String message) {
        super(message);
    }
}
