package com.backend.lab.api.member.nonMember.property.controller;

import com.backend.lab.api.admin.PropertyRequest.dto.req.PropertyRequestNonMemberReq;
import com.backend.lab.api.member.nonMember.property.facade.NonMemberPropertyFacade;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/nonMember/property")
@Tag(name = "[비회원] 매물")
public class NonMemberPropertyController {

  private final NonMemberPropertyFacade nonMemberPropertyFacade;

  @Operation(summary = "비회원 매물 의뢰")
  @PostMapping("/request")
  public void createReq(
      @RequestBody PropertyRequestNonMemberReq req
  ) {
    nonMemberPropertyFacade.createReq(req);
  }


}
