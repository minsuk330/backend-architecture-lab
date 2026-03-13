package com.backend.lab.application.port.in.property.core;

import com.backend.lab.api.admin.property.core.dto.resp.PropertyDetailResp;

public interface GetPropertyInfoUseCase {
    PropertyDetailResp getInfo(Long propertyId, Long userId);
}
