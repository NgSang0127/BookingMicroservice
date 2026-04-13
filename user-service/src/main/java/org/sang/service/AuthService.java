package org.sang.service;

import org.sang.payload.request.SignUpDTO;
import org.sang.payload.response.AuthResponse;

public interface AuthService {
	AuthResponse login(String username, String password) throws Exception;
	AuthResponse signup(SignUpDTO req) throws Exception;
	AuthResponse getAccessTokenFromRefreshToken(String refreshToken) throws Exception;
}
