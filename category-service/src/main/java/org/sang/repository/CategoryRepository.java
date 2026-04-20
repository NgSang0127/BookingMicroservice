package org.sang.repository;

import java.util.Set;
import org.sang.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Category,Long> {
	Set<Category> findByClinicId(Long id);
}
