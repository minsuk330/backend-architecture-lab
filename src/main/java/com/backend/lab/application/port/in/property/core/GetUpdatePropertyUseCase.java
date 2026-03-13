package com.backend.lab.application.port.in.property.core;

import com.backend.lab.api.admin.property.core.dto.resp.PropertyUpdateResp;

public interface GetUpdatePropertyUseCase {
    PropertyUpdateResp getUpdateProperty(Long adminId, Long propertyId);
}
