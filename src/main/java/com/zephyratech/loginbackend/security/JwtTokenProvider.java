package com.zephyratech.loginbackend.security;

import java.util.Base64;
import java.util.Date;
import java.util.HexFormat;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtTokenProvider {

	private final SecretKey signingKey;
	private final long expirationMillis;

	public JwtTokenProvider(
			@Value("${jwt.secret}") String secret,
			@Value("${jwt.expiration}") long expirationMillis) {
		this.signingKey = Keys.hmacShaKeyFor(decodeSecret(secret));
		this.expirationMillis = expirationMillis;
	}

	private static byte[] decodeSecret(String secret) {
		if (secret.length() % 2 == 0 && secret.matches("[0-9a-fA-F]+")) {
			return HexFormat.of().parseHex(secret);
		}

		try {
			return Base64.getDecoder().decode(secret);
		} catch (IllegalArgumentException standardBase64Error) {
			try {
				return Base64.getUrlDecoder().decode(secret);
			} catch (IllegalArgumentException urlBase64Error) {
				throw new IllegalArgumentException(
						"JWT secret must be an even-length hex string or a Base64-encoded key",
						urlBase64Error);
			}
		}
	}

	public String generateToken(String email) {
		Date issuedAt = new Date();
		Date expiresAt = new Date(issuedAt.getTime() + expirationMillis);

		return Jwts.builder()
				.subject(email)
				.issuedAt(issuedAt)
				.expiration(expiresAt)
				.signWith(signingKey)
				.compact();
	}

	public boolean validateToken(String token) {
		try {
			String subject = Jwts.parser()
					.verifyWith(signingKey)
					.build()
					.parseSignedClaims(token)
					.getPayload()
					.getSubject();
			return subject != null && !subject.isBlank();
		} catch (JwtException | IllegalArgumentException exception) {
			return false;
		}
	}

	public String extractEmail(String token) {
		return Jwts.parser()
				.verifyWith(signingKey)
				.build()
				.parseSignedClaims(token)
				.getPayload()
				.getSubject();
	}
}
