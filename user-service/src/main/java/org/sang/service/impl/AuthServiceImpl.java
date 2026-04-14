package org.sang.service.impl;

import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.sang.model.User;
import org.sang.payload.request.SignUpDTO;
import org.sang.payload.response.AuthResponse;
import org.sang.payload.response.TokenResponse;
import org.sang.repository.UserRepository;
import org.sang.service.AuthService;
import org.sang.service.KeycloakUserService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
	private final UserRepository userRepository;
	private final KeycloakUserService keycloakUserService;

	@Override
	public AuthResponse signup(SignUpDTO req) throws Exception {

		keycloakUserService.createUser(req);

		User createdUser = new User();
		createdUser.setEmail(req.getEmail());
		createdUser.setCreatedAt(LocalDateTime.now());
		createdUser.setPhone(req.getPhone());
		createdUser.setRole(req.getRole());
		createdUser.setFullName(req.getFullName());
		createdUser.setUsername(req.getUsername());
		userRepository.save(createdUser);


		TokenResponse tokenResponse= keycloakUserService.getAdminAccessToken(
				req.getUsername(),
				req.getPassword(),
				"password",
				null
		);

		AuthResponse response = new AuthResponse();
		response.setTitle("Welcome " + createdUser.getEmail());
		response.setMessage("Register success");
		response.setJwt(tokenResponse.getAccessToken());
		response.setRefresh_token(tokenResponse.getRefreshToken());
		return response;
	}

	@Override
	public AuthResponse getAccessTokenFromRefreshToken(String refreshToken) throws Exception {
		TokenResponse tokenResponse= keycloakUserService.getAdminAccessToken(
				null,
				null,
				"refresh_token",
				refreshToken
		);
		AuthResponse response = new AuthResponse();

		response.setMessage("Access token received");
		response.setJwt(tokenResponse.getAccessToken());
		response.setRefresh_token(tokenResponse.getRefreshToken());
		return response;
	}

	@Override
	public AuthResponse login(String username, String password) throws Exception {
		TokenResponse tokenResponse=keycloakUserService.getAdminAccessToken(
				username,
				password,
				"password",
				null
		);
		AuthResponse response = new AuthResponse();
		response.setTitle("Welcome Back " + username);
		response.setMessage("login success");
		response.setJwt(tokenResponse.getAccessToken());
		response.setRefresh_token(tokenResponse.getRefreshToken());
		return response;
	}

}
