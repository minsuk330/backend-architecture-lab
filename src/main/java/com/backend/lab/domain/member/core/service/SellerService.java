package com.backend.lab.domain.member.core.service;

import com.backend.lab.api.admin.member.seller.facade.dto.req.AdminSellerSearchOptions;
import com.backend.lab.common.exception.BusinessException;
import com.backend.lab.common.exception.ErrorCode;
import com.backend.lab.domain.member.core.entity.Member;
import com.backend.lab.domain.member.core.entity.dto.req.SellerLocalRegisterReq;
import com.backend.lab.domain.member.core.entity.dto.req.SellerSocialRegisterReq;
import com.backend.lab.domain.member.core.entity.dto.req.SellerUpdateReq;
import com.backend.lab.domain.member.core.entity.embedded.CustomerProperties;
import com.backend.lab.domain.member.core.entity.embedded.SellerProperties;
import com.backend.lab.domain.member.core.entity.vo.MemberType;
import com.backend.lab.domain.member.core.entity.vo.ProviderType;
import com.backend.lab.domain.member.core.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SellerService {

  private final MemberType memberType = MemberType.SELLER;

  private final MemberRepository memberRepository;
  private final PasswordEncoder passwordEncoder;

  public Member getById(Long id) {
    return memberRepository.findById(id)
        .orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND, "Seller"));
  }

  @Transactional
  public Member registerLocal(SellerLocalRegisterReq req) {

    String email = req.getEmail();
    if (memberRepository.existsByEmail(email)) {
      throw new BusinessException(ErrorCode.EMAIL_DUPLICATED);
    }

    String phoneNumber = req.getPhoneNumber();
    if (memberRepository.existsBySellerAndPhoneNumber(MemberType.BUYER, phoneNumber)) {
      throw new BusinessException(ErrorCode.PHONE_NUMBER_DUPLICATED);
    }

    String encodedPassword = passwordEncoder.encode(req.getPassword());

    return memberRepository.save(
        Member.builder()
            .email(email)
            .password(encodedPassword)
            .profileImage(req.getProfileImage())
            .provider(ProviderType.LOCAL)
            .di(req.getDi())

            .isFirstLogin(false)
            .isBlocked(false)

            .type(memberType)

            .sellerProperties(
                SellerProperties.builder()
                    .name(req.getName())
                    .companyType(req.getCompanyType())
                    .phoneNumber(req.getPhoneNumber())
                    .build()
            )

            .customerProperties(CustomerProperties.builder()
                .name(req.getName())
                .phoneNumber(req.getPhoneNumber())
                .homePhoneNumber(null)
                .etcPhoneNumber(null)
                .funnel(null)
                .note(null)
                .build())

            .build()
    );
  }

  @Transactional
  public Member registerSocial(Long id, SellerSocialRegisterReq req) {

    String phoneNumber = req.getPhoneNumber();
    if (memberRepository.existsBySellerAndPhoneNumber(MemberType.BUYER, phoneNumber)) {
      throw new BusinessException(ErrorCode.PHONE_NUMBER_DUPLICATED);
    }


    Member member = this.getById(id);

    member.setType(memberType);

    member.setProfileImage(req.getProfileImage());
    member.setFirstLogin(false);
    member.setDi(req.getDi());

    SellerProperties sellerProperties = member.getSellerProperties();
    if (sellerProperties == null) {
      member.setSellerProperties(new SellerProperties());
      sellerProperties = member.getSellerProperties();
    }
    sellerProperties.setName(req.getName());
    sellerProperties.setCompanyType(req.getCompanyType());
    sellerProperties.setPhoneNumber(req.getPhoneNumber());

    CustomerProperties customerProperties = member.getCustomerProperties();
    if (customerProperties == null) {
      member.setCustomerProperties(new CustomerProperties());
      customerProperties = member.getCustomerProperties();
    }
    customerProperties.setName(req.getName());
    customerProperties.setPhoneNumber(req.getPhoneNumber());

    return memberRepository.save(member);
  }

  @Transactional
  public void update(Long id, SellerUpdateReq req) {
    Member member = this.getById(id);

    if (req.getEmail() != null) {
      if (!member.getEmail().equals(req.getEmail()) &&
          memberRepository.existsByEmail(req.getEmail())
      ) {
        throw new BusinessException(ErrorCode.EMAIL_DUPLICATED);
      }
      member.setEmail(req.getEmail());
    }
    if (req.getNewPassword() != null) {
      member.setPassword(passwordEncoder.encode(req.getNewPassword()));
    }
    if (req.getProfileImage() != null) {
      member.setProfileImage(req.getProfileImage());
    }

    SellerProperties sellerProperties = member.getSellerProperties();
    if (req.getName() != null) {
      sellerProperties.setName(req.getName());
    }
    if (req.getCompanyType() != null) {
      sellerProperties.setCompanyType(req.getCompanyType());
    }
    if (req.getPhoneNumber() != null) {
      sellerProperties.setPhoneNumber(req.getPhoneNumber());
    }

    CustomerProperties customerProperties = member.getCustomerProperties();
    if (req.getName() != null) {
      customerProperties.setName(req.getName());
    }
    if (req.getPhoneNumber() != null) {
      customerProperties.setPhoneNumber(req.getPhoneNumber());
    }
  }

  public Page<Member> adminSearch(AdminSellerSearchOptions options) {
    return memberRepository.adminSearchSeller(options);
  }

}
