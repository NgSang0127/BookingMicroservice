package org.sang.controller;

import lombok.RequiredArgsConstructor;
import org.sang.exception.UserException;
import org.sang.mapper.UserMapper;
import org.sang.model.User;
import org.sang.payload.dto.UserDTO;
import org.sang.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

	private final UserService userService;
	private final UserMapper userMapper;
	

	@GetMapping("/profile")
	public ResponseEntity<UserDTO> getUserFromJwtToken(
			@RequestHeader("Authorization") String jwt) throws Exception {

		User user = userService.getUserFromJwtToken(jwt);
		UserDTO userDTO = userMapper.mapToDTO(user);

		return new ResponseEntity<>(userDTO, HttpStatus.OK);
	}

	@GetMapping("/{userId}")
	public ResponseEntity<UserDTO> getUserById(
			@PathVariable Long userId
	) throws UserException {
		User user = userService.getUserById(userId);
		if (user == null) {
			throw new UserException("User not found");
		}
		UserDTO userDTO = userMapper.mapToDTO(user);

		return new ResponseEntity<>(userDTO, HttpStatus.OK);
	}

	@GetMapping("/users")
	public ResponseEntity<Page<User>> getUsers(
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size
	) throws UserException {
		Page<User> users = userService.getAllUsers(page,size);

		return new ResponseEntity<>(users, HttpStatus.OK);
	}
}
