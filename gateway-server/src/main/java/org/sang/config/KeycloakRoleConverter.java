package org.sang.config;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class KeycloakRoleConverter implements Converter<Jwt, Collection<GrantedAuthority>> {
	
	public Collection<GrantedAuthority> converts(Jwt source) {
		Map<String, Object> realmAccess = (Map<String, Object>)
				source.getClaims().get("realm_access");
		if (realmAccess == null || realmAccess.isEmpty()) {
			return new ArrayList<>();
		}
		Collection<GrantedAuthority> returnValue =
				((List<String>) realmAccess.get("roles"))
						.stream().map(roleName -> "ROLE_" + roleName)
						.map(SimpleGrantedAuthority::new)
						.collect(Collectors.toList());

		return returnValue;
	}

	@Override
	public Collection<GrantedAuthority> convert(Jwt jwt) {
		Collection<GrantedAuthority> authorities = new ArrayList<>();

		// Extract realm roles
		Map<String, Object> realmAccess = jwt.getClaimAsMap("realm_access");
		if (realmAccess != null && realmAccess.containsKey("roles")) {
			List<String> realmRoles = (List<String>) realmAccess.get("roles");
			realmRoles.forEach(role -> authorities.add(new SimpleGrantedAuthority("ROLE_" + role.toUpperCase())));
		}

		// Extract client roles
		Map<String, Object> resourceAccess = jwt.getClaimAsMap("resource_access");
		if (resourceAccess != null) {
			resourceAccess.forEach((client, clientDetails) -> {
				Map<String, Object> clientRoles = (Map<String, Object>) clientDetails;
				if (clientRoles.containsKey("roles")) {
					List<String> roles = (List<String>) clientRoles.get("roles");
					roles.forEach(role -> authorities.add(new SimpleGrantedAuthority("ROLE_" + role.toUpperCase())));
				}
			});
		}

		return authorities;
	}

}
