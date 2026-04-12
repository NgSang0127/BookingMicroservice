package org.sang.modal;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import org.sang.domain.UserRole;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotBlank(message = "FullName is mandatory")
	private String fullName;

	@Column(nullable = false, unique = true)
	@NotBlank(message = "Username is mandatory")
	private String username;

	@Column(nullable = false, unique = true)
	@NotBlank(message = "Email is mandatory")
	@Email(message = "Email should be valid")
	private String email;

	private String phone;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private UserRole role = UserRole.CUSTOMER;

	@Column(nullable = false, updatable = false)
	@CreationTimestamp
	private LocalDateTime createdAt;

	@Column(nullable = false)
	@UpdateTimestamp
	private LocalDateTime updatedAt;


}
