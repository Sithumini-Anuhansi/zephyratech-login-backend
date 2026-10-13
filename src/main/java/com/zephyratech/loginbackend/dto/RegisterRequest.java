package com.zephyratech.loginbackend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import com.zephyratech.loginbackend.util.EmailNormalizer;

public record RegisterRequest(
		@Email @NotBlank String email,
		@NotBlank @Size(min = 8) String password) {

	public RegisterRequest {
		email = EmailNormalizer.normalize(email);
	}
}
