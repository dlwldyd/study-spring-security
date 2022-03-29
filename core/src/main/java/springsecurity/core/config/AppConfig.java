package springsecurity.core.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springsecurity.core.repository.ResourcesRepository;
import springsecurity.core.security.factory.UrlResourcesMapFactoryBean;
import springsecurity.core.service.SecurityResourceService;

@Configuration
public class AppConfig {

    @Bean
    public SecurityResourceService securityResourceService(ResourcesRepository resourcesRepository) {
        return new SecurityResourceService(resourcesRepository);
    }
}
