package org.sang.service;

import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.sang.payload.request.SignUpDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class KeycloakUserService {

	private final Keycloak keycloak;

	@Value("${keycloak.realm}")
	private String realm;

	private UsersResource getUsersResource() {
		return keycloak.realm(realm).users();
	}

	public String createUser(SignUpDTO dto) throws Exception {
		UserRepresentation user = new UserRepresentation();
		user.setEnabled(true);
		user.setEmail(dto.getEmail());
		user.setUsername(dto.getEmail());     // email as username
		user.setFirstName(dto.getFirstName());
		user.setLastName(dto.getLastName());
		user.setEmailVerified(false);

		CredentialRepresentation credential = new CredentialRepresentation();
		credential.setTemporary(false);
		credential.setType(CredentialRepresentation.PASSWORD);
		credential.setValue(dto.getPassword());
		user.setCredentials(Collections.singletonList(credential));

		try (Response response = getUsersResource().create(user)) {
			if (response.getStatus() == 201) {
				String location = response.getHeaderString("Location");
				String userId = location.substring(location.lastIndexOf('/') + 1);

				// Gán role mặc định (CUSTOMER, OWNER,...)
				assignRealmRole(userId, dto.getRole().toString());

				// Gửi email xác thực
				getUsersResource().get(userId).sendVerifyEmail();

				return userId; // keycloakId — lưu vào DB

			} else if (response.getStatus() == 409) {
				throw new Exception("Email đã được đăng ký");
			} else {
				throw new Exception("Tạo user thất bại: HTTP " + response.getStatus());
			}
		}
	}

	public void assignRealmRole(String userId, String roleName) {
		RoleRepresentation role = keycloak.realm(realm)
				.roles().get(roleName).toRepresentation();
		getUsersResource().get(userId)
				.roles().realmLevel()
				.add(Collections.singletonList(role));
	}

	public void sendResetPasswordEmailByEmail(String email) {
		List<UserRepresentation> users = getUsersResource()
				.searchByEmail(email, true);

		if (users == null || users.isEmpty()) return;

		getUsersResource().get(users.get(0).getId())
				.executeActionsEmail(List.of("UPDATE_PASSWORD"));
	}

	// ─── Đổi mật khẩu (user đã authenticated — chỉ cần keycloakId) ──────────
	public void changePassword(String keycloakId, String newPassword) {
		CredentialRepresentation credential = new CredentialRepresentation();
		credential.setType(CredentialRepresentation.PASSWORD);
		credential.setValue(newPassword);
		credential.setTemporary(false);
		getUsersResource().get(keycloakId).resetPassword(credential);
	}

	public void updateUserProfile(String keycloakId,
			String firstName, String lastName) {
		UserRepresentation user = getUsersResource()
				.get(keycloakId).toRepresentation();
		user.setFirstName(firstName);
		user.setLastName(lastName);
		getUsersResource().get(keycloakId).update(user);
	}
}