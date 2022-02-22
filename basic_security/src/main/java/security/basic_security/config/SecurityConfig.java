package security.basic_security.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.security.web.session.HttpSessionEventPublisher;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSessionListener;
import java.io.IOException;
import java.util.Enumeration;

@Configuration
@EnableWebSecurity //스프링 시큐리티 configuration 객체에 해당 어노테이션을 꼭 붙여줘야함
@RequiredArgsConstructor
@Order(1)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final UserDetailsService userDetailsService;

    @Bean
    public static ServletListenerRegistrationBean<HttpSessionListener> httpSessionEventPublisher() {
        return new ServletListenerRegistrationBean<>(new HttpSessionEventPublisher());
    }

    //http 요청에 대한 인증, 인가를 설정할 때 이 메서드를 오버라이드 해야함
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication().withUser("user").password("{noop}1").roles("USER");
        auth.inMemoryAuthentication().withUser("sys").password("{noop}1").roles("SYS");
        auth.inMemoryAuthentication().withUser("admin").password("{noop}1").roles("ADMIN");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        configureAuthorizePolicy(http);

        configureAuthenticationPolicy(http);

        configureLogoutPolicy(http);

        configureRememberMePolicy(http);

        configureSessionManagementPolicy(http);

        configureExceptionHandlingPolicy(http);

        SecurityContextHolder.setStrategyName(SecurityContextHolder.MODE_THREADLOCAL);
//        SecurityContextHolder.setStrategyName(SecurityContextHolder.MODE_INHERITABLETHREADLOCAL);
//        SecurityContextHolder.setStrategyName(SecurityContextHolder.MODE_GLOBAL);
    }

    private void configureAuthorizePolicy(HttpSecurity http) throws Exception {
        //인가 정책
        http.authorizeRequests() //http request 인가 시작
                .antMatchers("/denied").permitAll()
                .antMatchers("/user").hasRole("USER") // "/user" url 에 접근하려면 USER 라는 role 을 가져야함
                .antMatchers("/admin/pay").hasRole("ADMIN") //"/admin/pay" url 에 접근하려면 ADMIN 라는 role 을 가져야함
                .antMatchers("/admin/**").access("hasRole('ADMIN') or hasRole('SYS')") // "/admin" 하위의 url(/pay 제외)에 접근하려면 ADMIN or SYS 라는 role 을 가져야함
                .anyRequest().authenticated(); //모든 request 에 대해 (인가에 대한)인증을 수행함
    }

    private void configureAuthenticationPolicy(HttpSecurity http) throws Exception {
        //인증 정책
        http.formLogin() //form 로그인 방식으로 인증 수행
//                .loginPage("/loginPage") //로그인 페이지 지정
//                .defaultSuccessUrl("/") //인증이 성공했을 때 이동할 url
//                .failureUrl("/login") // 인증이 실패했을 때 이동할 url
                .usernameParameter("userId") //아이디가 어떤 파라미터명으로 들어올지 지정(default : username)
                .passwordParameter("passwd") //패스워드가 어떤 파라미터명으로 들어올지 지정(default : password)
                .loginProcessingUrl("/login_proc") //로그인 폼 데이터가 어떤 url 로 올지 지정(form 태그의 action 속성)
                .successHandler((request, response, authentication) -> {
                    //인증 성공 후 실행할 로직을 넣어준다.
                    //authentication 에는 인증에 성공한 사용자의 정보와 사용자의 권한 정보가 들어있다.
                    /*
                    핸들러는 하나만 넣을 수 있다. 따라서 defaultSuccessUrl()은 해당 url 로 redirect 하는 핸들러를
                    추가하는 메서드이기 때문에 내가 따로 핸들러를 만들어서 넣는다면 해당 핸들러에 redirect 로직을 넣어야 한다.
                     */

                    System.out.println("request.getRequestURL() = " + request.getRequestURL());
                    Enumeration<String> headerNames = request.getHeaderNames();
                    while (headerNames.hasMoreElements()) {
                        String name = headerNames.nextElement();
                        String header = request.getHeader(name);
                        System.out.println(name + " = " + header);
                    }
                    System.out.println("authentication.getName() = " + authentication.getName());
                    RequestCache requestCache = new HttpSessionRequestCache(); //session 에 savedRequest 를 저장하고 가져오는 역할을 하는 객체
                    SavedRequest savedRequest = requestCache.getRequest(request, response); //savedRequest 를 가지고옴
                    response.sendRedirect(savedRequest.getRedirectUrl()); //savedRequest 의 url 로 redirect
                })
                .failureHandler((request, response, exception) -> {
                    //인증 성공 후 실행할 로직을 넣어준다.
                    /*
                    핸들러는 하나만 넣을 수 있다. 따라서 failureUrl()은 해당 url 로 redirect 하는 핸들러를
                    추가하는 메서드이기 때문에 내가 따로 핸들러를 만들어서 넣는다면 해당 핸들러에 redirect 로직을 넣어야 한다.
                     */

                    //예외 출력
                    System.out.println("exception.getMessage() = " + exception.getMessage());
                    response.sendRedirect("/login");
                })
                .permitAll(); //http 에 설정한 로그인 페이지에 인증을 받지 않아도 접근할 수 있도록 함
    }

    private void configureLogoutPolicy(HttpSecurity http) throws Exception {
        http.logout() //로그아웃 설정 시작
                .logoutUrl("/logout") //로그아웃 url, default 로 post 방식으로 "/logout"을 받아야한다.
                .logoutSuccessUrl("/login") //로그아웃 성공시 리다이렉트될 url
                .addLogoutHandler((request, response, authentication) -> {
                    //로그아웃 처리시 수행되는 핸들러
                    //세션을 무효화시키는 핸들러 등이 기본적으로 있고, 그 외에도 추가적인 작업을 하고싶을 때 새로운 핸들러를 만들어 추가해준다.
                    System.out.println("logout");

                })
                .logoutSuccessHandler((request, response, authentication) -> {
                    //로그아웃 성공시 수행되는 핸들러
                    /*
                    핸들러는 하나만 넣을 수 있다. 따라서 logoutSuccessUrl()은 해당 url 로 redirect 하는 핸들러를
                    추가하는 메서드이기 때문에 내가 따로 핸들러를 만들어서 넣는다면 해당 핸들러에 redirect 로직을 넣어야 한다.
                     */
                    System.out.println("logout success");

                    response.sendRedirect("/login");
                })
                .deleteCookies("JSESSIONID", "remember-me"); //해당하는 이름의 쿠키를 삭제(JSESSIONID, remember-me 쿠키 삭제)
    }

    private void configureRememberMePolicy(HttpSecurity http) throws Exception {
        http.rememberMe()
                .rememberMeParameter("remember") //remember me 쿠키를 사용할지 말지를 정하는 파라미터(체크박스에 체크시 true)의 이름을 정함, 기본 파라미터명은 remember-me
                .tokenValiditySeconds(3600) //remember me 쿠키 만료 시간, default 는 14일(1209600초)
//                .alwaysRemember(true) //remember me 쿠키를 사용한다는 체크박스에 체크하지 않더라도 항상 remember me 쿠키 사용, default 는 false, 일반적으로는 false 로 사용한다.
                .userDetailsService(userDetailsService); //필수
    }

    private void configureSessionManagementPolicy(HttpSecurity http) throws Exception {
        http.sessionManagement() //동시 세션제어 설정 시작
                .sessionFixation().changeSessionId()
                /*
                SessionCreationPolicy.ALWAYS : 스프링 시큐리티가 항상 세션 생성
                SessionCreationPolicy.IF_REQUIRED : 스프링 시큐리티가 필요시 세션 생성(default)
                SessionCreationPolicy.NEVER : 스프링 시큐리티가 세션을 생성하지 않지만 이미 존재하면 사용
                SessionCreationPolicy.STATELESS : 스프링 시큐리티가 세션을 생성하지도 않고 존재해도 사용하지 않음(JWT 인증 방식을 사용할 때 사용)
                 */
                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                .invalidSessionUrl("/login") //세션이 유효하지 않을 때 이동할 url
                .maximumSessions(1) //최대 혀용 가능 세션 수, -1일 경우 같은 아이디로 무제한 로그인 세션 허용
                /*
                최대 세션 혀용 개수를 초과했을 경우에 어떤 세션 전략을 사용할지 설정함
                true 면 현재 로그인된 사용자가 있을 시 새로 로그인하는 사용자에게 인증 예외를 전달
                false 면 현재 로그인 된 사용자가 있을 시 현재 로그인된 사용자의 세션을 만료시키고 새로 로그인하는 사용자의
                세션을 생성함(default : false)
                */
                .maxSessionsPreventsLogin(false)
                .expiredUrl("/login"); // 세션이 만료된 경우 이동할 url
    }

    private void configureExceptionHandlingPolicy(HttpSecurity http) throws Exception {
        http.exceptionHandling()
                .authenticationEntryPoint(new AuthenticationEntryPoint() {
//                      인증 예외 발생 시(익명 사용자가 인가되지 않은 리소스에 접근했을 때) 실행할 로직을 넣는다.

                    @Override
                    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
                        response.sendRedirect("/login");
                    }
                })
                .accessDeniedHandler(new AccessDeniedHandler() {
                    @Override
                    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
                        //인가 예외 발생 시 실행할 로직을 넣는다.

                        response.sendRedirect("/denied");
                    }
                });
    }
}
