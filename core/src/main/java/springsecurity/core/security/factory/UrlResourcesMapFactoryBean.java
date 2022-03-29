package springsecurity.core.security.factory;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.web.util.matcher.RequestMatcher;
import springsecurity.core.service.SecurityResourceService;

import java.util.LinkedHashMap;
import java.util.List;

// LinkedHashMap<RequestMatcher, List<ConfigAttribute>> 타입의 객체를 생성하는 팩토리
@RequiredArgsConstructor
public class UrlResourcesMapFactoryBean implements FactoryBean<LinkedHashMap<RequestMatcher, List<ConfigAttribute>>> {

    private final SecurityResourceService securityResourceService;
    /*
    구체적인 경로가 먼저 오고 더 큰 범위의 경로는 나중에 와야지 제대로된 권한 설정이 가능하기 때문에
    순서를 보장하지 않는 HashMap 이 아니라 순서를 보장하는 LinkedHashMap 을 사용해야한다.
     */
    private LinkedHashMap<RequestMatcher, List<ConfigAttribute>> resourceMap;

    //해당 타입의 객체를 생성해 반환함
    @Override
    public LinkedHashMap<RequestMatcher, List<ConfigAttribute>> getObject() throws Exception {

        if (resourceMap == null) {
            init();
        }

        return resourceMap;
    }

    private void init() {
        resourceMap = securityResourceService.getResourceList();
    }

    //어떤 타입의 객체를 생성하는지 반환함
    @Override
    public Class<?> getObjectType() {
        return LinkedHashMap.class;
    }

    //생성되는 객체가 싱글톤인지 아닌지
    @Override
    public boolean isSingleton() {
        return true;
    }
}
