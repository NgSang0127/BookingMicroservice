package org.sang.service.impl;

import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.sang.model.User;
import org.sang.payload.request.SignUpDTO;
import org.sang.repository.UserRepository;
import org.sang.service.AuthService;
import org.sang.service.KeycloakUserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

	private final UserRepository userRepository;
	private final KeycloakUserService keycloakUserService;

	@Override
	@Transactional
	public void signup(SignUpDTO req) throws Exception {
		// 1. Tạo user trong Keycloak → nhận keycloakId
		String keycloakId = keycloakUserService.createUser(req);

		// 2. Lưu profile vào DB — keycloakId là cầu nối
		User user = new User();
		user.setKeycloakId(keycloakId);
		user.setEmail(req.getEmail());
		user.setFullName(req.getFirstName() +" "+ req.getLastName());
		user.setPhone(req.getPhone());
		user.setRole(req.getRole());
		user.setUsername(req.getEmail());
		user.setCreatedAt(LocalDateTime.now());
		userRepository.save(user);

		// KHÔNG trả token — React redirect sang Keycloak login page sau khi signup
	}

	@Override
	public void forgotPassword(String email) throws Exception {
		keycloakUserService.sendResetPasswordEmailByEmail(email);
	}

	@Override
	public void changePassword(String keycloakId, String newPassword) throws Exception {
		// keycloakId lấy từ header X-User-Id (Gateway đã extract từ JWT)
		keycloakUserService.changePassword(keycloakId, newPassword);
	}
}