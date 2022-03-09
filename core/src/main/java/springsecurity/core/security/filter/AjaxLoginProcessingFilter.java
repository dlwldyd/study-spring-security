package springsecurity.core.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import springsecurity.core.domain.AccountDto;
import springsecurity.core.security.token.AjaxAuthenticationToken;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static org.springframework.util.StringUtils.*;

public class AjaxLoginProcessingFilter extends AbstractAuthenticationProcessingFilter {

    //Ajax 는 json 으로 데이터를 보내기 때문에 ObjectMapper 를 통해 json 을 객체로 변환해줄 필요가 있음
    private ObjectMapper objectMapper = new ObjectMapper();

    public AjaxLoginProcessingFilter() {
        // "/api/login"으로 요청이 들어오면 필터가 작동함
        super(new AntPathRequestMatcher("/api/login"));
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException, ServletException {

        if (!isAjax(request)) {
            throw new IllegalStateException("Authentication is not supported");
        }

        //json 으로 받은 유저 정보를 AccountDto 객체로 변환
        AccountDto accountDto = objectMapper.readValue(request.getReader(), AccountDto.class);
        if (!(hasText(accountDto.getUsername()) && hasText(accountDto.getPassword()))) {
            throw new IllegalArgumentException("Username or Password is empty");
        }

        //인증 처리를 위한 Authentication 토큰 생성
        AjaxAuthenticationToken authentication = new AjaxAuthenticationToken(accountDto.getUsername(), accountDto.getPassword());

        //AuthenticationManager 에게 인증처리 위임
        return getAuthenticationManager().authenticate(authentication);
    }

    private boolean isAjax(HttpServletRequest request) {
        //Ajax 인지 구분하는 특정한 방법이 있는게 아니라 그냥 클라이언트 측과의 약속을 통해 헤더에 Ajax 인지를 나타내는 정보를 덧붙여서 보냄

        //"X-Requested-with" 헤더 값이 "XMLHttpRequest" 면 Ajax 로그인 처리를 함
        if ("XMLHttpRequest".equals(request.getHeader("X-Requested-with"))) {
            return true;
        }

        return false;
    }
}
