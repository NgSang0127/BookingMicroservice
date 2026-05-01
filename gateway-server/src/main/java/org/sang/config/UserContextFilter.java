//package org.sang.config;
//
//import org.springframework.cloud.gateway.filter.GlobalFilter;
//import org.springframework.cloud.gateway.filter.GatewayFilterChain;
//import org.springframework.core.Ordered;
//import org.springframework.stereotype.Component;
//import org.springframework.web.server.ServerWebExchange;
//import reactor.core.publisher.Mono;
//
//import java.util.List;
//import java.util.Map;
//
//@Component
//public class UserContextFilter implements GlobalFilter, Ordered {
//
//	@Override
//	public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
//		return exchange.getPrincipal()
//				.cast(org.springframework.security.core.Authentication.class)
//				.flatMap(authentication -> {
//					if (!(authentication.getPrincipal()
//							instanceof org.springframework.security.oauth2.jwt.Jwt jwt)) {
//						return chain.filter(exchange);
//					}
//
//					String keycloakId = jwt.getSubject();
//					String email      = jwt.getClaimAsString("email");
//					String firstName  = jwt.getClaimAsString("given_name");
//					String roles      = extractRoles(jwt);
//
//					var mutatedRequest = exchange.getRequest().mutate()
//							.header("X-User-Id",        keycloakId)
//							.header("X-User-Email",     email != null ? email : "")
//							.header("X-User-FirstName", firstName != null ? firstName : "")
//							.header("X-User-Roles",     roles)
//							.build();
//
//					return chain.filter(exchange.mutate().request(mutatedRequest).build());
//				})
//				.switchIfEmpty(chain.filter(exchange)); // public route → không có principal → đi tiếp
//	}
//
//	@Override
//	public int getOrder() {
//		// Chạy SAU khi SecurityFilter đã validate JWT xong
//		return Ordered.LOWEST_PRECEDENCE - 1;
//	}
//
//	@SuppressWarnings("unchecked")
//	private String extractRoles(org.springframework.security.oauth2.jwt.Jwt jwt) {
//		Map<String, Object> realmAccess = jwt.getClaimAsMap("realm_access");
//		if (realmAccess == null || !realmAccess.containsKey("roles")) return "";
//		List<String> roles = (List<String>) realmAccess.get("roles");
//		return String.join(",", roles);
//	}
//}