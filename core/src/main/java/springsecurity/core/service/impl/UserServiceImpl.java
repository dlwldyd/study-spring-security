package springsecurity.core.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import springsecurity.core.security.configs.dto.AccountDto;
import springsecurity.core.domain.entity.Account;
import springsecurity.core.domain.entity.AccountRole;
import springsecurity.core.domain.entity.Role;
import springsecurity.core.repository.AccountRoleRepository;
import springsecurity.core.repository.RoleRepository;
import springsecurity.core.repository.UserRepository;
import springsecurity.core.service.UserService;

import javax.persistence.EntityNotFoundException;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final AccountRoleRepository accountRoleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void createUser(Account account) {

        Role role = roleRepository.findByRoleName("ROLE_USER");
        account.getUserRoles().add(new AccountRole(account, role));
        userRepository.save(account);
    }

    @Transactional
    @Override
    public void modifyUser(AccountDto accountDto){

        Account account = accountDto.createAccount(passwordEncoder);

        if(!accountDto.getRoles().isEmpty()){
            accountDto.getRoles().stream().forEach(s ->
                    account.getUserRoles().add(accountRoleRepository.findByRoleName(s).orElseThrow(EntityNotFoundException::new))
            );
        }
        account.setPassword(accountDto.getPassword(), passwordEncoder);
        userRepository.save(account);
    }

    @Transactional
    @Override
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    public AccountDto getUser(Long id) {

        Account account = userRepository.findById(id).orElseThrow(EntityNotFoundException::new);

        return AccountDto.of(account);
    }

    public List<Account> getUsers() {
        return userRepository.findAllUsers();
    }
}
