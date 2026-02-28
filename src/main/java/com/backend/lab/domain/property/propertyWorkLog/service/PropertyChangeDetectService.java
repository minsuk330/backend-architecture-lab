package com.backend.lab.domain.property.propertyWorkLog.service;

import com.backend.lab.api.admin.property.core.dto.req.PropertyCreateReq;
import com.backend.lab.api.admin.property.core.dto.req.PropertyUpdateReq;
import com.backend.lab.domain.admin.core.entity.Admin;
import com.backend.lab.domain.admin.core.service.AdminService;
import com.backend.lab.domain.member.core.entity.Member;
import com.backend.lab.domain.member.core.entity.vo.MemberType;
import com.backend.lab.domain.member.core.service.MemberService;
import com.backend.lab.domain.property.core.entity.Property;
import com.backend.lab.domain.property.core.entity.vo.PropertyStatus;
import com.backend.lab.domain.property.propertyWorkLog.entity.vo.LogFieldType;
import com.backend.lab.domain.property.propertyWorkLog.entity.PropertyWorkLogDetail;
import com.backend.lab.domain.propertyMember.entity.PropertyMember;
import com.backend.lab.domain.propertyMember.service.PropertyMemberService;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PropertyChangeDetectService {

  private final PropertyWorkLogService propertyWorkLogService;
  private final PropertyMemberService propertyMemberService;
  private final MemberService memberService;
  private final AdminService adminService;


  @Transactional
  public void PropertyCreateWorkLog(PropertyCreateReq req, Property property, Admin admin,
      String clientIp) {
    List<PropertyWorkLogDetail> details = new ArrayList<>();

    String adminName = admin.getName() + (admin.getJobGrade() != null ? " " + admin.getJobGrade().getName() : "");

    String phoneAndName = null;
    if (req.getMembers() != null && !req.getMembers().isEmpty()) {
      phoneAndName = req.getMembers().stream()
          .map(member -> member.getPhoneNumber() + " " + member.getName())
          .collect(Collectors.joining(", "));
    }

    details.add(createDetail(LogFieldType.ADMIN,null ,adminName));
    details.add(createDetail(LogFieldType.PHONE_NUMBER, null, phoneAndName));
    details.add(createDetail(LogFieldType.PYENG_PRICE, null, req.getPrice().getPyeongPrice().toString()));
    details.add(createDetail(LogFieldType.MM_PRICE,null,req.getPrice().getMmPrice().toString()));
    details.add(createDetail(LogFieldType.STATUS,null,req.getDefaults().getStatus().toString()));


    if (req.getPrice() != null) {
      if (req.getPrice().getRoi() != null) {
        details.add(createDetail(LogFieldType.ROI, null, req.getPrice().getRoi().toString()));
      }
      if (req.getPrice().getDepositPrice() != null) {
        details.add(createDetail(LogFieldType.DEPOSIT_PRICE, null, req.getPrice().getDepositPrice().toString()));
      }
      if (req.getPrice().getMonthPrice() != null) {
        details.add(createDetail(LogFieldType.MONTH_PRICE, null, req.getPrice().getMonthPrice().toString()));
      }
    }

    propertyWorkLogService.createLog(details,admin,property,clientIp);
  }

  @Transactional
  public void PropertyUpdateWorkLog(PropertyUpdateReq req, Property property, Admin admin, String clientIp) {
    List<PropertyWorkLogDetail> details = new ArrayList<>();

    List<String> phoneNumberChanges = detectPhoneNumber(req, admin, property);
    if (phoneNumberChanges != null) {
      details.add(createDetail(LogFieldType.PHONE_NUMBER, phoneNumberChanges.get(0), phoneNumberChanges.get(1)));
    }
    //담당자 변경 확인 있다가 함수로 빼서 공통으로 사용
    List<String> adminChanges = detectAdmin(req, property);
    if (adminChanges !=null) {
      details.add(createDetail(LogFieldType.ADMIN,adminChanges.get(0) , adminChanges.get(1)));
    }
    //평단가
    List<String> pyengPriceChanges = detectPyengPrice(req, property);
    if (pyengPriceChanges != null) {
      details.add(createDetail(LogFieldType.PYENG_PRICE,pyengPriceChanges.get(0) , pyengPriceChanges.get(1)));
    }
    //상태비교
    List<String> statusChanges = detectStatus(req, property);
    if (statusChanges != null) {
      details.add(createDetail(LogFieldType.STATUS,statusChanges.get(0), statusChanges.get(1)));
    }
    //매매가 비교
    List<String> mmPriceChanges = detectMMPrice(req, property);
    if (mmPriceChanges != null) {
      details.add(createDetail(LogFieldType.MM_PRICE,mmPriceChanges.get(0) , mmPriceChanges.get(1)));
    }

    //값이 null 일 수 있는 친구들
    //수익률
    List<String> roiChanges = detectRoi(req, property);
    if (roiChanges != null) {
      details.add(createDetail(LogFieldType.ROI,roiChanges.get(0) , roiChanges.get(1)));
    }
    //보증금
    List<String> depositPriceChanges = detectDepositPrice(req, property);
    if (depositPriceChanges != null) {
      details.add(createDetail(LogFieldType.DEPOSIT_PRICE,depositPriceChanges.get(0) , depositPriceChanges.get(1)));
    }
    //월임대료
    List<String> monthPriceChanges = detectMonthPrice(req, property);
    if (monthPriceChanges != null) {
      details.add(createDetail(LogFieldType.MONTH_PRICE,monthPriceChanges.get(0) , monthPriceChanges.get(1)));
    }

    propertyWorkLogService.updateLog(details,admin,property,clientIp);
  }

  public List<String> detectPhoneNumber(PropertyUpdateReq req, Admin admin, Property property) {

    List<PropertyMember> propertyMember = propertyMemberService.getByProperty(property.getId());

    // 기존 멤버 조회 (DB에서 가져온 현재 저장된 멤버들) - BEFORE 값
    List<Member> existingMembers = propertyMember.stream()
        .map(pm -> {
          try {
            return memberService.getById(pm.getMemberId());
          } catch (Exception e) {
            return null;
          }
        })
        .filter(Objects::nonNull)
        .toList();


    // 기존 멤버의 "전화번호 이름" (BEFORE 값 - 기존 멤버들만)
    List<String> beforePhoneAndNames = existingMembers.stream()
        .map(member -> {
          try {
            if (member.getType() == MemberType.SELLER) {
              if (member.getSellerProperties() != null) {
                return member.getSellerProperties().getPhoneNumber() + " " + member.getSellerProperties().getName();
              }
            } else if (member.getType() == MemberType.CUSTOMER) {
              if (member.getCustomerProperties() != null) {
                return member.getCustomerProperties().getPhoneNumber() + " " + member.getCustomerProperties().getName();
              }
            }
            return "";
          } catch (Exception e) {
            return "";
          }
        })
        .filter(str -> !str.trim().isEmpty())
        .toList();

    // 새로운 요청의 "전화번호 이름" (AFTER 값 - 기존 + 새로운)
    List<String> afterPhoneAndNames = new ArrayList<>();
    if (req.getMembers() != null) {
      afterPhoneAndNames = req.getMembers().stream()
          .filter(Objects::nonNull)
          .filter(member -> member.getPhoneNumber() != null && member.getName() != null)
          .map(member -> member.getPhoneNumber() + " " + member.getName())
          .toList();
    }

    boolean isChanged = !Objects.equals(
        new HashSet<>(beforePhoneAndNames),
        new HashSet<>(afterPhoneAndNames)
    );

    if (!isChanged) {
      return null; // 변경사항 없으면 null 반환
    }

    // BEFORE 값 (기존 멤버들만)
    String beforeValue = beforePhoneAndNames.isEmpty() ? null :
        String.join(", ", beforePhoneAndNames);

    // AFTER 값 (기존 + 새로운 멤버들)
    String afterValue = afterPhoneAndNames.isEmpty() ? null :
        String.join(", ", afterPhoneAndNames);

    // 순서: [BEFORE, AFTER] 순서로 반환
    return Arrays.asList(beforeValue, afterValue);
  }


  public List<String> detectAdmin(PropertyUpdateReq req, Property property) {

    Long newAdmin = req.getDefaults().getAdminId();
    Long beforeAdmin = property.getAdminId();
    if(!Objects.equals(newAdmin, beforeAdmin)) {

      Admin before = adminService.getById(beforeAdmin);
      Admin after = adminService.getById(newAdmin);


      String beforeAdminName = before.getName() + (before.getJobGrade() != null ? " " + before.getJobGrade().getName() : "");
      String afterAdminName = after.getName() + (after.getJobGrade() != null ? " " + after.getJobGrade().getName() : "");

      return Arrays.asList(beforeAdminName, afterAdminName); // [0]=before, [1]=after

    }
    return null;
  }

  public List<String> detectPyengPrice(PropertyUpdateReq req, Property property) {
      if (property.getPrice() == null || property.getPrice().getProperties() == null) {
        return null;
      }
      
      Long beforePrice = property.getPrice().getProperties().getPyeongPrice();
      Long afterPrice = req.getPrice().getPyeongPrice();

      if(afterPrice != null && beforePrice != null) {
        if (!Objects.equals(afterPrice, beforePrice)) {
          return Arrays.asList(beforePrice.toString(), afterPrice.toString());
        }
      }
      else if(afterPrice != null) {
        return Arrays.asList("-", afterPrice.toString());
      }
      else if(beforePrice != null) {
        return Arrays.asList(beforePrice.toString(), "-");
      }
      return null;
  }

  public List<String> detectStatus(PropertyUpdateReq req, Property property) {
    PropertyStatus before = property.getStatus();
    PropertyStatus after = req.getDefaults().getStatus();

    if (!Objects.equals(after, before)) {
      return Arrays.asList(before.toString(), after.toString());
    }
    return null;
  }

  public List<String> detectMMPrice(PropertyUpdateReq req, Property property) {
    if (property.getPrice() == null || property.getPrice().getProperties() == null) {
      return null;
    }
    
    Long before = property.getPrice().getProperties().getMmPrice();
    Long after = req.getPrice().getMmPrice();

    if(after != null && before != null) {
      if (!Objects.equals(after, before)) {
        return Arrays.asList(before.toString(), after.toString());
      }
    }
    else if(after != null) {
      return Arrays.asList("-", after.toString());
    }
    else if(before != null) {
      return Arrays.asList(before.toString(), "-");
    }
    return null;
  }

  public List<String> detectRoi(PropertyUpdateReq req, Property property) {
    if (property.getPrice() == null || property.getPrice().getProperties() == null) {
      return null;
    }
    
    Double before = property.getPrice().getProperties().getRoi();
    Double after = req.getPrice().getRoi();

    if(after != null&& before != null) {
      if (!Objects.equals(after, before)) {
        return Arrays.asList(before.toString(), after.toString());
      }
    }
    else if(after != null) {
      return Arrays.asList("-", after.toString());
    }
    else if(before != null) {
      return Arrays.asList(before.toString(),"-");
    }
    return null;
  }

  public List<String> detectDepositPrice(PropertyUpdateReq req, Property property) {
    if (property.getPrice() == null || property.getPrice().getProperties() == null) {
      return null;
    }
    
    Long before = property.getPrice().getProperties().getDepositPrice();
    Long after = req.getPrice().getDepositPrice();

    if(after != null&& before != null) {
      if (!Objects.equals(after, before)) {
        return Arrays.asList(before.toString(), after.toString());
      }
    }
    else if(after != null) {
      return Arrays.asList("-", after.toString());
    }
    else if(before != null) {
      return Arrays.asList(before.toString(),"-");
    }
    return null;
  }

  public List<String> detectMonthPrice(PropertyUpdateReq req, Property property) {
    if (property.getPrice() == null || property.getPrice().getProperties() == null) {
      return null;
    }
    
    Long before = property.getPrice().getProperties().getMonthPrice();
    Long after = req.getPrice().getMonthPrice();

    if(after != null&& before != null) {
      if (!Objects.equals(after, before)) {
        return Arrays.asList(before.toString(), after.toString());
      }
    }
    else if(after != null) {
      return Arrays.asList("-", after.toString());
    }
    else if(before != null) {
      return Arrays.asList(before.toString(),"-");
    }
    return null;
  }

  private PropertyWorkLogDetail createDetail(LogFieldType logFieldType, String beforeValue, String afterValue) {
    return PropertyWorkLogDetail.builder()
        .logFieldType(logFieldType)
        .beforeValue(beforeValue)
        .afterValue(afterValue)
        .build();
  }
}
