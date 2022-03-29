package springsecurity.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import springsecurity.core.domain.entity.Resources;

import java.util.List;

public interface ResourcesRepository extends JpaRepository<Resources, Long> {

    Resources findByResourceNameAndHttpMethod(String resourceName, String httpMethod);

    //구체적인 경로가 먼저 오고 더 큰 범위의 경로는 나중에 와야지 제대로된 권한 설정이 가능하기 때문에 orderNum 으로 정렬했다.
    //orderNum 이 작을수록 구체적인 경로이고, 클 수록 큰 범위의 경로이다.
    @Query("select r from Resources r join fetch r.roleSet rs join fetch rs.role where r.resourceType = 'url' order by r.orderNum desc")
    List<Resources> findAllResources();

    @Query("select r from Resources r join fetch r.roleSet where r.resourceType = 'method' order by r.orderNum desc")
    List<Resources> findAllMethodResources();

    @Query("select r from Resources r join fetch r.roleSet where r.resourceType = 'pointcut' order by r.orderNum desc")
    List<Resources> findAllPointcutResources();
}
