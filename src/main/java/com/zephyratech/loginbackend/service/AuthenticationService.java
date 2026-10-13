package com.zephyratech.loginbackend.service;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.zephyratech.loginbackend.dto.AuthResponse;
import com.zephyratech.loginbackend.dto.LoginRequest;
import com.zephyratech.loginbackend.dto.RegisterRequest;
import com.zephyratech.loginbackend.dto.UserProfileResponse;
import com.zephyratech.loginbackend.model.User;
import com.zephyratech.loginbackend.repository.UserRepository;
import com.zephyratech.loginbackend.security.JwtTokenProvider;

@Service
public class AuthenticationService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final AuthenticationManager authenticationManager;
	private final JwtTokenProvider jwtTokenProvider;

	public AuthenticationService(
			UserRepository userRepository,
			PasswordEncoder passwordEncoder,
			AuthenticationManager authenticationManager,
			JwtTokenProvider jwtTokenProvider) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
		this.authenticationManager = authenticationManager;
		this.jwtTokenProvider = jwtTokenProvider;
	}

	public AuthResponse register(RegisterRequest request) {
		if (userRepository.findByEmail(request.email()).isPresent()) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, "Email is already registered");
		}

		var user = new User(request.email(), passwordEncoder.encode(request.password()));
		userRepository.save(user);
		return new AuthResponse(jwtTokenProvider.generateToken(user.getEmail()), user.getEmail());
	}

	public AuthResponse login(LoginRequest request) {
		authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(request.email(), request.password()));
		return new AuthResponse(jwtTokenProvider.generateToken(request.email()), request.email());
	}

	public UserProfileResponse getProfile(String email) {
		var user = userRepository.findByEmail(email)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
		return new UserProfileResponse(user.getId(), user.getEmail());
	}
}
