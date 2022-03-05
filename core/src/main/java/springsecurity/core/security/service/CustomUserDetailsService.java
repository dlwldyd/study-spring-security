package springsecurity.core.security.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import springsecurity.core.domain.Account;
import springsecurity.core.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Account findAccount = userRepository.findByUsername(username);

        if (findAccount == null) {
            throw new UsernameNotFoundException("UsernameNoFoundException");
        }

        //GrantedAuthority 는 인터페이스이고 SimpleGrantedAuthority 는 구현체이다.
        //SimpleGrantedAuthority 는 그냥 권한(String)과 그에 대한 EqualsAndHashCode, getter, toString 이 구현되어 있는 객체이다.(별거 없음)
        List<GrantedAuthority> roles = new ArrayList<>();
        roles.add(new SimpleGrantedAuthority(findAccount.getRole()));

        //UserDetails 의 구현체를 반환해야함
        /*
        UserDetails 에는 아이디, 권한, 비밀번호를 가져오는 메서드와
        계정이 잠겨있는지, 계정이 만료됐는지, 비밀번호가 만료됐는지, 사용할 수 없는 계정인지를 반환하는 메서드가 있다.
         */
        return new AccountContext(findAccount, roles);
    }
}
