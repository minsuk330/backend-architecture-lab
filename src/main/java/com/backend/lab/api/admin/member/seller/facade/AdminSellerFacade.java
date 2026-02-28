package com.backend.lab.api.admin.member.seller.facade;

import com.backend.lab.api.admin.PropertyRequest.dto.resp.PropertyRequestAgentResp;
import com.backend.lab.api.admin.PropertyRequest.mapper.PropertyRequestMapper;
import com.backend.lab.api.admin.member.seller.facade.dto.req.AdminSellerSearchOptions;
import com.backend.lab.api.admin.member.seller.facade.dto.resp.AdminSellerFullResp;
import com.backend.lab.api.admin.member.seller.facade.dto.resp.AdminSellerSearchResp;
import com.backend.lab.api.member.global.facade.MemberGlobalFacade;
import com.backend.lab.common.entity.dto.resp.PageResp;
import com.backend.lab.domain.admin.core.entity.Admin;
import com.backend.lab.domain.admin.core.entity.dto.resp.AdminResp;
import com.backend.lab.domain.admin.core.service.AdminService;
import com.backend.lab.domain.member.core.entity.Member;
import com.backend.lab.domain.member.core.entity.dto.resp.SellerPropsResp;
import com.backend.lab.domain.member.core.entity.embedded.SellerProperties;
import com.backend.lab.domain.member.core.service.MemberService;
import com.backend.lab.domain.member.core.service.SellerService;
import com.backend.lab.domain.memberNote.entity.dto.resp.MemberNoteResp;
import com.backend.lab.domain.memberNote.service.MemberNoteService;
import com.backend.lab.domain.propertyRequest.service.PropertyRequestService;
import com.backend.lab.domain.uploadFile.service.UploadFileService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminSellerFacade {

  private final SellerService sellerService;
  private final AdminService adminService;
  private final PropertyRequestService propertyRequestService;
  private final UploadFileService uploadFileService;
  private final MemberNoteService memberNoteService;

  private final MemberGlobalFacade memberGlobalFacade;
  private final PropertyRequestMapper propertyRequestMapper;
  private final MemberService memberService;

  public PageResp<AdminSellerSearchResp> search(AdminSellerSearchOptions options) {
    Page<Member> page = sellerService.adminSearch(options);
    List<AdminSellerSearchResp> data = page.getContent().stream()
        .map(this::searchResp)
        .toList();
    return new PageResp<>(page, data);
  }

  public AdminSellerFullResp getById(Long id) {
    Member member = sellerService.getById(id);
    return this.fullResp(member);
  }

  public AdminSellerSearchResp searchResp(Member member) {

    if (member.getDeletedAt() != null) {
      return AdminSellerSearchResp.builder()
          .id(member.getId())
          .createdAt(member.getCreatedAt())
          .deletedAt(member.getDeletedAt())
          .isActive(false)
          .build();
    }

    SellerProperties sellerProperties = member.getSellerProperties();

    return AdminSellerSearchResp.builder()
        .id(member.getId())
        .email(member.getEmail())

        .name(sellerProperties != null ? sellerProperties.getName() : null)
        .companyType(sellerProperties != null ? sellerProperties.getCompanyType() : null)
        .phoneNumber(sellerProperties != null ? sellerProperties.getPhoneNumber() : null)

        .createdAt(member.getCreatedAt())
        .deletedAt(member.getDeletedAt())

        .isActive(member.getDeletedAt() == null)
        .build();
  }

  public AdminSellerFullResp fullResp(Member member) {

    List<PropertyRequestAgentResp> properties = propertyRequestService.getsByMember(member).stream()
        .map(propertyRequestMapper::propertyRequestAgentResp)
        .toList();

    List<MemberNoteResp> notes = memberNoteService.getsByMember(member).stream()
        .map(m -> {
          Admin admin = adminService.getById(m.getAdminId());
          AdminResp adminResp = adminService.adminResp(admin,
              uploadFileService.uploadFileResp(admin.getProfileImage()));
          return memberNoteService.memberNoteResp(m, adminResp);
        })
        .toList();

    SellerPropsResp sellerPropsResp = memberGlobalFacade.sellerPropsResp(member);

    return AdminSellerFullResp.builder()
        .id(member.getId())
        .createdAt(member.getCreatedAt())

        .memberResp(memberService.memberResp(
            member,
            uploadFileService.uploadFileResp(member.getProfileImage())
        ))

        .sellerPropsResp(sellerPropsResp)
        .notes(notes)
        .propertyRequests(properties)
        .build();
  }
}
