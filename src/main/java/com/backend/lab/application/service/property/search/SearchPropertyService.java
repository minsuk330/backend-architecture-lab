package com.backend.lab.application.service.property.search;

import com.backend.lab.api.admin.property.core.mapper.PropertyMapper;
import com.backend.lab.api.common.searchFilter.dto.req.SearchPropertyOptions;
import com.backend.lab.application.port.in.property.search.SearchPropertyUseCase;
import com.backend.lab.common.entity.dto.resp.PageResp;
import com.backend.lab.domain.property.core.entity.Property;
import com.backend.lab.domain.property.core.entity.dto.resp.PropertySearchResp;
import com.backend.lab.domain.property.core.service.PropertyService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SearchPropertyService implements SearchPropertyUseCase {

    private final PropertyService propertyService;
    private final PropertyMapper propertyMapper;

    @Override
    public PageResp<PropertySearchResp> search(SearchPropertyOptions options, List<Long> adminIds,
                                               List<Long> bigCategoryIds, List<Long> smallCategoryIds) {
        Page<Property> search = propertyService.search(options, adminIds, bigCategoryIds, smallCategoryIds);
        List<PropertySearchResp> list = search.stream().map(propertyMapper::propertySearchResp).toList();
        return new PageResp<>(search, list);
    }
}
