package com.backend.lab.application.port.in.property.core;

import com.backend.lab.api.admin.property.core.dto.req.PropertyCreateReq;

public interface CreatePropertyUseCase {
    void execute(PropertyCreateReq req, Long adminId, String clientIp);
}
