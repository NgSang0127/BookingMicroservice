package org.sang.repository;

import java.util.Optional;
import org.sang.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {
	User findByEmail(String email);
	Optional<User> findByKeycloakId(String keycloakId);
}
