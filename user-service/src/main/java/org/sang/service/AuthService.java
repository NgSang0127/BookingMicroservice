package org.sang.service;

import org.sang.payload.request.SignUpDTO;
import org.sang.payload.response.AuthResponse;

public interface AuthService {
	void signup(SignUpDTO req) throws Exception;
	void forgotPassword(String email) throws Exception;
	void changePassword(String keycloakId, String newPassword) throws Exception;
}
