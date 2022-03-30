package springsecurity.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import springsecurity.core.domain.entity.RoleHierarchy;

public interface RoleHierarchyRepository extends JpaRepository<RoleHierarchy, Long> {

    RoleHierarchy findByChildName(String roleName);
}
