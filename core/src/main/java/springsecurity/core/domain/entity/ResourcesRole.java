package springsecurity.core.domain.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor
public class ResourcesRole {

    @Id
    @GeneratedValue
    @Column(name = "resources_role_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resources_id")
    private Resources resources;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id")
    private Role role;

    public ResourcesRole(Resources resources, Role role) {
        this.resources = resources;
        this.role = role;
    }
}
