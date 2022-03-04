package springsecurity.core.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)

public class Account {

    @Id
    @GeneratedValue
    private Long id;

    private String username;

    private String password;

    private String email;

    private String age;

    private String role;

    public Account(String username, String password, String email, String age, String role) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.age = age;
        this.role = role;
    }

    public static Account convertDtoToAccount(AccountDto accountDto, PasswordEncoder passwordEncoder) {
        return new Account(accountDto.getUsername(),
                passwordEncoder.encode(accountDto.getPassword()),
                accountDto.getEmail(),
                accountDto.getAge(),
                accountDto.getRole());
    }
}
