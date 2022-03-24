package springsecurity.core.security.configs.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.crypto.password.PasswordEncoder;
import springsecurity.core.domain.entity.Account;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
public class AccountDto {

    private Long id;

    private String username;

    private String password;

    private String email;

    private Integer age;

    private List<String> roles;

    public AccountDto(Long id, String username, String password, String email, int age) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.email = email;
        this.age = age;
    }

    public static AccountDto of(Account account) {
        AccountDto accountDto = new AccountDto(account.getId(),
                account.getUsername(),
                account.getPassword(),
                account.getEmail(),
                account.getAge());

        List<String> roleList = account.getUserRoles().stream().map(accountRole -> accountRole.getRole().getRoleName()).collect(Collectors.toList());
        accountDto.setRoles(roleList);

        return accountDto;
    }

    public Account createAccount(PasswordEncoder passwordEncoder) {
        return new Account(this.username,
                passwordEncoder.encode(this.password),
                this.email,
                this.age);
    }
}
