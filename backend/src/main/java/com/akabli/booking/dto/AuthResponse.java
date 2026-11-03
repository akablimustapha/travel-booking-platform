package com.akabli.booking.dto;

import com.akabli.booking.entity.Role;

public record AuthResponse(String token, String fullName, String email, Role role) {
}
