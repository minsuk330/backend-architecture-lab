package com.backend.lab.api.member.agent.thumbnail.controller;

import static com.backend.lab.common.util.AuthUtil.getUserId;

import com.backend.lab.api.member.agent.thumbnail.facade.AgentThumbnailFacade;
import com.backend.lab.common.auth.annotation.RequireAgentRole;
import com.backend.lab.domain.thumbnail.entity.dto.req.ThumbnailReq;
import com.backend.lab.domain.thumbnail.entity.dto.resp.ThumbnailResp;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequireAgentRole
@RestController
@RequiredArgsConstructor
@RequestMapping("/agent/thumbnail")
@Tag(name = "[회원/공인중개사] 썸네일 설정")
public class AgentThumbnailController {

  private final AgentThumbnailFacade agentThumbnailFacade;

  @GetMapping
  public ThumbnailResp get () {
    return agentThumbnailFacade.get(getUserId());
  }

  @PostMapping("/image")
  public void updateImage(
      @RequestBody ThumbnailReq req
  ) {
    agentThumbnailFacade.imageUpdate(req,getUserId());
  }

}
