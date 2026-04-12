package org.sang.service;

import java.util.List;
import org.sang.exception.UserException;
import org.sang.modal.User;
import org.springframework.data.domain.Page;

public interface UserService {
	User createUser(User user);

	User updateUser(Long id,User user) throws UserException;

	String deleteUser(Long id) throws UserException;

	User getByUserByEmail(String email) throws UserException;

	User getUserFromJwtToken(String jwt) throws Exception;

	User getUserById(Long id) throws UserException;

	Page<User> getAllUsers(int page,int size);

}
