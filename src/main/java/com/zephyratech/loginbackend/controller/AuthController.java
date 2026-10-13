package com.zephyratech.loginbackend.controller;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.zephyratech.loginbackend.dto.AuthResponse;
import com.zephyratech.loginbackend.dto.LoginRequest;
import com.zephyratech.loginbackend.dto.RegisterRequest;
import com.zephyratech.loginbackend.dto.UserProfileResponse;
import com.zephyratech.loginbackend.service.AuthenticationService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api")
public class AuthController {

	private final AuthenticationService authenticationService;

	public AuthController(AuthenticationService authenticationService) {
		this.authenticationService = authenticationService;
	}

	@PostMapping("/auth/register")
	@ResponseStatus(HttpStatus.CREATED)
	public AuthResponse register(@Valid @RequestBody RegisterRequest request) {
		return authenticationService.register(request);
	}

	@PostMapping("/auth/login")
	public AuthResponse login(@Valid @RequestBody LoginRequest request) {
		return authenticationService.login(request);
	}

	@GetMapping("/auth/me")
	public UserProfileResponse me(Authentication authentication) {
		return authenticationService.getProfile(authentication.getName());
	}

	@GetMapping("/health")
	public HealthResponse health() {
		return new HealthResponse("UP");
	}

	public record HealthResponse(String status) {
	}
}
