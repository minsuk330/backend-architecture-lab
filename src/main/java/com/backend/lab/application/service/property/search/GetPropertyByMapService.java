package com.backend.lab.application.service.property.search;

import com.backend.lab.api.admin.property.core.dto.req.PropertyMapListReq;
import com.backend.lab.api.admin.property.core.dto.resp.PropertyAdminMapDto;
import com.backend.lab.api.admin.property.core.dto.resp.PropertyAddressResp;
import com.backend.lab.api.member.common.property.wishlist.dto.PropertyListResp;
import com.backend.lab.application.port.in.property.search.GetPropertyByMapUseCase;
import com.backend.lab.common.entity.dto.resp.ListResp;
import com.backend.lab.domain.property.core.service.PropertyService;
import com.backend.lab.domain.uploadFile.entity.dto.resp.UploadFileResp;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetPropertyByMapService implements GetPropertyByMapUseCase {

    private final PropertyService propertyService;

    @Override
    public ListResp<PropertyListResp> getByMap(PropertyMapListReq req, List<Long> adminIds,
                                               List<Long> bigCategoryIds, List<Long> smallCategoryIds) {
        List<PropertyAdminMapDto> list = propertyService.getsByMapDto(req, adminIds, bigCategoryIds, smallCategoryIds);
        List<PropertyListResp> data = list.stream().map(this::toResp).toList();
        return new ListResp<>(data);
    }

    private PropertyListResp toResp(PropertyAdminMapDto dto) {
        PropertyAddressResp addressResp = PropertyAddressResp.builder()
            .pnu(dto.getPnu())
            .roadAddress(dto.getRoadAddress())
            .jibunAddress(dto.getJibunAddress())
            .lat(dto.getLat())
            .lng(dto.getLng())
            .build();

        UploadFileResp thumbnailResp = null;
        if (dto.getThumbnailFileUrl() != null) {
            thumbnailResp = UploadFileResp.builder()
                .fileName(dto.getThumbnailFileName())
                .fileUrl(dto.getThumbnailFileUrl())
                .build();
        }

        return PropertyListResp.builder()
            .id(dto.getId())
            .createdAt(dto.getCreatedAt())
            .updatedAt(dto.getUpdatedAt())
            .mmPrice(dto.getMmPrice())
            .pyeongPrice(dto.getPyeongPrice())
            .status(dto.getStatus())
            .buildingName(dto.getBuildingName())
            .thumbnailImage(thumbnailResp)
            .propertyAddressResp(addressResp)
            .minFloor(dto.getMinFloor())
            .maxFloor(dto.getMaxFloor())
            .yeonAreaPyeng(round(dto.getTotalYeonAreaPyeng()))
            .yeonAreaMeter(round(dto.getTotalYeonAreaMeter()))
            .buildingAreaPyeng(round(dto.getTotalBuildingAreaPyeng()))
            .buildingAreaMeter(round(dto.getTotalBuildingAreaMeter()))
            .areaMeter(round(dto.getTotalLandAreaPyeong()))
            .areaPyeng(round(dto.getTotalLandAreaMeter()))
            .build();
    }

    private Double round(Double value) {
        if (value == null || value <= 0) return null;
        return Math.round(value * 100.0) / 100.0;
    }
}
