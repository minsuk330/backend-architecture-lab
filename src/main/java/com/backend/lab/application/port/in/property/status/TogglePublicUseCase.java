package com.backend.lab.application.port.in.property.status;

import com.backend.lab.api.admin.property.core.dto.resp.PropertyPublicResp;

public interface TogglePublicUseCase {
    PropertyPublicResp toggle(Long propertyId);
}
