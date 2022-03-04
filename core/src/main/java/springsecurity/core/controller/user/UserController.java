package springsecurity.core.controller.user;


import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import springsecurity.core.domain.Account;
import springsecurity.core.domain.AccountDto;
import springsecurity.core.service.UserService;

import static springsecurity.core.domain.Account.*;

@Controller
@RequiredArgsConstructor
public class UserController {

	private final PasswordEncoder passwordEncoder;

	private final UserService userService;

	@GetMapping("/mypage")
	public String myPage() throws Exception {
		return "user/mypage";
	}

	@GetMapping("/users")
	public String createUser() {
		return "user/login/register";
	}

	@PostMapping("/users")
	public String createUser(AccountDto accountDto) {
		userService.createUser(convertDtoToAccount(accountDto, passwordEncoder));
		return "redirect:/";
	}
}
