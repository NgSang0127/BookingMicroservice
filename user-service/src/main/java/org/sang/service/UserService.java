package org.sang.service;

import org.sang.exception.UserException;
import org.sang.model.User;
import org.springframework.data.domain.Page;

public interface UserService {

	User updateUser(Long id,User user) throws UserException;


	User getUserById(Long id) throws UserException;

	Page<User> getAllUsers(int page,int size);

	User getByKeycloakId(String keycloakId) throws UserException;

}
