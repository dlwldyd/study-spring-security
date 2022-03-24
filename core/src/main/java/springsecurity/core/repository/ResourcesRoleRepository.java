package springsecurity.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import springsecurity.core.domain.entity.ResourcesRole;

public interface ResourcesRoleRepository extends JpaRepository<ResourcesRole, Long> {
}
