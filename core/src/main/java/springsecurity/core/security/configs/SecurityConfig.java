package springsecurity.core.security.configs;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationDetailsSource;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import springsecurity.core.security.handler.CustomAccessDeniedHandler;
import springsecurity.core.security.provider.CustomAuthenticationProvider;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@Order(1)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final UserDetailsService userDetailsService;
    private final AuthenticationDetailsSource authenticationDetailsSource;
    private final AuthenticationSuccessHandler authenticationSuccessHandler;
    private final AuthenticationFailureHandler authenticationFailureHandler;

    //PasswordEncoder 스프링 빈으로 등록
    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public AuthenticationProvider customAuthenticationProvider() {
        return new CustomAuthenticationProvider(userDetailsService, passwordEncoder());
    }

    @Bean
    public AccessDeniedHandler accessDeniedHandler() {
        CustomAccessDeniedHandler accessDeniedHandler = new CustomAccessDeniedHandler();
        accessDeniedHandler.setErrorPage("/denied");
        return accessDeniedHandler;
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
                /*
                loginPage("/login").permitAll()은 파라미터가 붙지 않은 "/login"만 모든 사용자가 접근 가능하다.
                로그인 에러를 파라미터의 형태로 로그인 페이지에 전달하기 위해서는 "/login*"도 모든 사용자의 접근을 허용해야 한다.
                 */
                .antMatchers("/", "/users", "/login*", "/api/login").permitAll()
                .antMatchers("/mypage").hasRole("USER")
                .antMatchers("/messages").hasRole("MANAGER")
                .antMatchers("/config").hasRole("ADMIN")
                .anyRequest().authenticated();

        http.formLogin()
                .loginPage("/login")
//                .defaultSuccessUrl("/")
                .loginProcessingUrl("/login_proc")
                .authenticationDetailsSource(authenticationDetailsSource) //직접 만든 AuthenticationDetailsSource 등록
                .successHandler(authenticationSuccessHandler)
                .failureHandler(authenticationFailureHandler)
                .permitAll();

        http.exceptionHandling()
                .accessDeniedHandler(accessDeniedHandler()); //권한이 없는 리소스에 접근했을 때(인가 예외가 발생했을 때) 사용되는 핸들러

    }

    //보안 예외처리
    @Override
    public void configure(WebSecurity web) throws Exception {
        //webIgnore 설정, 정적 리소스들이 스프링 시큐리티 필터를 거치지 않는다.(그렇기 때문에 permitAll() 보다 비용이 싸다)
        web.ignoring().requestMatchers(PathRequest.toStaticResources().atCommonLocations());
    }
}
