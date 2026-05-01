package org.sang.payload.request;

import lombok.Data;

@Data
public class PasswordChangeRequest {
	private String oldPassword;
	private String newPassword;
}
