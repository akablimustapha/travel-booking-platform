package com.akabli.booking.dto;

import java.time.Instant;
import java.util.Map;

public record ApiError(int status, String message, Map<String, String> errors, Instant timestamp) {

    public static ApiError of(int status, String message, Map<String, String> errors) {
        return new ApiError(status, message, errors, Instant.now());
    }
}
