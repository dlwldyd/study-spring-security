package springsecurity.core.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import springsecurity.core.domain.entity.Resources;
import springsecurity.core.repository.ResourcesRepository;
import springsecurity.core.service.ResourcesService;

import javax.persistence.EntityNotFoundException;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ResourcesServiceImpl implements ResourcesService {

    private final ResourcesRepository resourcesRepository;

    @Transactional
    public void createResources(Resources resources){
        resourcesRepository.save(resources);
    }

    @Transactional
    public void deleteResources(long id) {
        resourcesRepository.deleteById(id);
    }

    public Resources getResources(long id) {
        return resourcesRepository.findById(id).orElseThrow(EntityNotFoundException::new);
    }

    public List<Resources> getResources() {
        return resourcesRepository.findAll(Sort.by(Sort.Order.asc("orderNum")));
    }
}
