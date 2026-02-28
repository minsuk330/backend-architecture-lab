package com.backend.lab.api.common.property.advertisement.controller;

import static com.backend.lab.common.util.AuthUtil.getUserIdWithAnonymous;

import com.backend.lab.api.common.property.advertisement.dto.PropertyAdvResp;
import com.backend.lab.api.common.property.advertisement.facade.PropertyAdvertisementFacade;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequiredArgsConstructor
@RequestMapping("common/property/adv")
@Tag(name = "[공통] 매물 광고")
public class PropertyAdvertisementController {

  private final PropertyAdvertisementFacade propertyAdvertisementFacade;

  @Operation(summary = "매물 공인중개사 광고 조회")
  @GetMapping("/{propertyId}/agents")
  public PropertyAdvResp searchAgentList(
      @PathVariable Long propertyId
  ) {
    // todo : rollback
    System.out.println("LocalDateTime.now() = " + LocalDateTime.now());
    return propertyAdvertisementFacade.getPropertyAgent(propertyId,getUserIdWithAnonymous());
  }


}
