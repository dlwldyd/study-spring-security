package springsecurity.core.security.metadatasource;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.access.intercept.FilterInvocationSecurityMetadataSource;
import org.springframework.security.web.util.matcher.RequestMatcher;
import springsecurity.core.service.SecurityResourceService;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@RequiredArgsConstructor
public class UrlFilterInvocationSecurityMetadataSource implements FilterInvocationSecurityMetadataSource {

    private final Map<RequestMatcher, List<ConfigAttribute>> requestMap;
    private final SecurityResourceService securityResourceService;


    //MetadataSource 에 Map 의 형태로 저장되어 있는 자원에 대한 권한 정보를 가져오는 메서드이다.
    @Override
    public Collection<ConfigAttribute> getAttributes(Object object) throws IllegalArgumentException {

        /*
        url 방식의 MetadataSource 면 FilterInvocation 이 들어오지만 메서드 방식이면 MethodInvocation 이 들어오기 때문에
        Object 타입을 받고 그걸 타입 캐스팅 해서 써야한다.
         */
        HttpServletRequest request = ((FilterInvocation) object).getRequest();

        if (requestMap != null) {
            for (Map.Entry<RequestMatcher, List<ConfigAttribute>> entry : requestMap.entrySet()) {
                if (entry.getKey().matches(request)) {
                    return entry.getValue();
                }
            }
        }

        return null;
    }

    @Override
    public Collection<ConfigAttribute> getAllConfigAttributes() {
        Set<ConfigAttribute> allAttributes = new HashSet<>();
        this.requestMap.values().forEach(allAttributes::addAll);
        return allAttributes;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        //url 방식으로 권한을 부여하면 getAttributes 의 파라미터로 FilterInvocation, 메서드 방식이면 MethodInvocation 을 받는다.
        return FilterInvocation.class.isAssignableFrom(clazz);
    }

    public void reload() {
        LinkedHashMap<RequestMatcher, List<ConfigAttribute>> reloadedMap = securityResourceService.getResourceList();
        requestMap.clear();
        requestMap.putAll(reloadedMap);
    }
}
