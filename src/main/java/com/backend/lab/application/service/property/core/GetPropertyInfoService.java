package com.backend.lab.application.service.property.core;

import com.backend.lab.api.admin.property.core.dto.resp.PropertyDefaultResp;
import com.backend.lab.api.admin.property.core.dto.resp.PropertyDetailResp;
import com.backend.lab.api.admin.property.core.dto.resp.PropertyLandResp;
import com.backend.lab.api.admin.property.core.dto.resp.PropertyLedgeResp;
import com.backend.lab.api.admin.property.core.dto.resp.PropertyPriceResp;
import com.backend.lab.api.admin.property.core.mapper.PropertyMapper;
import com.backend.lab.application.port.in.property.core.GetPropertyInfoUseCase;
import com.backend.lab.domain.property.core.entity.Property;
import com.backend.lab.domain.property.core.service.PropertyService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetPropertyInfoService implements GetPropertyInfoUseCase {

    private final PropertyService propertyService;
    private final PropertyMapper propertyMapper;

    @Override
    public PropertyDetailResp getInfo(Long propertyId, Long userId) {
        Property property = propertyService.getByIdWithAllDetails(propertyId);
        List<PropertyLedgeResp> propertyLedgeResps = propertyMapper.propertyLedgeResp(property);
        PropertyDefaultResp propertyDefaultResp = propertyMapper.propertyDefaultResp(property, null);
        List<PropertyLandResp> propertyLandResps = propertyMapper.propertyLandResp(property);
        PropertyPriceResp propertyPriceResp = propertyMapper.propertyPriceResp(property);
        return PropertyDetailResp.builder()
            .propertyLedge(propertyLedgeResps.get(0))
            .propertyDefault(propertyDefaultResp)
            .propertyLand(propertyLandResps.get(0))
            .propertyPrice(propertyPriceResp)
            .build();
    }
}
