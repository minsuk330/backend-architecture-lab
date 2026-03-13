package com.backend.lab.application.service.property.status;

import com.backend.lab.api.admin.property.core.dto.req.PropertyStatusUpdateReq;
import com.backend.lab.api.admin.property.core.dto.resp.PropertyStatusResp;
import com.backend.lab.application.port.in.property.status.UpdatePropertyStatusUseCase;
import com.backend.lab.domain.property.core.entity.Property;
import com.backend.lab.domain.property.core.entity.vo.PropertyStatus;
import com.backend.lab.domain.property.core.service.PropertyService;
import com.backend.lab.domain.property.sale.entity.dto.SaleResp;
import com.backend.lab.domain.property.sale.service.SaleService;
import com.backend.lab.domain.uploadFile.entity.UploadFile;
import com.backend.lab.domain.uploadFile.service.UploadFileService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class UpdatePropertyStatusService implements UpdatePropertyStatusUseCase {

    private final PropertyService propertyService;
    private final SaleService saleService;
    private final UploadFileService uploadFileService;

    @Override
    public PropertyStatusResp update(Long propertyId, PropertyStatusUpdateReq req, Long adminId) {
        PropertyStatus updateStatus = req.getUpdateStatus();
        Property property = propertyService.getById(propertyId);

        switch (updateStatus) {
            case READY -> {
                property.setStatus(updateStatus);
                return PropertyStatusResp.builder().status(PropertyStatus.READY).build();
            }
            case COMPLETE -> {
                property.setStatus(updateStatus);
                property.setCompletedAt(req.getCompletedAt());
                return PropertyStatusResp.builder()
                    .status(PropertyStatus.COMPLETE)
                    .completedAt(req.getCompletedAt())
                    .build();
            }
            case PENDING -> {
                property.setStatus(updateStatus);
                property.setPendingAt(req.getPendingAt());
                return PropertyStatusResp.builder()
                    .status(PropertyStatus.PENDING)
                    .pendingAt(req.getPendingAt())
                    .build();
            }
            case SOLD -> {
                property.setStatus(updateStatus);
                if (req.getSale() != null) {
                    UploadFile contract = null;
                    if (req.getSale().getContractId() != null) {
                        contract = uploadFileService.getById(req.getSale().getContractId());
                    }
                    req.getSale().setContract(contract);
                    saleService.create(req.getSale(), adminId, propertyId);
                    SaleResp saleResp = SaleResp.builder()
                        .saleAt(req.getSale().getSaleAt())
                        .memberId(adminId)
                        .earningPrice(req.getSale().getEarningPrice())
                        .salePrice(req.getSale().getSalePrice())
                        .propertyId(propertyId)
                        .build();
                    return PropertyStatusResp.builder()
                        .status(PropertyStatus.SOLD)
                        .sale(saleResp)
                        .build();
                }
            }
        }
        return PropertyStatusResp.builder().status(updateStatus).build();
    }
}
