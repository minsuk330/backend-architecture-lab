package com.backend.lab.api.member.common.property.core.controller;

import static com.backend.lab.common.util.AuthUtil.getUserIdWithAnonymous;

import com.backend.lab.api.admin.property.core.dto.req.PropertyMapListReq;
import com.backend.lab.api.admin.property.core.dto.resp.PropertyResp;
import com.backend.lab.api.common.property.advertisement.dto.PropertyAdvResp;
import com.backend.lab.api.common.property.advertisement.facade.PropertyAdvertisementFacade;
import com.backend.lab.api.member.common.property.core.facade.MemberPropertyFacade;
import com.backend.lab.api.member.common.property.wishlist.dto.PropertyListResp;
import com.backend.lab.common.entity.dto.resp.ListResp;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/member/property")
@RequiredArgsConstructor
@Tag(name="[회원/공용] 매물")
public class MemberPropertyController {

  private final MemberPropertyFacade memberPropertyFacade;
  private final PropertyAdvertisementFacade propertyAdvertisementFacade;

  @Operation(summary = "홈 매물")
  @GetMapping("/home")
  public ListResp<PropertyListResp> gets(
  ) {
    return memberPropertyFacade.gets(getUserIdWithAnonymous());
  }

  @Operation(summary = "지도 매물조회")
  @GetMapping
  public ListResp<PropertyListResp> gets(
      @RequestParam(required = false) List<Long> bigCategoryIds,
      @RequestParam(required = false) List<Long> smallCategoryIds,
      @ModelAttribute PropertyMapListReq req
  ) {
    return memberPropertyFacade.getsByMap(req,getUserIdWithAnonymous(),bigCategoryIds,smallCategoryIds);
  }

  @Operation(summary = "회원매물 상세조회")
  @GetMapping("{propertyId}")
  public PropertyResp get(
      @PathVariable("propertyId") Long propertyId
  ) {
    return memberPropertyFacade.get(propertyId, getUserIdWithAnonymous());
  }

  @Operation(summary = "매물 공인중개사 광고 조회")
  @GetMapping("/{propertyId}/agents")
  public PropertyAdvResp searchAgentList(
      @PathVariable Long propertyId
  ) {
    return propertyAdvertisementFacade.getPropertyAgent(propertyId,getUserIdWithAnonymous());
  }

}
