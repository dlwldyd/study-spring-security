package springsecurity.core.security.configs.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import springsecurity.core.domain.entity.Role;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RoleDto {

    private Long id;
    private String roleName;
    private String roleDesc;

    public static RoleDto of(Role role) {
        return new RoleDto(role.getId(),
                role.getRoleName(),
                role.getRoleDesc());
    }

    public Role createRole() {
        return new Role(this.getRoleName(),
                this.getRoleDesc());
    }
}
