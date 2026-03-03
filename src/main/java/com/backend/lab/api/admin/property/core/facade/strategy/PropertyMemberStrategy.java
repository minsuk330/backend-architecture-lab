package com.backend.lab.api.admin.property.core.facade.strategy;

import com.backend.lab.api.admin.property.core.dto.req.PropertyCustomerCreateReq;
import com.backend.lab.domain.admin.core.entity.Admin;

public interface PropertyMemberStrategy {

  boolean supports(PropertyCustomerCreateReq req);

  void handle(PropertyCustomerCreateReq req, Long propertyId, Admin admin, String clientIp);
}