package com.backend.lab.api.admin.PropertyRequest.facade;

import com.backend.lab.api.admin.PropertyRequest.dto.req.SearchPropertyRequestOptions;
import com.backend.lab.api.admin.PropertyRequest.dto.resp.PropertyRequestAgentResp;
import com.backend.lab.api.admin.PropertyRequest.dto.resp.PropertyRequestCustomerResp;
import com.backend.lab.api.admin.PropertyRequest.dto.resp.PropertyRequestSellerResp;
import com.backend.lab.api.admin.PropertyRequest.mapper.PropertyRequestMapper;
import com.backend.lab.common.entity.dto.resp.PageResp;
import com.backend.lab.domain.propertyRequest.entity.PropertyRequest;
import com.backend.lab.domain.propertyRequest.service.PropertyRequestService;
import com.backend.lab.domain.uploadFile.service.UploadFileService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
@Slf4j
public class PropertyRequestFacade {

  private final PropertyRequestService propertyRequestService;
  private final UploadFileService uploadFileService;
  private final PropertyRequestMapper propertyRequestMapper;

  //id값으로 받는거 추가 검색
  public PageResp<PropertyRequestAgentResp> agentSearch(SearchPropertyRequestOptions options) {
    Page<PropertyRequest> page = propertyRequestService.agentSearch(options);
    List<PropertyRequestAgentResp> list = page.getContent().stream().map(
        propertyRequestMapper::propertyRequestAgentResp
    ).toList();

    return new PageResp<>(page, list);
  }

  public PageResp<PropertyRequestSellerResp> sellerSearch(SearchPropertyRequestOptions options) {
    Page<PropertyRequest> page = propertyRequestService.sellerSearch(options);

    List<PropertyRequestSellerResp> list = page.getContent().stream().map(
        propertyRequestMapper::propertyRequestSellerResp
    ).toList();

    return new PageResp<>(page, list);
  }

  public PageResp<PropertyRequestCustomerResp> customerSearch(SearchPropertyRequestOptions options) {
    Page<PropertyRequest> page = propertyRequestService.customerSearch(options);


    List<PropertyRequestCustomerResp> list = page.getContent().stream().map(
        propertyRequestMapper::propertyRequestCustomerResp
    ).toList();


    return new PageResp<>(page, list);
  }

  @Transactional
  public void approve(Long propertyRequestId, Long adminId) {
    propertyRequestService.approve(propertyRequestId, adminId);
  }

  @Transactional
  public void reject(Long propertyRequestId) {
    propertyRequestService.reject(propertyRequestId);
  }




}
