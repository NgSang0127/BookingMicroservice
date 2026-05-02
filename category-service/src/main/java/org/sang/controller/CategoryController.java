package org.sang.controller;

import lombok.RequiredArgsConstructor;
import org.sang.client.feign.ClinicFeignClient;
import org.sang.client.feign.UserFeignClient;
import org.sang.model.Category;
import org.sang.payload.dto.ClinicDTO;
import org.sang.payload.dto.UserDTO;
import org.sang.service.CategoryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

	private final CategoryService categoryService;
	private final UserFeignClient userService;
	private final ClinicFeignClient clinicService;

	// Get all Categories
	@GetMapping
	public ResponseEntity<List<Category>> getAllCategories() {
		List<Category> categories = categoryService.getAllCategories();
		return new ResponseEntity<>(categories, HttpStatus.OK);
	}

	// Get all Categories by Salon ID
	@GetMapping("/clinic/{id}")
	public ResponseEntity<Set<Category>> getCategoriesByClinic(
			@PathVariable Long id) throws Exception {
		UserDTO user=userService.getUserProfile();
		ClinicDTO clinic=clinicService.getClinicById(id).getBody();

		Set<Category> categories = categoryService
				.getAllCategoriesByClinic(clinic.getId());
		return new ResponseEntity<>(categories, HttpStatus.OK);
	}

	@GetMapping("/{id}")
	public ResponseEntity<Category> getCategoryById(@PathVariable Long id) {
		try {
			Category category = categoryService.getCategoryById(id);
			return new ResponseEntity<>(category, HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}

	@PatchMapping("/{id}")
	public ResponseEntity<Category> updateCategory(
			@PathVariable Long id,
			@RequestBody Category category
	) throws Exception {
		Category updatedCategory = categoryService.updateCategory(id,category);
		return new ResponseEntity<>(updatedCategory, HttpStatus.OK);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
		try {
			categoryService.deleteCategory(id);
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		} catch (Exception e) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}
}
