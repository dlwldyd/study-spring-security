package springsecurity.core.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import springsecurity.core.domain.entity.RoleHierarchy;
import springsecurity.core.repository.RoleHierarchyRepository;
import springsecurity.core.service.RoleHierarchyService;

import java.util.Iterator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RoleHierarchyServiceImpl implements RoleHierarchyService {

    private final RoleHierarchyRepository roleHierarchyRepository;

    @Override
    public String findAllHierarchy() {
        List<RoleHierarchy> roleHierarchy = roleHierarchyRepository.findAll();

        Iterator<RoleHierarchy> iterator = roleHierarchy.iterator();
        StringBuilder concatRole = new StringBuilder();
        while (iterator.hasNext()) {
            RoleHierarchy hierarchy = iterator.next();
            if (hierarchy.getParentName() != null) {
                concatRole.append(hierarchy.getParentName().getChildName());
                concatRole.append(" > ");
                concatRole.append(hierarchy.getChildName());
                concatRole.append("\n");
            }
        }

        return concatRole.toString();
    }
}
