package springsecurity.core.controller.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import springsecurity.core.security.configs.dto.RoleDto;
import springsecurity.core.domain.entity.Role;
import springsecurity.core.service.RoleService;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;

    @GetMapping("/admin/roles")
    public String getRoles(Model model) throws Exception {

        List<Role> roles = roleService.getRoles();
        model.addAttribute("roles", roles);

        return "admin/role/list";
    }

    @GetMapping("/admin/roles/register")
    public String viewRoles(Model model) throws Exception {

        RoleDto role = new RoleDto();
        model.addAttribute("role", role);

        return "admin/role/detail";
    }

    @PostMapping("/admin/roles")
    public String createRole(RoleDto roleDto) throws Exception {

        Role role = roleDto.createRole();
        roleService.createRole(role);

        return "redirect:/admin/roles";
    }

    @GetMapping("/admin/roles/{id}")
    public String getRole(@PathVariable Long id, Model model) throws Exception {

        Role role = roleService.getRole(id);

        RoleDto roleDto = RoleDto.of(role);
        model.addAttribute("role", roleDto);

        return "admin/role/detail";
    }

    @GetMapping("/admin/roles/delete/{id}")
    public String removeResources(@PathVariable Long id) throws Exception {

        roleService.deleteRole(id);

        return "redirect:/admin/resources";
    }
}