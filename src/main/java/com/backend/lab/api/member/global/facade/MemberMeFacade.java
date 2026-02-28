package com.backend.lab.api.member.global.facade;

import com.backend.lab.api.member.global.dto.resp.MemberGlobalResp;
import com.backend.lab.domain.member.core.entity.Member;
import com.backend.lab.domain.member.core.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberMeFacade {

  private final MemberService memberService;
  private final MemberGlobalFacade memberGlobalFacade;

  @Transactional(readOnly = true)
  public MemberGlobalResp me(Long memberId) {
    Member member = memberService.getById(memberId);
    return memberGlobalFacade.memberGlobalResp(member);
  }
}
