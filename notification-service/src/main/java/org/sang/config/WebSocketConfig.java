package org.sang.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;


@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

	@Override
	public void registerStompEndpoints(StompEndpointRegistry registry) {
		// CHỈ ĐỂ /ws - Gateway sẽ chịu trách nhiệm định tuyến /api/notifications/ws vào đây
		registry.addEndpoint("/ws")
				.setAllowedOrigins("http://localhost:5173", "http://localhost:3173", "https://clinic-booking-three.vercel.app")
				.withSockJS();
	}

	@Override
	public void configureMessageBroker(MessageBrokerRegistry registry) {
		registry.setApplicationDestinationPrefixes("/app");
		registry.enableSimpleBroker("/notification", "/user", "/chat");
		registry.setUserDestinationPrefix("/user");
	}
}
