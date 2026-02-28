package com.backend.lab.domain.propertyRequest.repository;

import com.backend.lab.api.admin.PropertyRequest.dto.req.SearchPropertyRequestOptions;
import com.backend.lab.domain.propertyRequest.entity.PropertyRequest;
import org.springframework.data.domain.Page;

public interface PropertyRequestRepositoryCustom {

  Page<PropertyRequest> agentSearch(SearchPropertyRequestOptions options);

  Page<PropertyRequest> sellerSearch(SearchPropertyRequestOptions options);

  Page<PropertyRequest> customerSearch(SearchPropertyRequestOptions options);
}
