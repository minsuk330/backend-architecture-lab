package com.backend.lab.api.admin.customer.facade;

import com.backend.lab.api.member.global.dto.resp.MemberGlobalResp;
import com.backend.lab.api.member.global.facade.MemberGlobalFacade;
import com.backend.lab.common.entity.dto.resp.PageResp;
import com.backend.lab.domain.member.core.entity.DeletedMember;
import com.backend.lab.domain.member.core.entity.dto.req.AdminCustomerSearchOptions;
import com.backend.lab.domain.member.core.service.DeletedMemberService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminDeletedCustomerFacade {

  private final DeletedMemberService deletedMemberService;
  private final MemberGlobalFacade memberGlobalFacade;

  public PageResp<MemberGlobalResp> search(AdminCustomerSearchOptions options) {
    Page<DeletedMember> page = deletedMemberService.search(options);
    List<MemberGlobalResp> data = page.getContent().stream()
        .map(memberGlobalFacade::memberGlobalResp)
        .toList();
    return new PageResp<>(page, data);
  }

  @Transactional
  public void restore(Long id) {
    deletedMemberService.restore(id);
  }

  @Transactional
  public void remove(Long id) {
    deletedMemberService.remove(id);
  }
}
