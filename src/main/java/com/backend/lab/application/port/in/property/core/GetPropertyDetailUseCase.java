package com.backend.lab.application.port.in.property.core;

import com.backend.lab.api.admin.property.core.dto.resp.PropertyResp;

public interface GetPropertyDetailUseCase {
    PropertyResp get(Long propertyId, Long userId);
}
