package com.backend.lab.application.service.property.search;

import com.backend.lab.api.admin.property.core.dto.req.PropertyStatReq;
import com.backend.lab.api.admin.property.core.dto.req.PropertyStatReq.FilterOptions;
import com.backend.lab.api.admin.property.core.dto.resp.PropertyStatResp;
import com.backend.lab.application.port.in.property.search.GetPropertyStatUseCase;
import com.backend.lab.domain.property.core.service.PropertyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetPropertyStatService implements GetPropertyStatUseCase {

    private final PropertyService propertyService;

    @Override
    public PropertyStatResp getStat(Long totalDepartmentId, Long totalMajorCategoryId, Long totalAdminId,
                                    Long monthlyAdminId, Long monthlyDepartmentId, Long monthlyMajorCategoryId) {
        PropertyStatReq req = new PropertyStatReq();
        if (totalDepartmentId != null || totalMajorCategoryId != null || totalAdminId != null) {
            FilterOptions totalFilter = new FilterOptions();
            totalFilter.setDepartmentId(totalDepartmentId);
            totalFilter.setMajorCategoryId(totalMajorCategoryId);
            totalFilter.setAdminId(totalAdminId);
            req.setTotal(totalFilter);
        }
        if (monthlyDepartmentId != null || monthlyMajorCategoryId != null || monthlyAdminId != null) {
            FilterOptions monthlyFilter = new FilterOptions();
            monthlyFilter.setDepartmentId(monthlyDepartmentId);
            monthlyFilter.setMajorCategoryId(monthlyMajorCategoryId);
            monthlyFilter.setAdminId(monthlyAdminId);
            req.setMonthly(monthlyFilter);
        }
        return propertyService.getStat(req);
    }
}
