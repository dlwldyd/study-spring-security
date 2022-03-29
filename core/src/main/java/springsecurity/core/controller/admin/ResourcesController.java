package springsecurity.core.controller.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import springsecurity.core.security.configs.dto.ResourcesDto;
import springsecurity.core.domain.entity.Resources;
import springsecurity.core.domain.entity.ResourcesRole;
import springsecurity.core.domain.entity.Role;
import springsecurity.core.repository.RoleRepository;
import springsecurity.core.security.metadatasource.UrlFilterInvocationSecurityMetadataSource;
import springsecurity.core.service.ResourcesService;
import springsecurity.core.service.RoleService;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class ResourcesController {

    private final ResourcesService resourcesService;
    private final RoleRepository roleRepository;
    private final RoleService roleService;
    private final UrlFilterInvocationSecurityMetadataSource urlFilterInvocationSecurityMetadataSource;

    @GetMapping("/admin/resources")
    public String getResources(Model model) throws Exception {

        List<Resources> resources = resourcesService.getResources();
        model.addAttribute("resources", resources);

        return "admin/resource/list";
    }

    @PostMapping("/admin/resources")
    public String createResources(ResourcesDto resourcesDto) throws Exception {

        Role role = roleRepository.findByRoleName(resourcesDto.getRoleName());
        Resources resources = resourcesDto.createResources();
        resources.getRoleSet().add(new ResourcesRole(resources, role));

        resourcesService.createResources(resources);
        urlFilterInvocationSecurityMetadataSource.reload();

        return "redirect:/admin/resources";
    }

    @GetMapping("/admin/resources/register")
    public String viewRoles(Model model) throws Exception {

        List<Role> roleList = roleService.getRoles();
        model.addAttribute("roleList", roleList);

        ResourcesDto resourcesDto = new ResourcesDto();
        model.addAttribute("resources", resourcesDto);

        return "admin/resource/detail";
    }

    @GetMapping("/admin/resources/{id}")
    public String getResources(@PathVariable Long id, Model model) throws Exception {

        List<Role> roleList = roleService.getRoles();
        model.addAttribute("roleList", roleList);
        Resources resources = resourcesService.getResources(id);

        ResourcesDto resourcesDto = ResourcesDto.of(resources);
        model.addAttribute("resources", resourcesDto);

        return "admin/resource/detail";
    }

    @GetMapping("/admin/resources/delete/{id}")
    public String removeResources(@PathVariable Long id, Model model) throws Exception {

        resourcesService.deleteResources(id);
        urlFilterInvocationSecurityMetadataSource.reload();
        return "redirect:/admin/resources";
    }
}
