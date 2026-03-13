package com.backend.lab.application.port.in.property.search;

import com.backend.lab.api.admin.property.core.dto.resp.PropertyStatResp;

public interface GetPropertyStatUseCase {
    PropertyStatResp getStat(Long totalDepartmentId, Long totalMajorCategoryId, Long totalAdminId,
                             Long monthlyAdminId, Long monthlyDepartmentId, Long monthlyMajorCategoryId);
}
