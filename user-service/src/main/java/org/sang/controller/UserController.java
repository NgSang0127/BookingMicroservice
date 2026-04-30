package org.sang.controller;

import lombok.RequiredArgsConstructor;
import org.sang.exception.UserException;
import org.sang.mapper.UserMapper;
import org.sang.model.User;
import org.sang.payload.dto.UserDTO;
import org.sang.payload.request.PasswordChangeRequest;
import org.sang.service.AuthService;
import org.sang.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

	private final UserService userService;
	private final UserMapper userMapper;
	private final AuthService authService;

	@GetMapping("/profile")
	public ResponseEntity<UserDTO> getMyProfile(
			@AuthenticationPrincipal Jwt jwt) throws UserException {

		String keycloakId = jwt.getSubject();
		User user = userService.getByKeycloakId(keycloakId);
		return ResponseEntity.ok(userMapper.mapToDTO(user));
	}

	@GetMapping("/{userId}")
	public ResponseEntity<UserDTO> getUserById(
			@PathVariable Long userId) throws UserException {
		User user = userService.getUserById(userId);
		return ResponseEntity.ok(userMapper.mapToDTO(user));
	}
	@GetMapping
	public ResponseEntity<Page<UserDTO>> getAllUsers(
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size) {
		Page<User> userPage = userService.getAllUsers(page, size);
		List<UserDTO> dtos = userPage.getContent().stream()
				.map(userMapper::mapToDTO)
				.collect(Collectors.toList());
		return ResponseEntity.ok(
				new PageImpl<>(dtos, userPage.getPageable(), userPage.getTotalElements())
		);
	}

	@PutMapping("/profile")
	public ResponseEntity<UserDTO> updateProfile(
			@AuthenticationPrincipal Jwt jwt,
			@RequestBody User userUpdate) throws UserException {

		String keycloakId = jwt.getSubject();
		User current = userService.getByKeycloakId(keycloakId);
		User updated = userService.updateUser(current.getId(), userUpdate);
		return ResponseEntity.ok(userMapper.mapToDTO(updated));
	}

	@PutMapping("/profile/change-password")
	public ResponseEntity<String> changePassword(
			@AuthenticationPrincipal Jwt jwt,
			@RequestBody PasswordChangeRequest request) throws Exception {

		String keycloakId = jwt.getSubject();
		authService.changePassword(keycloakId, request.getNewPassword());
		return ResponseEntity.ok("Đổi mật khẩu thành công!");
	}
}