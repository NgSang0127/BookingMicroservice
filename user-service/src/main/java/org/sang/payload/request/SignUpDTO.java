package org.sang.payload.request;

import lombok.Data;
import org.sang.constant.UserRole;

@Data
public class SignUpDTO {
	private String email;
	private String password;
	private String phone;
	private String firstName;
	private String lastName;
	private String username;
	private UserRole role;
}
