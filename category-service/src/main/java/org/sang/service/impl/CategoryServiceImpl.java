package org.sang.service.impl;

import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.sang.model.Category;
import org.sang.payload.dto.ClinicDTO;
import org.sang.repository.CategoryRepository;
import org.sang.service.CategoryService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
	private final CategoryRepository categoryRepository;

	@Override
	public Category saveCategory(Category category, ClinicDTO clinic) {
		Category newCategory=new Category();
		newCategory.setName(category.getName());
		newCategory.setImage(category.getImage());
		newCategory.setClinicId(clinic.getId());

		return categoryRepository.save(newCategory);
	}

	@Override
	public List<Category> getAllCategories() {
		return categoryRepository.findAll();
	}

	@Override
	public Set<Category> getAllCategoriesByClinic(Long id) {
		return categoryRepository.findByClinicId(id);
	}

	@Override
	public Category getCategoryById(Long id) throws Exception {
		return categoryRepository.findById(id)
				.orElseThrow(()->new Exception("Category does not found with: "+id));
	}

	@Override
	public Category updateCategory(Long id, Category category) throws Exception {
		Category existingCategory=getCategoryById(id);

		if(category.getName()!=null){
			existingCategory.setName(category.getName());
		}
		if(category.getImage()!=null){
			existingCategory.setImage(category.getImage());
		}

		return categoryRepository.save(existingCategory);
	}

	@Override
	public void deleteCategory(Long id) throws Exception {
		getCategoryById(id);
		categoryRepository.deleteById(id);
	}
}

