package com.backend.lab.application.service.property.status;

import com.backend.lab.api.admin.property.core.dto.resp.PropertyPublicResp;
import com.backend.lab.application.port.in.property.status.TogglePublicUseCase;
import com.backend.lab.domain.property.core.service.PropertyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TogglePublicService implements TogglePublicUseCase {

    private final PropertyService propertyService;

    @Override
    @Transactional
    public PropertyPublicResp toggle(Long propertyId) {
        Boolean currentStatus = propertyService.togglePublic(propertyId);
        return PropertyPublicResp.builder()
            .isPublic(currentStatus)
            .build();
    }
}
