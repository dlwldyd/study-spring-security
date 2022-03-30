package springsecurity.core.domain.entity;

import lombok.*;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RoleHierarchy {

    @Id
    @GeneratedValue
    @Column(name = "hierarchy_id")
    private Long id;

    private String childName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_name")
    private RoleHierarchy parentName;

    @OneToMany(mappedBy = "parentName")
    private final Set<RoleHierarchy> roleHierarchy = new HashSet<>();

    public RoleHierarchy(String childName) {
        this.childName = childName;
    }

    public RoleHierarchy(String childName, RoleHierarchy parentName) {
        this.childName = childName;
        this.parentName = parentName;
    }

    public void setParentName(RoleHierarchy parentName) {
        this.parentName = parentName;
    }
}
