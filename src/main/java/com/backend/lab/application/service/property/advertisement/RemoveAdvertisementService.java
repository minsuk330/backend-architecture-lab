package com.backend.lab.application.service.property.advertisement;

import com.backend.lab.application.port.in.property.advertisement.RemoveAdvertisementUseCase;
import com.backend.lab.domain.propertyAdvertisement.service.PropertyAdvertisementService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RemoveAdvertisementService implements RemoveAdvertisementUseCase {

    private final PropertyAdvertisementService propertyAdvertisementService;

    @Override
    @Transactional
    public void remove(Long propertyId, Long agentId) {
        propertyAdvertisementService.removeAdvertisement(propertyId, agentId);
    }
}
