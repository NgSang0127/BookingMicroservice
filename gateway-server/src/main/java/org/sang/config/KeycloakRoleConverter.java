package org.sang.config;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.*;


public class KeycloakRoleConverter implements Converter<Jwt, Collection<GrantedAuthority>> {

	private static final List<String> IGNORED_ROLES = List.of(
			"offline_access", "uma_authorization"
	);

	@Override
	@SuppressWarnings("unchecked")
	public Collection<GrantedAuthority> convert(Jwt jwt) {
		Collection<GrantedAuthority> authorities = new ArrayList<>();

		Map<String, Object> realmAccess = jwt.getClaimAsMap("realm_access");
		if (realmAccess == null || !realmAccess.containsKey("roles")) {
			return authorities;
		}

		List<String> roles = (List<String>) realmAccess.get("roles");
		roles.stream()
				.filter(role -> !IGNORED_ROLES.contains(role))
				.filter(role -> !role.startsWith("default-roles-"))
				.forEach(role -> {
					String authority = role.toUpperCase().startsWith("ROLE_")
							? role.toUpperCase()
							: "ROLE_" + role.toUpperCase();
					authorities.add(new SimpleGrantedAuthority(authority));
				});

		return authorities;
	}
}