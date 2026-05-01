package org.sang.client;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Component
public class FeignClientInterceptor implements RequestInterceptor {

	@Override
	public void apply(RequestTemplate template) {

		ServletRequestAttributes attributes =
				(ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

		if (attributes == null) return;

		String authHeader = attributes.getRequest()
				.getHeader("Authorization");

		if (authHeader != null && authHeader.startsWith("Bearer ")) {
			template.header("Authorization", authHeader);
		}
	}
}