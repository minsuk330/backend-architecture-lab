package com.backend.lab.application.service.property.core;

import com.backend.lab.api.admin.property.core.dto.resp.PropertyResp;
import com.backend.lab.api.admin.property.core.mapper.PropertyMapper;
import com.backend.lab.application.port.in.property.core.GetPropertyDetailUseCase;
import com.backend.lab.domain.details.entity.Details;
import com.backend.lab.domain.details.service.DetailsService;
import com.backend.lab.domain.property.core.entity.Property;
import com.backend.lab.domain.property.core.service.PropertyService;
import com.backend.lab.domain.property.sale.entity.Sale;
import com.backend.lab.domain.property.sale.service.SaleService;
import com.backend.lab.domain.uploadFile.entity.dto.resp.UploadFileResp;
import com.backend.lab.domain.uploadFile.service.UploadFileService;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetPropertyDetailService implements GetPropertyDetailUseCase {

    private final PropertyService propertyService;
    private final DetailsService detailsService;
    private final SaleService saleService;
    private final UploadFileService uploadFileService;
    private final PropertyMapper propertyMapper;

    @Override
    public PropertyResp get(Long propertyId, Long userId) {
        Property property = propertyService.getByIdWithAllDetails(propertyId);

        Details details = detailsService.getByPropertyId(propertyId);

        List<UploadFileResp> propertyImages = details != null && details.getPropertyImages() != null
            ? details.getPropertyImages().stream().map(uploadFileService::uploadFileResp).toList()
            : new ArrayList<>();

        List<UploadFileResp> dataImages = details != null && details.getDataImages() != null
            ? details.getDataImages().stream().map(uploadFileService::uploadFileResp).toList()
            : new ArrayList<>();

        Sale sale = saleService.getByProperty(propertyId);

        return propertyService.propertyResp(property,
            propertyMapper.propertyAdminResp(property),
            propertyMapper.propertyDefaultResp(property, null),
            propertyMapper.propertyPriceResp(property),
            propertyMapper.propertyLedgeResp(property),
            propertyMapper.toPropertyFloorResp(property),
            propertyMapper.propertyLandResp(property),
            detailsService.detailsResp(details, propertyImages, dataImages),
            propertyMapper.propertyAddressResp(property),
            sale != null ? saleService.saleResp(sale) : null,
            propertyMapper.propertyTemplateResp(property)
        );
    }
}
