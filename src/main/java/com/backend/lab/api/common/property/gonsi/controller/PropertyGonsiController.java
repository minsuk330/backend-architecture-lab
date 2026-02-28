package com.backend.lab.api.common.property.gonsi.controller;

import com.backend.lab.api.admin.property.info.dto.resp.PropertyGonsiInfo;
import com.backend.lab.api.admin.property.info.dto.resp.PropertyGonsiInfoResp;
import com.backend.lab.api.common.property.gonsi.facade.PropertyGonsiFacade;
import com.backend.lab.common.entity.dto.req.PageOptions;
import com.backend.lab.common.entity.dto.resp.PageResp;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/common/property/gonsi")
@Tag(name = "[공통] 공시지가")
public class PropertyGonsiController {

  private final PropertyGonsiFacade propertyGonsiFacade;

  @Operation(summary = "전체 공시지가 리스트")
  @GetMapping("/{pnu}/chart")
  public PropertyGonsiInfoResp getPropertyGonsiInfo(
      @PathVariable("pnu") String pnu
  ) {
    return propertyGonsiFacade.getGonsi(pnu);
  }

  @Operation(summary = "공시지가 페이징")
  @GetMapping("/{pnu}")
  public PageResp<PropertyGonsiInfo> getPropertygonsiInfoPage(
      @PathVariable("pnu") String pnu,
      @ParameterObject @ModelAttribute PageOptions options
  ) {
    return propertyGonsiFacade.getGinsiPage(pnu,options);
  }

}
