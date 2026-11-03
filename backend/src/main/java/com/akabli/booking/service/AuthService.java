package com.akabli.booking.service;

import com.akabli.booking.dto.AuthResponse;
import com.akabli.booking.dto.LoginRequest;
import com.akabli.booking.dto.RegisterRequest;
import com.akabli.booking.entity.AppUser;
import com.akabli.booking.entity.Role;
import com.akabli.booking.exception.BusinessException;
import com.akabli.booking.repository.UserRepository;
import com.akabli.booking.security.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       AuthenticationManager authenticationManager,
                       JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        String email = request.email().trim().toLowerCase();
        if (userRepository.existsByEmail(email)) {
            throw new BusinessException("Cet email est déjà utilisé");
        }
        AppUser user = userRepository.save(new AppUser(request.fullName().trim(), email,
                passwordEncoder.encode(request.password()), Role.CLIENT));
        return toResponse(user);
    }

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        String email = request.email().trim().toLowerCase();
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, request.password()));
        AppUser user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BadCredentialsException("Utilisateur introuvable"));
        return toResponse(user);
    }

    private AuthResponse toResponse(AppUser user) {
        return new AuthResponse(jwtService.generateToken(user), user.getFullName(), user.getEmail(), user.getRole());
    }
}
