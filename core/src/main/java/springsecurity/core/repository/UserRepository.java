package springsecurity.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import springsecurity.core.domain.entity.Account;

import java.util.List;

public interface UserRepository extends JpaRepository<Account, Long> {

    @Query("select distinct a from Account a join fetch a.userRoles ur join fetch ur.role where a.username = :username")
    Account findAccountAndRoleByUsername(@Param("username") String username);

    Account findByUsername(String username);

    @Query("select distinct a from Account a join fetch a.userRoles ur join fetch ur.role")
    List<Account> findAllUsers();
}
