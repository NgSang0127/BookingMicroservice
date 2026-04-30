package org.sang.config;

import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KeycloakConfig {

	@Value("${keycloak.server-url}")
	private String serverUrl;

	@Value("${keycloak.admin-client-id}")
	private String adminClientId;

	@Value("${keycloak.admin-client-secret}")
	private String adminClientSecret;

	@Bean
	public Keycloak keycloak() {

		return KeycloakBuilder.builder()
				.serverUrl(serverUrl)
				.realm("clinic-booking")
				.clientId(adminClientId)
				.clientSecret(adminClientSecret)
				.grantType("client_credentials")
				.build();
	}
}