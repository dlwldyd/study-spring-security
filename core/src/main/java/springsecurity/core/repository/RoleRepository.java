package springsecurity.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import springsecurity.core.domain.entity.Role;

public interface RoleRepository extends JpaRepository<Role, Long> {

    Role findByRoleName(String name);

    @Override
    void delete(Role role);
}
