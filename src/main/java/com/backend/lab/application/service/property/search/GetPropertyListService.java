package com.backend.lab.application.service.property.search;

import com.backend.lab.api.admin.property.core.mapper.PropertyMapper;
import com.backend.lab.api.member.common.property.wishlist.dto.PropertyListResp;
import com.backend.lab.application.port.in.property.search.GetPropertyListUseCase;
import com.backend.lab.common.entity.dto.resp.ListResp;
import com.backend.lab.domain.property.core.entity.Property;
import com.backend.lab.domain.property.core.service.PropertyService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetPropertyListService implements GetPropertyListUseCase {

    private final PropertyService propertyService;
    private final PropertyMapper propertyMapper;

    @Override
    public ListResp<PropertyListResp> getList() {
        List<Property> list = propertyService.getList();
        List<PropertyListResp> data = list.stream().map(propertyMapper::propertyListResp).toList();
        return new ListResp<>(data);
    }
}
