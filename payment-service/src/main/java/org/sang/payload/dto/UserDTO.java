package org.sang.payload.dto;

import java.io.Serial;
import java.io.Serializable;
import lombok.Data;

@Data
public class UserDTO implements Serializable {
	@Serial
	private static final long serialVersionUID = 1L;

	private Long id;
	private String fullName;
	private String email;

}