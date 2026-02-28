package com.backend.lab.api.admin.member.buyer.facade;

import com.backend.lab.api.admin.member.buyer.facade.dto.req.AdminBuyerSearchOptions;
import com.backend.lab.api.admin.member.buyer.facade.dto.resp.AdminBuyerFullResp;
import com.backend.lab.api.admin.member.buyer.facade.dto.resp.AdminBuyerSearchResp;
import com.backend.lab.api.member.global.facade.MemberGlobalFacade;
import com.backend.lab.common.entity.dto.resp.PageResp;
import com.backend.lab.domain.admin.core.entity.Admin;
import com.backend.lab.domain.admin.core.entity.dto.resp.AdminResp;
import com.backend.lab.domain.admin.core.service.AdminService;
import com.backend.lab.domain.member.core.entity.Member;
import com.backend.lab.domain.member.core.entity.dto.resp.BuyerPropsResp;
import com.backend.lab.domain.member.core.entity.embedded.BuyerProperties;
import com.backend.lab.domain.member.core.service.BuyerService;
import com.backend.lab.domain.member.core.service.MemberService;
import com.backend.lab.domain.memberNote.entity.dto.resp.MemberNoteResp;
import com.backend.lab.domain.memberNote.service.MemberNoteService;
import com.backend.lab.domain.uploadFile.service.UploadFileService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminBuyerFacade {

  private final BuyerService buyerService;
  private final AdminService adminService;
  private final UploadFileService uploadFileService;
  private final MemberNoteService memberNoteService;
  private final MemberGlobalFacade memberGlobalFacade;
  private final MemberService memberService;

  public PageResp<AdminBuyerSearchResp> search(AdminBuyerSearchOptions options) {
    Page<Member> page = buyerService.adminSearch(options);
    List<AdminBuyerSearchResp> data = page.getContent().stream()
        .map(this::searchResp)
        .toList();
    return new PageResp<>(page, data);
  }

  public AdminBuyerFullResp getById(Long id) {
    Member member = buyerService.getById(id);
    return this.fullResp(member);
  }

  public AdminBuyerSearchResp searchResp(Member member) {

    if (member.getDeletedAt() != null) {
      return AdminBuyerSearchResp.builder()
          .id(member.getId())
          .createdAt(member.getCreatedAt())
          .deletedAt(member.getDeletedAt())
          .isActive(false)
          .build();
    }

    BuyerProperties buyerProperties = member.getBuyerProperties();

    return AdminBuyerSearchResp.builder()
        .id(member.getId())
        .email(member.getEmail())
        .name(buyerProperties != null ? buyerProperties.getName() : null)
        .companyType(buyerProperties != null ? buyerProperties.getCompanyType() : null)
        .phoneNumber(buyerProperties != null ? buyerProperties.getPhoneNumber() : null)

        .createdAt(member.getCreatedAt())
        .deletedAt(member.getDeletedAt())

        .isActive(member.getDeletedAt() == null)
        .build();
  }

  public AdminBuyerFullResp fullResp(Member member) {

    List<MemberNoteResp> notes = memberNoteService.getsByMember(member).stream()
        .map(m -> {
          Admin admin = adminService.getById(m.getAdminId());
          AdminResp adminResp = adminService.adminResp(admin,
              uploadFileService.uploadFileResp(admin.getProfileImage()));
          return memberNoteService.memberNoteResp(m, adminResp);
        })
        .toList();

    BuyerPropsResp buyerPropsResp = memberGlobalFacade.buyerPropsResp(member);

    return AdminBuyerFullResp.builder()
        .id(member.getId())
        .createdAt(member.getCreatedAt())

        .memberResp(memberService.memberResp(
            member,
            uploadFileService.uploadFileResp(member.getProfileImage())
        ))

        .buyerPropsResp(buyerPropsResp)
        .notes(notes)
        .build();
  }
}
