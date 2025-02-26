package com.assignment_two_starter.controller;

import com.assignment_two_starter.config.JwtUtil;
import com.assignment_two_starter.dto.AuthenticationRequest;
import com.assignment_two_starter.dto.AuthenticationResponse;
import com.assignment_two_starter.model.Customer;
import com.assignment_two_starter.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {

    private final AuthenticationManager authenticationManager;
    private final AuthService authService;
    private final JwtUtil jwtUtil;

    public AuthenticationController(AuthenticationManager authenticationManager, AuthService authService, JwtUtil jwtUtil) {
        this.authenticationManager = authenticationManager;
        this.authService = authService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Customer customer) {
        authService.registerUser(customer);
        return ResponseEntity.ok("User registered successfully!");
    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticate(@RequestBody AuthenticationRequest authRequest) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authRequest.getEmail(), authRequest.getPassword())
        );

        final UserDetails userDetails = authService.loadUserByUsername(authRequest.getEmail());
        final String jwt = jwtUtil.generateToken(userDetails);

        final String refreshToken = jwtUtil.generateRefreshToken(userDetails);
        return ResponseEntity.ok(new AuthenticationResponse(jwt, refreshToken));
    }
}
