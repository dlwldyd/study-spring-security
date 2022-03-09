package springsecurity.core.init;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import springsecurity.core.domain.Account;
import springsecurity.core.service.UserService;

import javax.annotation.PostConstruct;

@Component
@RequiredArgsConstructor
public class InitUser {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    @PostConstruct
    public void init() {
        userService.createUser(new Account(
                "user",
                passwordEncoder.encode("user"),
                "ewofij@naver.com",
                "20",
                "ROLE_USER"));

        userService.createUser(new Account(
                "manager",
                passwordEncoder.encode("manager"),
                "vpcmef@google.com",
                "30",
                "ROLE_MANAGER"));

        userService.createUser(new Account(
                "admin",
                passwordEncoder.encode("admin"),
                "vpngnwp@daum.net",
                "33",
                "ROLE_ADMIN"));
    }
}
