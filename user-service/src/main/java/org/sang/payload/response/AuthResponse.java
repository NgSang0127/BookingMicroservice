package org.sang.payload.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.sang.constant.UserRole;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponse {
	private String jwt;
	private String refresh_token;
	private String message;
	private String title;
	private UserRole role;
}
