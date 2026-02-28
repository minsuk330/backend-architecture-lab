package com.backend.lab.domain.property.core.repository;

import com.backend.lab.api.common.searchFilter.dto.req.SearchPropertyOptions;
import com.backend.lab.domain.property.core.entity.Property;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;

public interface PropertyDeletedRepositoryCustom {

  Page<Property> search(SearchPropertyOptions options, List<Long> adminIds,
      List<Long> bigCategoryIds, List<Long> smallCategoryIds);

  Optional<Property> getDeletedById(Long id);
}
