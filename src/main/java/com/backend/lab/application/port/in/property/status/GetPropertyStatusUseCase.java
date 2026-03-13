package com.backend.lab.application.port.in.property.status;

import com.backend.lab.api.admin.property.core.dto.resp.PropertyStatusResp;

public interface GetPropertyStatusUseCase {
    PropertyStatusResp getStatus(Long propertyId);
}
