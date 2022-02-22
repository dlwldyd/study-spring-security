package security.basic_security;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.security.web.session.HttpSessionEventPublisher;

@SpringBootApplication
public class BasicSecurityApplication {

	public static void main(String[] args) {
		SpringApplication.run(BasicSecurityApplication.class, args);
	}
}
