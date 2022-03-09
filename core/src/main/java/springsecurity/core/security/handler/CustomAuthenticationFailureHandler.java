package springsecurity.core.security.handler;

import org.springframework.context.annotation.Primary;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@Primary
public class CustomAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        //AuthenticationException 은 AuthenticationProvider 에서 인증이 실패했을 때 throw 된 exception 이다.

        String errorMessage = "Invalid Username or Password";

        if (exception instanceof InsufficientAuthenticationException) {
            errorMessage = "Invalid Secret Key"; //히든필드로 넘긴 secretKey 값이 "secret_key"가 아닐 때
        } else if (exception instanceof DisabledException) {
            errorMessage = "Locked";
        } else if (exception instanceof CredentialsExpiredException) {
            errorMessage = "Expired Password";
        }

        setDefaultFailureUrl("/login?error=true&exception=" + errorMessage);

        //SimpleUrlAuthentication 의 onAuthenticationFailure() 메서드에 위임
        super.onAuthenticationFailure(request, response, exception);
    }
}
