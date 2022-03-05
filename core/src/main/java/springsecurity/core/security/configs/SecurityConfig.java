package springsecurity.core.security.configs;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationDetailsSource;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import springsecurity.core.security.provider.CustomAuthenticationProvider;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final UserDetailsService userDetailsService;
    private final AuthenticationDetailsSource authenticationDetailsSource;

    //PasswordEncoder 스프링 빈으로 등록
    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public CustomAuthenticationProvider customAuthenticationProvider() {
        return new CustomAuthenticationProvider(userDetailsService, passwordEncoder());
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        //사용자에 대한 인증처리를 할 때 여기에 userDetailsService() 로 등록된 UserDetailsService 를 사용한다.
        //UserDetailsService 를 구현하고 있는 객체를 등록해야한다.
        //auth.userDetailsService()나 http.userDetailsService()나 다를건 없다.
//        auth.userDetailsService(userDetailsService); -> CustomAuthenticationProvider 가 userDetailsService 를 사용하고 있기 때문에 필요 없음


        //CustomAuthenticationProvider 등록
        auth.authenticationProvider(customAuthenticationProvider());
    }

    //보안 처리
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/", "/users").permitAll()
                .antMatchers("/mypage").hasRole("USER")
                .antMatchers("/messages").hasRole("MANAGER")
                .antMatchers("config").hasRole("ADMIN")
                .anyRequest().authenticated()

                .and().formLogin()
                .loginPage("/login")
                .defaultSuccessUrl("/")
                .loginProcessingUrl("/login_proc")
                .authenticationDetailsSource(authenticationDetailsSource) //직접 만든 AuthenticationDetailsSource 등록
                .permitAll();
    }

    //보안 예외처리
    @Override
    public void configure(WebSecurity web) throws Exception {
        //webIgnore 설정, 정적 리소스들이 스프링 시큐리티 필터를 거치지 않는다.(그렇기 때문에 permitAll() 보다 비용이 싸다)
        web.ignoring().requestMatchers(PathRequest.toStaticResources().atCommonLocations());
    }
}
