package com.anmol.bookingsystem.service;

import com.anmol.bookingsystem.dto.LoginRequest;
import com.anmol.bookingsystem.dto.LoginResponse;
import com.anmol.bookingsystem.entity.User;
import com.anmol.bookingsystem.repository.UserRepository;
import com.anmol.bookingsystem.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    public LoginResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        String token = jwtUtil.generateToken(user);

        return new LoginResponse(token, user.getUsername(), user.getRole().name());
    }
}