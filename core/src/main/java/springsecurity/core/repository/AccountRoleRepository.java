package springsecurity.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import springsecurity.core.domain.entity.AccountRole;

import java.util.Optional;

public interface AccountRoleRepository extends JpaRepository<AccountRole, Long> {

    @Query("select ar from AccountRole ar join fetch ar.role r where r.roleName = :name")
    Optional<AccountRole> findByRoleName(@Param("name") String name);
}
