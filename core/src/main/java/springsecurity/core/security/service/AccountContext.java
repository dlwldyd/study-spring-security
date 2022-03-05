package springsecurity.core.security.service;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import springsecurity.core.domain.Account;

import java.util.Collection;

@Getter
//User 는 UserDetails 를 구현하는 객체이다.
public class AccountContext extends User {

    private final Account account;

    //생성자
    public AccountContext(Account account, Collection<? extends GrantedAuthority> authorities) {
        super(account.getUsername(), account.getPassword(), authorities);
        this.account = account;
    }
}
