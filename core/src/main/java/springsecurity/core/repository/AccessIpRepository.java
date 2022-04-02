package springsecurity.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import springsecurity.core.domain.entity.AccessIp;

public interface AccessIpRepository extends JpaRepository<AccessIp, Long> {
    AccessIp findByIpAddress(String IpAddress);
}
