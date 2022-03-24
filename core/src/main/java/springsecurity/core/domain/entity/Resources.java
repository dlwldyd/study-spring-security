package springsecurity.core.domain.entity;

import lombok.*;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Resources {

    @Id
    @GeneratedValue
    @Column(name = "resource_id")
    private Long id;

    private String resourceName;

    private String httpMethod;

    private int orderNum;

    private String resourceType;

    @OneToMany(mappedBy = "resources", cascade = CascadeType.ALL, orphanRemoval = true)
    private final Set<ResourcesRole> roleSet = new HashSet<>();

    public Resources(String resourceName, String httpMethod, int orderNum, String resourceType) {
        this.resourceName = resourceName;
        this.httpMethod = httpMethod;
        this.orderNum = orderNum;
        this.resourceType = resourceType;
    }
}
