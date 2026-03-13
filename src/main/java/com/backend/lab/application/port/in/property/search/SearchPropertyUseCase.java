package com.backend.lab.application.port.in.property.search;

import com.backend.lab.api.common.searchFilter.dto.req.SearchPropertyOptions;
import com.backend.lab.common.entity.dto.resp.PageResp;
import com.backend.lab.domain.property.core.entity.dto.resp.PropertySearchResp;
import java.util.List;

public interface SearchPropertyUseCase {
    PageResp<PropertySearchResp> search(SearchPropertyOptions options, List<Long> adminIds, List<Long> bigCategoryIds, List<Long> smallCategoryIds);
}
