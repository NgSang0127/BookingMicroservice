package org.sang.mapper;

import org.sang.model.User;
import org.sang.payload.dto.UserDTO;
import org.springframework.stereotype.Service;

@Service
public class UserMapper {
	public UserDTO mapToDTO(User user) {
		UserDTO userDTO = new UserDTO();
		userDTO.setId(user.getId());
		userDTO.setEmail(user.getEmail());
		userDTO.setFullName(user.getFullName());
		userDTO.setRole(user.getRole().toString());

		return userDTO;
	}
}
