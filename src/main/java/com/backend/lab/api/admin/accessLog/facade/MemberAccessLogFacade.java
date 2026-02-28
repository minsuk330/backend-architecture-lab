package com.backend.lab.api.admin.accessLog.facade;

import com.backend.lab.api.admin.accessLog.dto.resp.MemberAccessLogApiResp;
import com.backend.lab.api.member.global.facade.MemberGlobalFacade;
import com.backend.lab.common.entity.dto.resp.PageResp;
import com.backend.lab.domain.member.audit.accessLog.entity.MemberAccessLog;
import com.backend.lab.domain.member.audit.accessLog.entity.dto.req.GetMemberAccessLogOptions;
import com.backend.lab.domain.member.audit.accessLog.service.MemberAccessLogService;
import com.backend.lab.domain.member.core.service.MemberService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberAccessLogFacade {

  private final MemberAccessLogService memberAccessLogService;
  private final MemberGlobalFacade memberCommonFacade;
  private final MemberService memberService;

  @Transactional(readOnly = true)
  public PageResp<MemberAccessLogApiResp> search(GetMemberAccessLogOptions options) {

    Page<MemberAccessLog> page = memberAccessLogService.search(options);

    List<MemberAccessLogApiResp> data = page.getContent().stream()
        .map(accessLog -> {
          Long memberId = accessLog.getMemberId();
          return MemberAccessLogApiResp.builder()
              .member(memberCommonFacade.memberGlobalResp(memberService.getById(memberId)))
              .log(memberAccessLogService.memberAccessLogResp(accessLog))
              .build();
        })
        .toList();
    return new PageResp<>(page, data);
  }
}
