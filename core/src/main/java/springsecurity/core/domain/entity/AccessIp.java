package springsecurity.core.domain.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AccessIp {

    @Id
    @GeneratedValue
    @Column(name = "ip_id")
    private Long id;

    private String ipAddress;

    public AccessIp(String ipAddress) {
        this.ipAddress = ipAddress;
    }
}
