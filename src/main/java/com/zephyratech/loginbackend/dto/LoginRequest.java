package com.zephyratech.loginbackend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import com.zephyratech.loginbackend.util.EmailNormalizer;

public record LoginRequest(
		@Email @NotBlank String email,
		@NotBlank String password) {

	public LoginRequest {
		email = EmailNormalizer.normalize(email);
	}
}
