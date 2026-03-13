package com.backend.lab.application.port.in.property.status;

import com.backend.lab.api.admin.property.core.dto.req.PropertyStatusUpdateReq;
import com.backend.lab.api.admin.property.core.dto.resp.PropertyStatusResp;

public interface UpdatePropertyStatusUseCase {
    PropertyStatusResp update(Long propertyId, PropertyStatusUpdateReq req, Long adminId);
}
