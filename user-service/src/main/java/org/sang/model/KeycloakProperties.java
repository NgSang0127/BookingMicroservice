package org.sang.model;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "keycloak")
@Data
@Component
public class KeycloakProperties {
	private String baseUrl;
	private String clientId;
	private String clientSecret;
	private String scope;
	private String grantType;
	private String internalClientId;

	private Admin admin;

	@Data
	public static class Admin {
		private String username;
		private String password;
	}
}
