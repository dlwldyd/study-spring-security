package springsecurity.core.security.configs;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.access.vote.AffirmativeBased;
import org.springframework.security.access.vote.RoleHierarchyVoter;
import org.springframework.security.access.vote.RoleVoter;
import org.springframework.security.authentication.AuthenticationDetailsSource;
import org.springframework.security.authentication.AuthenticationManager;
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
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import springsecurity.core.security.factory.UrlResourcesMapFactoryBean;
import springsecurity.core.security.filter.PermitAllFilter;
import springsecurity.core.security.handler.CustomAccessDeniedHandler;
import springsecurity.core.security.metadatasource.UrlFilterInvocationSecurityMetadataSource;
import springsecurity.core.security.provider.CustomAuthenticationProvider;
import springsecurity.core.security.voter.IpAddressVoter;
import springsecurity.core.service.SecurityResourceService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@Order(1)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final UserDetailsService userDetailsService;
    private final AuthenticationDetailsSource authenticationDetailsSource;
    private final AuthenticationSuccessHandler authenticationSuccessHandler;
    private final AuthenticationFailureHandler authenticationFailureHandler;
    private final SecurityResourceService securityResourceService;
    private String[] permitAllResources = {"/", "/login", "/user/login/**"};

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

    @Bean
    public UrlFilterInvocationSecurityMetadataSource urlFilterInvocationSecurityMetadataSource() throws Exception {
        return new UrlFilterInvocationSecurityMetadataSource(urlResourcesMapFactoryBean().getObject(), securityResourceService);
    }

    @Bean
    public UrlResourcesMapFactoryBean urlResourcesMapFactoryBean() {
        return new UrlResourcesMapFactoryBean(securityResourceService);
    }

    @Bean
    public AccessDecisionManager accessDecisionManager() {
        //AccessDecisionVoter 는 AccessDecisionManager 를 스프링 빈으로 등록할 때 주입해야 한다.
        return new AffirmativeBased(getAccessDecisionVoters());
    }

    @Bean
    public AccessDecisionVoter<? extends Object> roleVoter() {
        return new RoleHierarchyVoter(roleHierarchy());
    }

    @Bean
    public RoleHierarchyImpl roleHierarchy() {
        return new RoleHierarchyImpl();
    }

    private List<AccessDecisionVoter<?>> getAccessDecisionVoters() {

        List<AccessDecisionVoter<? extends Object>> accessDecisionVoters = new ArrayList<>();
        //voter 를 추가할 때의 순서에 따라 결과가 달리질 수 있기 때문에 voter 의 추가 순서도 잘 생각해서 정해야한다.
        accessDecisionVoters.add(new IpAddressVoter(securityResourceService));
        accessDecisionVoters.add(roleVoter());
        return accessDecisionVoters;
    }

    /*
    MetadataSource, AccessDecisionManager, AccessDecisionVoter 등을 설정하려면 FilterSecurityInterceptor 의
    setter 를 사용해 설정한 후 해당 FilterSecurityInterceptor 를 addFilterBefore 를 통해
    기존의 FilterSecurityInterceptor 앞에 추가해줘야 한다.
     */
    @Bean
    public FilterSecurityInterceptor customFilterSecurityInterceptor() throws Exception {
        FilterSecurityInterceptor filterSecurityInterceptor = new PermitAllFilter(permitAllResources);
        filterSecurityInterceptor.setSecurityMetadataSource(urlFilterInvocationSecurityMetadataSource());
        filterSecurityInterceptor.setAccessDecisionManager(accessDecisionManager());
        filterSecurityInterceptor.setAuthenticationManager(authenticationManagerBean());
        return filterSecurityInterceptor;
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
        http.authorizeRequests();
                /*
                loginPage("/login").permitAll()은 파라미터가 붙지 않은 "/login"만 모든 사용자가 접근 가능하다.
                로그인 에러를 파라미터의 형태로 로그인 페이지에 전달하기 위해서는 "/login*"도 모든 사용자의 접근을 허용해야 한다.
                 */
//                .antMatchers("/", "/users", "/login*", "/api/login").permitAll()
//                .antMatchers("/mypage").hasRole("USER")
//                .antMatchers("/messages").hasRole("MANAGER")
//                .antMatchers("/config").hasRole("ADMIN")
//                .anyRequest().authenticated();

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

        /*
        기본으로 등록되는 FilterSecurityInterceptor 앞에 내가 생성한 FilterSecurityInterceptor 를 등록해야
        내가 생성한 FilterSecurityInterceptor 가 동작한다. 한번 FilterSecurityInterceptor 에 의해
        권한 검사가 진행되면 다음 필터로 넘어가더라도 권한 검사를 진행하지 않는다.(그렇게 로직이 짜여있음)
         */
        http.addFilterBefore(customFilterSecurityInterceptor(), FilterSecurityInterceptor.class);

    }

    //보안 예외처리
    @Override
    public void configure(WebSecurity web) throws Exception {
        //webIgnore 설정, 정적 리소스들이 스프링 시큐리티 필터를 거치지 않는다.(그렇기 때문에 permitAll() 보다 비용이 싸다)
        web.ignoring().requestMatchers(PathRequest.toStaticResources().atCommonLocations());
    }

    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }
}
