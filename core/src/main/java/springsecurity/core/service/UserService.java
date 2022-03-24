package springsecurity.core.service;

import springsecurity.core.security.configs.dto.AccountDto;
import springsecurity.core.domain.entity.Account;

import java.util.List;

public interface UserService {
    void createUser(Account account);

    void modifyUser(AccountDto accountDto);

    List<Account> getUsers();

    AccountDto getUser(Long id);

    void deleteUser(Long id);
}
