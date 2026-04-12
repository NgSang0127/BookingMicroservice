package org.sang.service;

public interface AuthService {
	AuthResponse login(String username, String password) throws Exception;
	AuthResponse signup(SignupDto req) throws Exception;
	AuthResponse getAccessTokenFromRefreshToken(String refreshToken) throws Exception;
}
