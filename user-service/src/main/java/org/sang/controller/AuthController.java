package org.sang.controller;

import lombok.RequiredArgsConstructor;
import org.sang.payload.request.LoginDTO;
import org.sang.payload.request.SignUpDTO;
import org.sang.payload.response.ApiResponse;
import org.sang.payload.response.ApiResponseBody;
import org.sang.payload.response.AuthResponse;
import org.sang.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

	private final AuthService authService;

	@GetMapping
	public ResponseEntity<ApiResponse> HomeControllerHandler() {

		return ResponseEntity.status(
						HttpStatus.OK)
				.body(new ApiResponse(
						"Welcome to user-service"

				));
	}

	@PostMapping("/signup")
	public ResponseEntity<ApiResponseBody<AuthResponse>> signupHandler(
			@RequestBody SignUpDTO req) throws Exception {

		System.out.println("signup dto " + req);
		AuthResponse response = authService.signup(req);

		return ResponseEntity.ok(new ApiResponseBody<>(
				true,
				"User created successfully", response)
		);
	}

	@PostMapping("/login")
	public ResponseEntity<ApiResponseBody<AuthResponse>> loginHandler(
			@RequestBody LoginDTO req) throws Exception {

		AuthResponse response = authService.login(req.getEmail(), req.getPassword());

		return ResponseEntity.ok(new ApiResponseBody<>(
				true,
				"User logged in successfully",
				response)
		);
	}

	@GetMapping("/access-token/refresh-token/{refreshToken}")
	public ResponseEntity<ApiResponseBody<AuthResponse>> getAccessTokenHandler(
			@PathVariable String refreshToken) throws Exception {

		AuthResponse response = authService.getAccessTokenFromRefreshToken(refreshToken);

		return ResponseEntity.ok(new ApiResponseBody<>(
				true,
				"refresh token received successfully",
				response
		));
	}

}
