package org.sang.payload.request;

import lombok.Data;
import org.sang.domain.UserRole;

@Data
public class SignUpDTO {
	private String email;
	private String password;
	private String phone;
	private String fullName;
	private String username;
	private UserRole role;
}
