package springsecurity.core.security.voter;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import springsecurity.core.service.SecurityResourceService;

import java.util.Collection;
import java.util.List;

//제네릭에는 요청 자원 정보가 들어있는 클래스가 들어간다.
//모든 요청에 대해 투표하길 원하면 Object 를 넣으면 된다.
@RequiredArgsConstructor
public class IpAddressVoter implements AccessDecisionVoter<Object> {

    private final SecurityResourceService securityResourceService;

    //해당 voter 가 ConfigAttribute(요청 자원에 대한 권한 정보들)를 보고 투표할 수 있는지 판단함
    @Override
    public boolean supports(ConfigAttribute attribute) {
        return true;
    }

    //해당 클래스타입(요청 자원 정보가 들어있는 객체의 타입)을 보고 투표할 수 있는지 판단함
    //보통 파라미터에는 FilterInvocation.class(요청 자원 정보)가 들어온다.
    @Override
    public boolean supports(Class<?> clazz) {
        return true;
    }

    //실질적인 투표를 하는 로직이 들어간다.
    @Override
    public int vote(Authentication authentication, Object object, Collection<ConfigAttribute> attributes) {

        WebAuthenticationDetails details = (WebAuthenticationDetails) authentication.getDetails();
        String remoteAddress = details.getRemoteAddress();

        List<String> accessIpList = securityResourceService.getAccessIpList();

        for (String s : accessIpList) {
            if (s.equals(remoteAddress)) {
                return ACCESS_ABSTAIN;
            }
        }

        throw new AccessDeniedException("인증되지 않은 IP 주소입니다.");
    }
}
