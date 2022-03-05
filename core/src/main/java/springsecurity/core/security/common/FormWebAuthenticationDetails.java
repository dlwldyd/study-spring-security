package springsecurity.core.security.common;

import lombok.Getter;
import org.springframework.security.web.authentication.WebAuthenticationDetails;

import javax.servlet.http.HttpServletRequest;

/*
authentication 의 멤버인 details(WebAuthenticationDetails 타입)에는 아이디와 비밀번호 외의 부가적인 정보가 들어간다.
클라이언트의 sessionId, address 가 기본적으로 들어가고 직접 WebAuthenticationDetails 를 상속한 클래스를 만들어서
다른 정보를 더 넣어도 된다. 만약 다른 정보를 넣을 필요가 없으면 굳이 아래와 같은 클래스를 만들 필요가 없다.
 */
@Getter
public class FormWebAuthenticationDetails extends WebAuthenticationDetails {

    private String secretKey;

    /**
     * Records the remote address and will also set the session Id if a session already
     * exists (it won't create one).
     *
     * @param request that the authentication request was received from
     */
    public FormWebAuthenticationDetails(HttpServletRequest request) {
        super(request);
        secretKey = request.getParameter("secret_key");
    }
}
