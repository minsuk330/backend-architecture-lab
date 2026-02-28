package com.backend.lab.api.admin.dbUpdate.facade;

import com.backend.lab.api.admin.dbUpdate.facade.dto.ConflictResolveReq;
import com.backend.lab.api.admin.property.core.mapper.PropertyMapper;
import com.backend.lab.common.entity.dto.req.PageOptions;
import com.backend.lab.common.entity.dto.resp.PageResp;
import com.backend.lab.common.util.PnuComponents;
import com.backend.lab.domain.property.conflictProperty.entity.ConflictProperty;
import com.backend.lab.domain.property.conflictProperty.service.ConflictPropertyService;
import com.backend.lab.domain.property.core.entity.Property;
import com.backend.lab.domain.property.core.entity.dto.resp.PropertySearchResp;
import com.backend.lab.domain.property.core.entity.embedded.AddressProperties;
import com.backend.lab.domain.property.core.entity.information.AddressInformation;
import com.backend.lab.domain.property.core.service.PropertyService;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminConflictFacade {

  private final ConflictPropertyService conflictPropertyService;
  private final PropertyService propertyService;

  private final PropertyMapper propertyMapper;

  public PageResp<PropertySearchResp> gets(PageOptions options) {
    Page<ConflictProperty> page = conflictPropertyService.gets(options);
    List<PropertySearchResp> data = page.getContent().stream()
        .map(ConflictProperty::getProperty)
        .map(propertyMapper::propertySearchResp)
        .toList();
    return new PageResp<>(page, data);
  }

  @Transactional
  public void resolve(Long propertyId, ConflictResolveReq req) {
    Property property = propertyService.getById(propertyId);
    Optional<ConflictProperty> optionalConflict = conflictPropertyService.getByProperty(property);

    if (optionalConflict.isEmpty()) {
      return;
    }

    PnuComponents pnuComponents = PnuComponents.parse(req.getPnu());

    ConflictProperty conflictProperty = optionalConflict.get();

    AddressInformation address = property.getAddress();
    AddressProperties properties = address.getProperties();
    properties.setPnu(req.getPnu());
    properties.setLat(req.getLat());
    properties.setLng(req.getLng());
    properties.setJibunAddress(req.getJibunAddress());
    properties.setRoadAddress(req.getRoadAddress());
    properties.setBunCode(Integer.valueOf(pnuComponents.getBun()));
    properties.setJiCode(Integer.valueOf(pnuComponents.getJi()));

    conflictPropertyService.delete(conflictProperty);
  }
}
