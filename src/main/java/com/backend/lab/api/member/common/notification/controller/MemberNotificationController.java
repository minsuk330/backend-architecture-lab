package com.backend.lab.api.member.common.notification.controller;

import static com.backend.lab.common.util.AuthUtil.*;

import com.backend.lab.api.member.common.notification.facade.MemberNotificationFacade;
import com.backend.lab.common.auth.annotation.RequireAgentRole;
import com.backend.lab.domain.notification.entity.dto.resp.NotificationPerDateResp;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequireAgentRole
@RestController
@RequestMapping("/member/notification")
@RequiredArgsConstructor
@Tag(name = "[회원/공용] 알림")
public class MemberNotificationController {

  private final MemberNotificationFacade agentNotificationFacade;

  @Operation(summary = "조회")
  @GetMapping
  public NotificationPerDateResp gets() {
    return agentNotificationFacade.gets(getUserId());
  }

  @Operation(summary = "모두 읽기 처리")
  @PostMapping("/read")
  public void read() {
    agentNotificationFacade.readAll(getUserId());
  }

  @Operation(summary = "단일 읽기 처리")
  @PostMapping("/read/{notificationId}")
  public void readById(
      @PathVariable("notificationId") Long notificationId
  ) {
    agentNotificationFacade.read(notificationId);
  }
}
