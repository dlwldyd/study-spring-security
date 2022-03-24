package springsecurity.core.security.configs.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import springsecurity.core.domain.entity.Resources;
import springsecurity.core.domain.entity.Role;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
public class ResourcesDto {

    private Long id;
    private String resourceName;
    private String httpMethod;
    private int orderNum;
    private String resourceType;
    private String roleName;
    private Set<Role> roleSet = new HashSet<>();

    public Resources createResources() {
        return new Resources(this.resourceName,
                this.httpMethod,
                this.orderNum,
                this.resourceType);
    }

    public ResourcesDto(Long id, String resourceName, String httpMethod, int orderNum, String resourceType) {
        this.id = id;
        this.resourceName = resourceName;
        this.httpMethod = httpMethod;
        this.orderNum = orderNum;
        this.resourceType = resourceType;
    }

    public static ResourcesDto of(Resources resources) {
        ResourcesDto resourcesDto = new ResourcesDto(resources.getId(),
                resources.getResourceName(),
                resources.getHttpMethod(),
                resources.getOrderNum(),
                resources.getResourceType());

        resources.getRoleSet().forEach(resourcesRole -> resourcesDto.getRoleSet().add(resourcesRole.getRole()));
        return resourcesDto;
    }
}
