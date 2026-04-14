package org.sang.service.impl;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
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
public class UserServiceImpl implements UserService {
	private final UserRepository userRepository;
	private final KeycloakUserService keycloakUserService;

	@Override
	public User getByUserByEmail(String email) throws UserException {
		User user=userRepository.findByEmail(email);
		if(user == null){
			throw new UserException("User not found with email: "+email);
		}
		return user;
	}

	@Override
	public User getUserFromJwtToken(String jwt) throws Exception {
		KeycloakUserInfo userinfo = keycloakUserService.fetchUserProfileByJwt(jwt);
		return userRepository.findByEmail(userinfo.getEmail());
	}

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
	public User createUser(User user) {
		return userRepository.save(user);
	}

	@Override
	public User updateUser(Long id, User user) throws UserException {
		Optional<User> otp=userRepository.findById(id);
		if(otp.isEmpty()){
			throw new UserException("User does not exist with id: "+id);
		}
		User existingUser=otp.get();
		existingUser.setFullName(user.getFullName());
		existingUser.setUsername(user.getUsername());
		existingUser.setEmail(user.getEmail());
		existingUser.setPhone(user.getPhone());
		existingUser.setRole(user.getRole());
		return existingUser;
	}

	@Override
	public String deleteUser(Long id) throws UserException {
		Optional<User> otp=userRepository.findById(id);
		if(otp.isEmpty()){
			throw new UserException("User does not exist with id: "+id);
		}
		userRepository.deleteById(otp.get().getId());
		return "User deleted";
	}
}
