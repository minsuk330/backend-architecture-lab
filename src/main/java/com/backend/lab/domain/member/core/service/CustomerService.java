package com.backend.lab.domain.member.core.service;

import com.backend.lab.api.admin.property.core.dto.req.PropertyCustomerCreateReq;
import com.backend.lab.common.exception.BusinessException;
import com.backend.lab.common.exception.ErrorCode;
import com.backend.lab.domain.member.core.entity.Member;
import com.backend.lab.domain.member.core.entity.dto.req.AdminCustomerDTO;
import com.backend.lab.domain.member.core.entity.dto.req.AdminCustomerSearchOptions;
import com.backend.lab.domain.member.core.entity.dto.req.SearchSellerAndCustomerOptions;
import com.backend.lab.domain.member.core.entity.embedded.CustomerProperties;
import com.backend.lab.domain.member.core.entity.vo.MemberType;
import com.backend.lab.domain.member.core.entity.vo.ProviderType;
import com.backend.lab.domain.member.core.repository.MemberRepository;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CustomerService {

  private final MemberType memberType = MemberType.CUSTOMER;

  private final MemberRepository memberRepository;

  public Member getById(Long id) {
    return memberRepository.findById(id)
        .orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND, "Customer"));
  }

  @Transactional
  public Member create(AdminCustomerDTO req) {
    return memberRepository.save(
        Member.builder()
            .email(req.getEmail())
            .password(null)
            .profileImage(null)
            .provider(ProviderType.LOCAL)

            .isFirstLogin(false)
            .isBlocked(false)

            .type(MemberType.CUSTOMER)

            .customerProperties(
                CustomerProperties.builder()
                    .name(req.getName())
                    .gender(req.getGender())
                    .birth(req.getBirth())
                    .etc(req.getEtc())
                    .address(req.getAddress())
                    .adminId(req.getAdminId())
                    .funnel(req.getFunnel())
                    .phoneNumber(req.getPhoneNumber())
                    .homePhoneNumber(req.getHomePhoneNumber())
                    .note(req.getNote())
                    .companyType(req.getCompanyType())
                    .build()
            )
            .build()
    );
  }

  @Transactional
  public Member update(Long id, AdminCustomerDTO req) {
    Member member = this.getById(id);

    member.setEmail(req.getEmail());
    CustomerProperties customerProperties = member.getCustomerProperties();
    customerProperties.setName(req.getName());
    customerProperties.setGender(req.getGender());
    customerProperties.setBirth(req.getBirth());
    customerProperties.setAddress(req.getAddress());
    customerProperties.setAdminId(req.getAdminId());
    customerProperties.setFunnel(req.getFunnel());
    customerProperties.setPhoneNumber(req.getPhoneNumber());
    customerProperties.setHomePhoneNumber(req.getHomePhoneNumber());
    customerProperties.setNote(req.getNote());
    customerProperties.setCompanyType(req.getCompanyType());
    customerProperties.setEtc(req.getEtc());

    member.setCustomerProperties(customerProperties);
    return memberRepository.save(member);
  }


  @Transactional
  public void updateCustomer(PropertyCustomerCreateReq req, Long adminId) {
    Member member = this.getById(req.getMemberId());
    CustomerProperties customerProperties = member.getCustomerProperties();
    customerProperties.setName(req.getName());
    customerProperties.setPhoneNumber(req.getPhoneNumber());
    customerProperties.setHomePhoneNumber(req.getHomePhoneNumber());
    customerProperties.setCompanyType(req.getCompanyType());
    customerProperties.setNote(req.getNote());
    customerProperties.setFunnel(req.getFunnel());
    customerProperties.setEtc(req.getEtc());
    member.setCustomerProperties(customerProperties);
    memberRepository.save(member);
  }
  public Member createCustomer(MemberInfo req, Long adminId) {
    return memberRepository.save(
        Member.builder()
            .email(null)
            .password(null)
            .profileImage(null)
            .provider(ProviderType.LOCAL)

            .isFirstLogin(false)
            .isBlocked(false)

            .type(MemberType.CUSTOMER)

            .customerProperties(
                CustomerProperties.builder()
                    .name(req.getName())
                    .phoneNumber(req.getPhone())
                    .homePhoneNumber(req.getHomePhone())
                    .note(req.getMemo())
                    .etcPhoneNumber(null)
                    .adminId(adminId)
                    .build()
            )
            .build()
    );
  }

  public Page<Member> search(AdminCustomerSearchOptions options) {
    return memberRepository.searchCustomer(options);
  }

  public Page<Member> search(SearchSellerAndCustomerOptions options) {
    return memberRepository.searchSellerAndCustomer(options);
  }

  @Transactional
  public void updateSellerAndCustomer(PropertyCustomerCreateReq req,Long memberId) {
    Member member = getById(memberId); //
    CustomerProperties customerProperties = member.getCustomerProperties();
    if (member.getType()==MemberType.CUSTOMER) {

      customerProperties.setName(req.getName());
      customerProperties.setPhoneNumber(req.getPhoneNumber());
      customerProperties.setCompanyType(req.getCompanyType());

      customerProperties.setNote(req.getNote());
      customerProperties.setFunnel(req.getFunnel());
      customerProperties.setHomePhoneNumber(req.getHomePhoneNumber());
      customerProperties.setEtc(req.getEtc());
    }
    else {
      customerProperties.setNote(req.getNote());
      customerProperties.setFunnel(req.getFunnel());
      customerProperties.setHomePhoneNumber(req.getHomePhoneNumber());
      customerProperties.setEtc(req.getEtc());
    }

    memberRepository.save(member);
  }

  public Member createCustomer(PropertyCustomerCreateReq req, Long adminId) {
    return memberRepository.save(
        Member.builder()
            .email(null)
            .password(null)
            .profileImage(null)
            .provider(ProviderType.LOCAL)

            .isFirstLogin(false)
            .isBlocked(false)

            .type(MemberType.CUSTOMER)

            .customerProperties(
                CustomerProperties.builder()
                    .name(req.getName())
                    .phoneNumber(req.getPhoneNumber())
                    .homePhoneNumber(req.getHomePhoneNumber())
                    .note(req.getNote())
                    .etc(req.getEtc())
                    .funnel(req.getFunnel())
                    .etcPhoneNumber(null)
                    .adminId(adminId)
                    .companyType(req.getCompanyType())
                    .build()
            )
            .build()
    );
  }

  @Transactional
  public void delete(Member member) {
    memberRepository.delete(member);
  }

  @Data
  @Builder
  public static class MemberInfo {
    private String name;
    private String type;
    private String phone;
    private String homePhone;
    private String funnel;
    private String memo;

    public boolean hasValidData() {
      return name != null && !name.trim().isEmpty();
    }
  }
}
