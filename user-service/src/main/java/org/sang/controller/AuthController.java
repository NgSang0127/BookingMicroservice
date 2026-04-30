package org.sang.controller;

import lombok.RequiredArgsConstructor;
import org.sang.payload.request.SignUpDTO;
import org.sang.payload.response.ApiResponseBody;
import org.sang.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

	private final AuthService authService;

	@PostMapping("/signup")
	public ResponseEntity<ApiResponseBody<String>> signup(
			@RequestBody SignUpDTO req) throws Exception {
		authService.signup(req);
		return ResponseEntity.status(HttpStatus.CREATED)
				.body(new ApiResponseBody<>(true,
						"Đăng ký thành công! Kiểm tra email để xác thực.", null));
	}

	@PostMapping("/forgot-password")
	public ResponseEntity<ApiResponseBody<String>> forgotPassword(
			@RequestBody Map<String, String> body) {
		try {
			authService.forgotPassword(body.get("email"));
		} catch (Exception ignored) {}
		return ResponseEntity.ok(new ApiResponseBody<>(true,
				"Nếu email tồn tại, link đặt lại mật khẩu đã được gửi.", null));
	}

}