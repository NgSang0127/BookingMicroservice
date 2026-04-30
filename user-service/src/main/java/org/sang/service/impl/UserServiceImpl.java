package org.sang.service.impl;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sang.exception.UserException;
import org.sang.model.User;
import org.sang.payload.dto.KeycloakUserInfo;
import org.sang.repository.UserRepository;
import org.sang.service.KeycloakUserService;
import org.sang.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
	private final UserRepository userRepository;
	private final KeycloakUserService keycloakUserService;


	@Override
	public User getUserById(Long id) throws UserException {
		return userRepository.findById(id).orElseThrow(
				()-> new UserException ("User not found with id: "+id)
		);
	}

	@Override
	public Page<User> getAllUsers(int page,int size) {
		Pageable pageable= PageRequest.of(page,size);
		return userRepository.findAll(pageable);
	}



	@Override
	public User updateUser(Long id, User user) throws UserException {
		User existingUser = userRepository.findById(id)
				.orElseThrow(() -> new UserException("User does not exist with id: " + id));

		// 1. Cập nhật DB
		if (user.getFullName() != null) existingUser.setFullName(user.getFullName());
		if (user.getUsername() != null) existingUser.setUsername(user.getUsername());
		if (user.getPhone() != null)    existingUser.setPhone(user.getPhone());

		User saved = userRepository.save(existingUser);

		// 2. Sync lên Keycloak — firstName/lastName trong token sẽ được cập nhật
		if (existingUser.getKeycloakId() != null) {
			try {
				keycloakUserService.updateUserProfile(
						existingUser.getKeycloakId(),
						user.getFullName(),   // firstName trong Keycloak
						null                  // lastName nếu bạn có field riêng
				);
			} catch (Exception e) {
				// Log lỗi nhưng không fail request — DB đã save thành công
				log.warn("Sync Keycloak thất bại cho user {}: {}", id, e.getMessage());
			}
		}

		return saved;
	}


	@Override
	public User getByKeycloakId(String keycloakId) throws UserException {
		return userRepository.findByKeycloakId(keycloakId)
				.orElseThrow(() -> new UserException(
						"User not found with keycloakId: " + keycloakId));
	}

}
