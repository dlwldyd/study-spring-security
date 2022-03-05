package springsecurity.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import springsecurity.core.domain.Account;

public interface UserRepository extends JpaRepository<Account, Long> {
    public Account findByUsername(String username);
}
