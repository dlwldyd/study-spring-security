package springsecurity.core.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import springsecurity.core.domain.entity.Role;
import springsecurity.core.repository.RoleRepository;
import springsecurity.core.service.RoleService;

import javax.persistence.EntityNotFoundException;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;

    @Transactional
    public void createRole(Role role){

        roleRepository.save(role);
    }

    @Transactional
    public void deleteRole(Long id) {
        roleRepository.deleteById(id);
    }

    public Role getRole(Long id) {
        return roleRepository.findById(id).orElseThrow(EntityNotFoundException::new);
    }

    public List<Role> getRoles() {

        return roleRepository.findAll();
    }
}