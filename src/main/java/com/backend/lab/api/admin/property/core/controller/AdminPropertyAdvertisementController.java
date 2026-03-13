package com.backend.lab.api.admin.property.core.controller;

import com.backend.lab.api.admin.property.core.dto.req.PropertyAdvertisementCreateReq;
import com.backend.lab.api.admin.property.core.dto.req.PropertyAdvertisementRemoveReq;
import com.backend.lab.application.port.in.property.advertisement.AssignAdvertisementUseCase;
import com.backend.lab.application.port.in.property.advertisement.RemoveAdvertisementUseCase;
import com.backend.lab.common.auth.annotation.RequireAdminRole;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/property")
@Tag(name = "[관리자] 매물 광고")
public class AdminPropertyAdvertisementController {

  private final AssignAdvertisementUseCase assignAdvertisementUseCase;
  private final RemoveAdvertisementUseCase removeAdvertisementUseCase;


  @RequireAdminRole
  @Operation(summary = "매물에 공인중개사 광고 추가")
  @PostMapping("/advertisement")
  public void assignPropertyAdvertisement(
      @Valid @RequestBody PropertyAdvertisementCreateReq req
  ) {
    assignAdvertisementUseCase.assign(
        req.getPropertyId(),
        req.getAgentId(),
        req.getStartDate(),
        req.getEndDate()
    );
  }

  @RequireAdminRole
  @Operation(summary = "매물에서 공인중개사 광고 삭제")
  @DeleteMapping("/advertisement")
  public void removePropertyAdvertisement(
      @Valid @RequestBody PropertyAdvertisementRemoveReq req
  ) {
    removeAdvertisementUseCase.remove(
        req.getPropertyId(),
        req.getAgentId()
    );
  }


}
