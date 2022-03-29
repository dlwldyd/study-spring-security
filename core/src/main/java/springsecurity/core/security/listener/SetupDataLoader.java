package springsecurity.core.security.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import springsecurity.core.domain.entity.*;
import springsecurity.core.repository.*;
import springsecurity.core.security.metadatasource.UrlFilterInvocationSecurityMetadataSource;

import java.util.concurrent.atomic.AtomicInteger;

@Component
@RequiredArgsConstructor
public class SetupDataLoader implements ApplicationListener<ContextRefreshedEvent> {

    private boolean alreadySetup = false;

    private final UserRepository userRepository;

    private final RoleRepository roleRepository;

    private final ResourcesRepository resourcesRepository;

    private final AccountRoleRepository accountRoleRepository;

    private final ResourcesRoleRepository resourcesRoleRepository;

    private final PasswordEncoder passwordEncoder;

    private final UrlFilterInvocationSecurityMetadataSource urlFilterInvocationSecurityMetadataSource;

    private static AtomicInteger count = new AtomicInteger(0);

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {

        if (alreadySetup) {
            return;
        }

        setupSecurityResources();

        alreadySetup = true;
    }

    @Transactional
    private void setupSecurityResources() {
        Account account = createUserIfNotFound("admin", "admin", "admin@gmail.com", 10);
        Role adminRole = createRoleIfNotFound("ROLE_ADMIN", "관리자");
        Resources resources = createResourceIfNotFound("/admin/**", "", "url");

        save(account, adminRole, resources);

        AccountRole accountRole = new AccountRole(account, adminRole);
        ResourcesRole resourcesRole = new ResourcesRole(resources, adminRole);

        save(accountRole, resourcesRole);

        account.getUserRoles().add(accountRole);
        resources.getRoleSet().add(resourcesRole);

//        Set<Role> roles1 = new HashSet<>();
//
//        Role managerRole = createRoleIfNotFound("ROLE_MANAGER", "매니저");
//        roles1.add(managerRole);
//        createResourceIfNotFound("io.security.corespringsecurity.aopsecurity.method.AopMethodService.methodTest", "", roles1, "method");
//        createResourceIfNotFound("io.security.corespringsecurity.aopsecurity.method.AopMethodService.innerCallMethodTest", "", roles1, "method");
//        createResourceIfNotFound("execution(* io.security.corespringsecurity.aopsecurity.pointcut.*Service.*(..))", "", roles1, "pointcut");
//        createUserIfNotFound("manager", "pass", "manager@gmail.com", 20, roles1);
//
//        Set<Role> roles3 = new HashSet<>();
//
//        Role childRole1 = createRoleIfNotFound("ROLE_USER", "회원");
//        roles3.add(childRole1);
//        createResourceIfNotFound("/users/**", "", roles3, "url");
//        createUserIfNotFound("user", "pass", "user@gmail.com", 30, roles3);

        urlFilterInvocationSecurityMetadataSource.reload();

    }

    private void save(AccountRole accountRole, ResourcesRole resourcesRole) {
        accountRoleRepository.save(accountRole);
        resourcesRoleRepository.save(resourcesRole);
    }

    private void save(Account account, Role role, Resources resources) {
        userRepository.save(account);
        roleRepository.save(role);
        resourcesRepository.save(resources);
    }

    private Role createRoleIfNotFound(String roleName, String roleDesc) {

        Role role = roleRepository.findByRoleName(roleName);

        if (role == null) {
            role = Role.builder()
                    .roleName(roleName)
                    .roleDesc(roleDesc)
                    .build();
        }
        return role;
    }

    private Account createUserIfNotFound(String userName, String password, String email, int age) {

        Account account = userRepository.findByUsername(userName);

        if (account == null) {
            account = Account.builder()
                    .username(userName)
                    .email(email)
                    .age(age)
                    .password(passwordEncoder.encode(password))
                    .build();
        }
        return account;
    }

    private Resources createResourceIfNotFound(String resourceName, String httpMethod, String resourceType) {
        Resources resources = resourcesRepository.findByResourceNameAndHttpMethod(resourceName, httpMethod);

        if (resources == null) {
            resources = Resources.builder()
                    .resourceName(resourceName)
                    .httpMethod(httpMethod)
                    .resourceType(resourceType)
                    .orderNum(count.incrementAndGet())
                    .build();
        }
        return resources;
    }
}
