package org.sang.payload.dto;

import lombok.Data;

@Data
public class UserDTO {
	private Long id;
	private String fullName;
	private String username;
	private String phone;
	private String email;
	private String role;
}
