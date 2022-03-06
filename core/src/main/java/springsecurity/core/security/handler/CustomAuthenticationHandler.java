package springsecurity.core.security.handler;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.stereotype.Component;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class CustomAuthenticationHandler extends SimpleUrlAuthenticationSuccessHandler {

    //session 에 savedRequest 를 저장하고 가져오는 역할을 하는 객체
    private RequestCache requestCache = new HttpSessionRequestCache();

    //response.sendRedirect()를 통해 리다이렉트 할 수도 있겠지만 이거를 사용하는 편이 더 좋다.
    private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        //SimpleUrlAuthenticationHandler 가 상속하는 AbstractAuthenticationTargetUrlRequestHandler 의 메서드임
        setDefaultTargetUrl("/");

        SavedRequest savedRequest = requestCache.getRequest(request, response);
        if (savedRequest != null) {
            String targetUrl = savedRequest.getRedirectUrl();
            redirectStrategy.sendRedirect(request, response, targetUrl);
        } else {
            //getDefaultTargetUrl() 은 SimpleUrlAuthenticationHandler 가 상속하는 AbstractAuthenticationTargetUrlRequestHandler 의 메서드임
            redirectStrategy.sendRedirect(request, response, getDefaultTargetUrl());
        }
    }
}
