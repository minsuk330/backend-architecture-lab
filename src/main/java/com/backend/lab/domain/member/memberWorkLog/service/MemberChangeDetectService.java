package com.backend.lab.domain.member.memberWorkLog.service;

import com.backend.lab.domain.admin.core.entity.Admin;
import com.backend.lab.domain.admin.core.service.AdminService;
import com.backend.lab.domain.member.core.entity.Member;
import com.backend.lab.domain.member.core.entity.dto.req.AdminCustomerDTO;
import com.backend.lab.domain.member.core.service.MemberService;
import com.backend.lab.domain.member.memberWorkLog.entity.MemberWorkLogDetail;
import com.backend.lab.domain.member.memberWorkLog.entity.vo.MemberLogFieldType;
import com.backend.lab.domain.member.memberWorkLog.repository.MemberWorkLogRepository;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberChangeDetectService {


  private final MemberWorkLogRepository memberWorkLogRepository;
  private final MemberService memberService;
  private final AdminService adminService;
  private final MemberWorkLogService memberWorkLogService;

  //어드민이 새로 생성시
  @Transactional
  public void memberCreateWorkLog(AdminCustomerDTO req, Admin admin, Member member,String adminIp) {
    List<MemberWorkLogDetail> details = new ArrayList<>();
    details.add(createDetail(MemberLogFieldType.NAME, null, req.getName()));
    details.add(createDetail(MemberLogFieldType.PHONE_NUMBER, null, req.getPhoneNumber()));
    details.add(createDetail(MemberLogFieldType.COMPANY_TYPE, null, req.getCompanyType() != null ? req.getCompanyType().toString() : null));


    if (req.getEmail() != null) {
      details.add(createDetail(MemberLogFieldType.EMAIL, null, req.getEmail()));
    }
    if (req.getHomePhoneNumber() != null) {
      details.add(createDetail(MemberLogFieldType.HOME_NUMBER, null, req.getHomePhoneNumber()));
    }
    if (req.getAdminId() != null) {
      details.add(createDetail(MemberLogFieldType.ADMIN, null, admin.getName()));
    }
    memberWorkLogService.createLog(details,admin,member,adminIp);
  }

  @Transactional
  public void memberUpdateWorkLog(AdminCustomerDTO req, Admin admin, Member member,String adminIp) {
    List<MemberWorkLogDetail> details = new ArrayList<>();

    // 이름 변경 감지
    List<String> nameChange = detectName(req, member);
    if (nameChange != null) {
      details.add(createDetail(MemberLogFieldType.NAME, nameChange.get(0), nameChange.get(1)));
    }

    // 전화번호 변경 감지
    List<String> phoneChange = detectPhone(req, member);
    if (phoneChange != null) {
      details.add(createDetail(MemberLogFieldType.PHONE_NUMBER, phoneChange.get(0), phoneChange.get(1)));
    }

    // 회사타입 변경 감지
    List<String> companyChange = detectCompanyType(req, member);
    if (companyChange != null) {
      details.add(createDetail(MemberLogFieldType.COMPANY_TYPE, companyChange.get(0), companyChange.get(1)));
    }

    // 이메일 변경 감지
    List<String> emailChange = detectEmail(req, member);
    if (emailChange != null) {
      details.add(createDetail(MemberLogFieldType.EMAIL, emailChange.get(0), emailChange.get(1)));
    }

    // 집전화번호 변경 감지
    List<String> homePhoneChange = detectHomePhoneNumber(req, member);
    if (homePhoneChange != null) {
      details.add(createDetail(MemberLogFieldType.HOME_NUMBER, homePhoneChange.get(0), homePhoneChange.get(1)));
    }

    // 담당 관리자 변경 감지
    List<String> adminChange = detectAdmin(req, member);
    if (adminChange != null) {
      details.add(createDetail(MemberLogFieldType.ADMIN, adminChange.get(0), adminChange.get(1)));
    }

    // 변경사항이 있을 때만 로그 생성
    if (!details.isEmpty()) {
      memberWorkLogService.updateLog(details, admin, member, adminIp);
    }
  }

  public List<String> detectName(AdminCustomerDTO req, Member member) {
    String beforeName = member.getCustomerProperties() != null ? 
        member.getCustomerProperties().getName() : null;
    String afterName = req.getName();

    if (!Objects.equals(beforeName, afterName)) {
      return Arrays.asList(beforeName, afterName);
    }
    return null;
  }

  public List<String> detectPhone(AdminCustomerDTO req, Member member) {
    String beforePhone = member.getCustomerProperties() != null ? 
        member.getCustomerProperties().getPhoneNumber() : null;
    String afterPhone = req.getPhoneNumber();

    if (!Objects.equals(beforePhone, afterPhone)) {
      return Arrays.asList(beforePhone, afterPhone);
    }
    return null;
  }

  public List<String> detectCompanyType(AdminCustomerDTO req, Member member) {
    String beforeCompanyType = member.getCustomerProperties() != null && 
                              member.getCustomerProperties().getCompanyType() != null ? 
        member.getCustomerProperties().getCompanyType().toString() : null;
    String afterCompanyType = req.getCompanyType() != null ? 
        req.getCompanyType().toString() : null;

    if (!Objects.equals(beforeCompanyType, afterCompanyType)) {
      return Arrays.asList(beforeCompanyType, afterCompanyType);
    }
    return null;
  }

  public List<String> detectEmail(AdminCustomerDTO req, Member member) {
    String beforeEmail = member != null ? member.getEmail() : null;
    String afterEmail = req != null ? req.getEmail() : null;

    if (!Objects.equals(beforeEmail, afterEmail)) {
      return Arrays.asList(beforeEmail, afterEmail);
    }
    return null;
  }

  public List<String> detectHomePhoneNumber(AdminCustomerDTO req, Member member) {
    String beforeHomePhone = member.getCustomerProperties() != null ? 
        member.getCustomerProperties().getHomePhoneNumber() : null;
    String afterHomePhone = req.getHomePhoneNumber();

    if (!Objects.equals(beforeHomePhone, afterHomePhone)) {
      return Arrays.asList(beforeHomePhone, afterHomePhone);
    }
    return null;
  }

  public List<String> detectAdmin(AdminCustomerDTO req, Member member) {
    // 기존 담당 관리자 ID
    Long beforeAdminId = member.getCustomerProperties() != null ? 
        member.getCustomerProperties().getAdminId() : null;
    Long afterAdminId = req.getAdminId();

    if (!Objects.equals(beforeAdminId, afterAdminId)) {
      String beforeAdminName = null;
      String afterAdminName = null;
      
      try {
        beforeAdminName = beforeAdminId != null ? 
            adminService.getById(beforeAdminId).getName() : null;
      } catch (Exception e) {
        beforeAdminName = "삭제된 관리자";
      }
      
      try {
        afterAdminName = afterAdminId != null ? 
            adminService.getById(afterAdminId).getName() : null;
      } catch (Exception e) {
        afterAdminName = "삭제된 관리자";
      }

      return Arrays.asList(beforeAdminName, afterAdminName);
    }
    return null;
  }

  private MemberWorkLogDetail createDetail(MemberLogFieldType type, String beforeValue, String afterValue) {
    return MemberWorkLogDetail.builder()
        .logFieldType(type)
        .beforeValue(beforeValue)
        .afterValue(afterValue)
        .build();
  }
}
