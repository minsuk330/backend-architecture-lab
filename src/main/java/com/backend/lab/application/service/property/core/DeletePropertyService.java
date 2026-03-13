package com.backend.lab.application.service.property.core;

import com.backend.lab.application.port.in.property.core.DeletePropertyUseCase;
import com.backend.lab.domain.admin.core.entity.Admin;
import com.backend.lab.domain.admin.core.service.AdminService;
import com.backend.lab.domain.property.core.entity.Property;
import com.backend.lab.domain.property.core.service.PropertyService;
import com.backend.lab.domain.property.propertyWorkLog.service.PropertyWorkLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DeletePropertyService implements DeletePropertyUseCase {

    private final PropertyService propertyService;
    private final AdminService adminService;
    private final PropertyWorkLogService propertyWorkLogService;

    @Override
    @Transactional
    public void delete(Long propertyId, Long userId, String clientIp) {
        Property property = propertyService.getById(propertyId);
        Admin admin = adminService.getById(userId);
        propertyWorkLogService.deleteLog(admin, property, clientIp);
        propertyService.deleteById(propertyId);
    }
}
