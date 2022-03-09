package springsecurity.core.security.configs;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import springsecurity.core.security.common.AjaxLoginAuthenticationEntryPoint;
import springsecurity.core.security.handler.AjaxAccessDeniedHandler;
import springsecurity.core.security.handler.AjaxAuthenticationFailureHandler;
import springsecurity.core.security.handler.AjaxAuthenticationSuccessHandler;
import springsecurity.core.security.provider.AjaxAuthenticationProvider;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@Order(0)
public class AjaxSecurityConfig extends WebSecurityConfigurerAdapter {

    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;

//    @Bean
//    public AjaxLoginProcessingFilter ajaxLoginProcessingFilter() throws Exception {
//        AjaxLoginProcessingFilter ajaxLoginProcessingFilter = new AjaxLoginProcessingFilter();
//        //ajaxLoginProcessingFilter 에서 인증처리를 위임할 AuthenticationManager 를 설정해야한다.
//        ajaxLoginProcessingFilter.setAuthenticationManager(authenticationManagerBean());
//
//        //핸들러는 여기서 추가해야함
//        ajaxLoginProcessingFilter.setAuthenticationSuccessHandler(ajaxAuthenticationSuccessHandler());
//        ajaxLoginProcessingFilter.setAuthenticationFailureHandler(ajaxAuthenticationFailureHandler());
//        return ajaxLoginProcessingFilter;
//    }

    @Bean
    public AuthenticationProvider ajaxAuthenticationProvider(UserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
        return new AjaxAuthenticationProvider(userDetailsService, passwordEncoder);
    }

    @Bean
    public AuthenticationSuccessHandler ajaxAuthenticationSuccessHandler() {
        return new AjaxAuthenticationSuccessHandler();
    }

    @Bean
    public AuthenticationFailureHandler ajaxAuthenticationFailureHandler() {
        return new AjaxAuthenticationFailureHandler();
    }

    @Bean
    public AccessDeniedHandler ajaxAccessDeniedHandler() {
        return new AjaxAccessDeniedHandler();
    }

    //AuthenticationManager 를 얻어오는 메서드
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(ajaxAuthenticationProvider(userDetailsService, passwordEncoder));
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http
                .antMatcher("/api/**") // "/api"로 시작하는 url 에 대해서만 작동함
                .authorizeRequests()
                .antMatchers("/api/messages").hasRole("MANAGER")
                .anyRequest().authenticated();

//        http
                //UsernamePasswordAuthenticationFilter 앞에 AjaxLoginProcessingFilter 추가
                //.addFilterBefore(ajaxLoginProcessingFilter(), UsernamePasswordAuthenticationFilter.class);

                //가장 마지막에 필터 추가
                //.addFilter(ajaxLoginProcessingFilter())

                //UsernamePasswordAuthenticationFilter 뒤에 AjaxLoginProcessingFilter 추가
                //.addFilterAfter(ajaxLoginProcessingFilter(), UsernamePasswordAuthenticationFilter.class)

                //UsernamePasswordAuthenticationFilter 를 AjaxLoginProcessingFilter 로 replace 한다.
                //.addFilterAt(ajaxLoginProcessingFilter(), UsernamePasswordAuthenticationFilter.class)

        customConfigurerAjax(http);

        http.exceptionHandling()
                .authenticationEntryPoint(new AjaxLoginAuthenticationEntryPoint())
                .accessDeniedHandler(ajaxAccessDeniedHandler());
    }

    private void customConfigurerAjax(HttpSecurity http) throws Exception {
        http.apply(new AjaxLoginConfigurer<>())
                .successHandlerAjax(ajaxAuthenticationSuccessHandler())
                .failureHandlerAjax(ajaxAuthenticationFailureHandler())
                .setAuthenticationManager(authenticationManagerBean());
    }
}
