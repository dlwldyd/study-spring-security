package springsecurity.core.domain.entity;

import lombok.*;

import javax.persistence.*;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Role {

    @Id
    @GeneratedValue
    @Column(name = "role_id")
    private Long id;

    private String roleName;

    private String roleDesc;

    @OneToMany(mappedBy = "role")
    private final Set<ResourcesRole> resourcesSet = new LinkedHashSet<>();

    @OneToMany(mappedBy = "account")
    private final Set<AccountRole> accounts = new HashSet<>();

    public Role(String roleName, String roleDesc) {
        this.roleName = roleName;
        this.roleDesc = roleDesc;
    }


}
