package org.sang.service;

import java.util.List;
import java.util.Set;
import org.sang.model.Category;
import org.sang.payload.dto.ClinicDTO;

public interface CategoryService {
	Category saveCategory(Category category, ClinicDTO clinic);

	List<Category> getAllCategories();

	Set<Category> getAllCategoriesByClinic(Long id);

	Category getCategoryById(Long id) throws Exception;

	Category updateCategory(Long id,Category category) throws Exception;

	void deleteCategory(Long id) throws Exception;
}
