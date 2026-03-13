package com.backend.lab.application.service.property.status;

import com.backend.lab.api.admin.property.core.dto.resp.PropertyStatusResp;
import com.backend.lab.application.port.in.property.status.GetPropertyStatusUseCase;
import com.backend.lab.domain.property.core.entity.Property;
import com.backend.lab.domain.property.core.entity.vo.PropertyStatus;
import com.backend.lab.domain.property.core.service.PropertyService;
import com.backend.lab.domain.property.sale.entity.Sale;
import com.backend.lab.domain.property.sale.entity.dto.SaleResp;
import com.backend.lab.domain.property.sale.service.SaleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetPropertyStatusService implements GetPropertyStatusUseCase {

    private final PropertyService propertyService;
    private final SaleService saleService;

    @Override
    public PropertyStatusResp getStatus(Long propertyId) {
        Property property = propertyService.getById(propertyId);
        PropertyStatus status = property.getStatus();

        return switch (status) {
            case READY -> PropertyStatusResp.builder()
                .status(PropertyStatus.READY)
                .build();
            case COMPLETE -> PropertyStatusResp.builder()
                .status(PropertyStatus.COMPLETE)
                .completedAt(property.getCompletedAt())
                .build();
            case PENDING -> PropertyStatusResp.builder()
                .status(PropertyStatus.PENDING)
                .pendingAt(property.getPendingAt())
                .build();
            case SOLD -> {
                Sale sale = saleService.getByProperty(propertyId);
                if (sale != null) {
                    SaleResp saleResp = saleService.saleResp(sale);
                    yield PropertyStatusResp.builder()
                        .status(PropertyStatus.SOLD)
                        .sale(saleResp)
                        .build();
                }
                yield PropertyStatusResp.builder().status(PropertyStatus.SOLD).build();
            }
        };
    }
}
