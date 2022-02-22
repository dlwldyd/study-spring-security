package security.basic_security.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SecurityController {

    @GetMapping("/")
    public String index() {
        return "home";
    }

    @GetMapping("/loginPage")
    public String loginPage() {
        return "loginPage";
    }

    @GetMapping("/user")
    public String user() {
        return "user";
    }

    @GetMapping("/admin/pay")
    public String adminPay() {
        return "adminPay";
    }

    @GetMapping("/admin/**")
    public String admin() {
        return "admin";
    }

    @GetMapping("/denied")
    public String denied() {
        return "denied";
    }

    @GetMapping("/admin/config2")
    public String config2() {
        return "config2 applied";
    }

    @GetMapping("/thread")
    public String thread() {

        Authentication auth1 = SecurityContextHolder.getContext().getAuthentication();

        new Thread(() -> {
            Authentication auth2 = SecurityContextHolder.getContext().getAuthentication();
        }); // SecurityContextHolder 의 mode 에 따라 같은 객체일 수도 있고 다른 객체일 수도 있다.

        return "thread";
    }
}
