package com.backend.lab.application.port.in.property.core;

import com.backend.lab.api.admin.property.core.dto.req.PropertyUpdateReq;

public interface UpdatePropertyUseCase {
    void execute(PropertyUpdateReq req, Long adminId, Long propertyId, String clientIp);
}
