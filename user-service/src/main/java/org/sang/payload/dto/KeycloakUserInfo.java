package org.sang.payload.dto;

import lombok.Data;

@Data
public class KeycloakUserInfo {
	private String sub;
	private String email;
	private String preferred_username;
}
