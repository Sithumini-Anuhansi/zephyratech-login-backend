package com.zephyratech.loginbackend.security;

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
		this.signingKey = Keys.hmacShaKeyFor(HexFormat.of().parseHex(secret));
		this.expirationMillis = expirationMillis;
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
