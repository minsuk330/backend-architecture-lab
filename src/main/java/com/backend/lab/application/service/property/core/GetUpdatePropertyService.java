package com.backend.lab.application.service.property.core;

import com.backend.lab.api.admin.property.core.dto.resp.PropertyUpdateResp;
import com.backend.lab.api.admin.property.core.mapper.PropertyMapper;
import com.backend.lab.application.port.in.property.core.GetUpdatePropertyUseCase;
import com.backend.lab.domain.admin.core.entity.Admin;
import com.backend.lab.domain.admin.core.service.AdminService;
import com.backend.lab.domain.property.core.entity.Property;
import com.backend.lab.domain.property.core.service.PropertyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetUpdatePropertyService implements GetUpdatePropertyUseCase {

    private final PropertyService propertyService;
    private final AdminService adminService;
    private final PropertyMapper propertyMapper;

    @Override
    public PropertyUpdateResp getUpdateProperty(Long adminId, Long propertyId) {
        Property property = propertyService.getByIdWithAllDetails(propertyId);
        Admin admin = adminService.getById(adminId);
        return propertyMapper.propertyUpdateResp(admin, property);
    }
}
