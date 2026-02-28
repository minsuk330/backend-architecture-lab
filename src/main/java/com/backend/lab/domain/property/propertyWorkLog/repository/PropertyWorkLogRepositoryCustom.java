package com.backend.lab.domain.property.propertyWorkLog.repository;

import com.backend.lab.domain.property.propertyWorkLog.entity.dto.req.PropertyWorkLogDetailOptions;
import com.backend.lab.domain.property.propertyWorkLog.entity.dto.req.PropertyWorkLogDetailsOptions;
import com.backend.lab.domain.property.propertyWorkLog.entity.dto.req.SearchPropertyWorkLogOptions;
import com.backend.lab.domain.property.propertyWorkLog.entity.dto.resp.PropertyWorkLogDetailResp;
import com.backend.lab.domain.property.propertyWorkLog.entity.dto.resp.PropertyWorkLogResp;
import org.springframework.data.domain.Page;

public interface PropertyWorkLogRepositoryCustom {

  Page<PropertyWorkLogResp> search(SearchPropertyWorkLogOptions options);

  Page<PropertyWorkLogDetailResp> getByWorkLogId(PropertyWorkLogDetailOptions options);

  Page<PropertyWorkLogDetailResp> getsByPropertyId(PropertyWorkLogDetailsOptions options);
}
