package org.sang.controller;

import lombok.RequiredArgsConstructor;
import org.sang.client.feign.ClinicFeignClient;
import org.sang.model.Category;
import org.sang.payload.dto.ClinicDTO;
import org.sang.service.CategoryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/categories/clinic-owner")
@RequiredArgsConstructor
public class ClinicCategoryController {

	private final CategoryService categoryService;
	private final ClinicFeignClient clinicService;

	@PostMapping
	public ResponseEntity<Category> createCategory(
			@RequestBody Category category) throws Exception {
		ClinicDTO clinic = clinicService.getClinicByOwner().getBody();

		Category savedCategory = categoryService.saveCategory(category, clinic);
		return new ResponseEntity<>(savedCategory, HttpStatus.CREATED);
	}
}
