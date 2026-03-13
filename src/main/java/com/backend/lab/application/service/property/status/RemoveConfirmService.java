package com.backend.lab.application.service.property.status;

import com.backend.lab.application.port.in.property.status.RemoveConfirmUseCase;
import com.backend.lab.domain.property.core.entity.Property;
import com.backend.lab.domain.property.core.service.PropertyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class RemoveConfirmService implements RemoveConfirmUseCase {

    private final PropertyService propertyService;

    @Override
    public void removeConfirm(Long propertyId) {
        Property property = propertyService.getById(propertyId);
        propertyService.deleteConfirm(property);
    }
}
