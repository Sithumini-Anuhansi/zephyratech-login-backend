package com.zephyratech.loginbackend.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class AuthExceptionHandler {

	@ExceptionHandler(BadCredentialsException.class)
	public ResponseEntity<Map<String, String>> handleBadCredentials(BadCredentialsException exception) {
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
				.body(Map.of("message", "Invalid email or password"));
	}
}
