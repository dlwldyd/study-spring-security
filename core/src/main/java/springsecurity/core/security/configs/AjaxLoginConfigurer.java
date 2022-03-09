package springsecurity.core.security.configs;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.HttpSecurityBuilder;
import org.springframework.security.config.annotation.web.configurers.AbstractAuthenticationFilterConfigurer;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import springsecurity.core.security.filter.AjaxLoginProcessingFilter;

//스프링 시큐리티 설정 API 로 사용 가능
//SecurityConfigurer 를 구현해서 만들 수 있음, AbstractAuthenticationFilterConfigurer 는 SecurityConfigurer 의 구현체
//SecurityConfigurer 를 구현하기에는 기능도 적고 기능을 전부 구현하려면 시간이 많이 드니깐 AbstractAuthenticationFilterConfigurer 를 상속받자
/*
<H extends HttpSecurityBuilder<H>> -> 어떤 configure 메서드에서 설정할지
configure(HttpSecurity http)
configure(AuthenticationManagerBuilder auth)
configure(WebSecurity web)
 */
//<H, AjaxLoginConfigurer<H>, AjaxLoginProcessingFilter> -> <H, AjaxLoginConfigurer<H>, 적용할 필터>
public final class AjaxLoginConfigurer<H extends HttpSecurityBuilder<H>> extends
        AbstractAuthenticationFilterConfigurer<H, AjaxLoginConfigurer<H>, AjaxLoginProcessingFilter> {

    private AuthenticationSuccessHandler successHandler;
    private AuthenticationFailureHandler failureHandler;
    private AuthenticationManager authenticationManager;

    public AjaxLoginConfigurer() {
        //필터와 해당 필터가 적용될 url
        super(new AjaxLoginProcessingFilter(), "/api/login");
    }

    @Override
    public void init(H http) throws Exception {
        super.init(http);
    }

    //WebSecurityConfigurerAdapter 의 configure 메서드가 실행된 후 실행됨
    @Override
    public void configure(H http) {
        //apply 시점에 적용됨

        if(authenticationManager == null){
            //스프링 시큐리티에서 공유하는 객체를 가져오는 메서드
            //Map<Class<?>, Object> 에서 꺼내옴
            authenticationManager = http.getSharedObject(AuthenticationManager.class);
        }
        getAuthenticationFilter().setAuthenticationManager(authenticationManager);
        getAuthenticationFilter().setAuthenticationSuccessHandler(successHandler);
        getAuthenticationFilter().setAuthenticationFailureHandler(failureHandler);

        SessionAuthenticationStrategy sessionAuthenticationStrategy = http
                .getSharedObject(SessionAuthenticationStrategy.class);
        if (sessionAuthenticationStrategy != null) {
            getAuthenticationFilter().setSessionAuthenticationStrategy(sessionAuthenticationStrategy);
        }
        RememberMeServices rememberMeServices = http
                .getSharedObject(RememberMeServices.class);
        if (rememberMeServices != null) {
            //내가 설정한 인증 필터(여기서는 AjaxLoginProcessingFilter)의 RememberMeServices 를 설정
            getAuthenticationFilter().setRememberMeServices(rememberMeServices);
        }
        //Map<Class<?>, Object> 에 내가 설정한 인증 필터(여기서는 AjaxLoginProcessingFilter) 넣음
        http.setSharedObject(AjaxLoginProcessingFilter.class, getAuthenticationFilter());
        http.addFilterBefore(getAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
    }

    public AjaxLoginConfigurer<H> successHandlerAjax(AuthenticationSuccessHandler successHandler) {
        this.successHandler = successHandler;
        return this;
    }

    public AjaxLoginConfigurer<H> failureHandlerAjax(AuthenticationFailureHandler authenticationFailureHandler) {
        this.failureHandler = authenticationFailureHandler;
        return this;
    }

    public AjaxLoginConfigurer<H> setAuthenticationManager(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
        return this;
    }

    @Override
    protected RequestMatcher createLoginProcessingUrlMatcher(String loginProcessingUrl) {
        return new AntPathRequestMatcher(loginProcessingUrl, "POST");
    }
}