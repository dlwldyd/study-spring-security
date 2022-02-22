package security.basic_security.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableWebSecurity
@Order(0) //Order 는 구체적인 경로에 매치되는 SecurityConfig 가 더 앞에 오도록 해야함
public class SecurityConfig2 extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.antMatcher("/admin/config2")
                .authorizeRequests()
                .anyRequest().permitAll()
                .and()
                .httpBasic();
    }
}
