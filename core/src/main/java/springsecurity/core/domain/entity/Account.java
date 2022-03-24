package springsecurity.core.domain.entity;

import lombok.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import springsecurity.core.security.configs.dto.AccountDto;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Account {

    @Id
    @GeneratedValue
    @Column(name = "account_id")
    private Long id;

    private String username;

    private String password;

    private String email;

    private Integer age;

    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL, orphanRemoval = true)
    private final Set<AccountRole> userRoles = new HashSet<>();

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password, PasswordEncoder passwordEncoder) {
        this.password = passwordEncoder.encode(password);
    }

    public Account(String username, String password, String email, int age) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.age = age;
    }

    public Account(Account account) {
        this.username = account.getUsername();
        this.password = account.getPassword();
        this.email = account.getEmail();
        this.age = account.getAge();
    }

    public static Account of(AccountDto accountDto, PasswordEncoder passwordEncoder) {
        return new Account(accountDto.getUsername(),
                passwordEncoder.encode(accountDto.getPassword()),
                accountDto.getEmail(),
                accountDto.getAge());
    }
}
