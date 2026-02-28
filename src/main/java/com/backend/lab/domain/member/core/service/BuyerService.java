package com.backend.lab.domain.member.core.service;

import com.backend.lab.api.admin.member.buyer.facade.dto.req.AdminBuyerSearchOptions;
import com.backend.lab.common.exception.BusinessException;
import com.backend.lab.common.exception.ErrorCode;
import com.backend.lab.domain.member.core.entity.Member;
import com.backend.lab.domain.member.core.entity.dto.req.BuyerLocalRegisterReq;
import com.backend.lab.domain.member.core.entity.dto.req.BuyerSocialRegisterReq;
import com.backend.lab.domain.member.core.entity.dto.req.BuyerUpdateReq;
import com.backend.lab.domain.member.core.entity.embedded.BuyerProperties;
import com.backend.lab.domain.member.core.entity.embedded.CustomerProperties;
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
public class BuyerService {

  private final MemberType memberType = MemberType.BUYER;

  private final MemberRepository memberRepository;
  private final PasswordEncoder passwordEncoder;

  public Member getById(Long id) {
    return memberRepository.findById(id)
        .orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND, "Buyer"));
  }

  @Transactional
  public Member registerLocal(BuyerLocalRegisterReq req) {
    String email = req.getEmail();
    if (memberRepository.existsByEmail(email)) {
      throw new BusinessException(ErrorCode.EMAIL_DUPLICATED);
    }


    String phoneNumber = req.getPhoneNumber();
    if (memberRepository.existsByBuyerAndPhoneNumber(MemberType.BUYER, phoneNumber)) {
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

            .buyerProperties(BuyerProperties.builder()
                .name(req.getName())
                .companyType(req.getCompanyType())
                .phoneNumber(req.getPhoneNumber())
                .preferSidoCode(req.getPreferSidoCode())
                .preferSigunguCode(req.getPreferSigunguCode())
                .preferBjdongCode(req.getPreferBjdongCode())
                .minPreferPrice(req.getMinPreferPrice())
                .maxPreferPrice(req.getMaxPreferPrice())
                .build())

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
  public Member registerSocial(Long id, BuyerSocialRegisterReq req) {

    String phoneNumber = req.getPhoneNumber();
    if (memberRepository.existsByBuyerAndPhoneNumber(MemberType.BUYER, phoneNumber)) {
      throw new BusinessException(ErrorCode.PHONE_NUMBER_DUPLICATED);
    }

    Member member = this.getById(id);

    member.setType(memberType);

    member.setProfileImage(req.getProfileImage());
    member.setFirstLogin(false);
    member.setDi(req.getDi());

    BuyerProperties buyerProperties = member.getBuyerProperties();
    if (buyerProperties == null) {
      member.setBuyerProperties(new BuyerProperties());
      buyerProperties = member.getBuyerProperties();
    }

    buyerProperties.setName(req.getName());
    buyerProperties.setCompanyType(req.getCompanyType());
    buyerProperties.setPhoneNumber(req.getPhoneNumber());
    buyerProperties.setPreferSidoCode(req.getPreferSidoCode());
    buyerProperties.setPreferSigunguCode(req.getPreferSigunguCode());
    buyerProperties.setPreferBjdongCode(req.getPreferBjdongCode());
    buyerProperties.setMinPreferPrice(req.getMinPreferPrice());
    buyerProperties.setMaxPreferPrice(req.getMaxPreferPrice());


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
  public void update(Long id, BuyerUpdateReq req) {
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

    BuyerProperties buyerProperties = member.getBuyerProperties();
    if (req.getName() != null) {
      buyerProperties.setName(req.getName());
    }
    if (req.getMinPreferPrice() != null) {
      buyerProperties.setMinPreferPrice(req.getMinPreferPrice());
    }
    if (req.getMaxPreferPrice() != null) {
      buyerProperties.setMaxPreferPrice(req.getMaxPreferPrice());
    }
    if (req.getCompanyType() != null) {
      buyerProperties.setCompanyType(req.getCompanyType());
    }
    if (req.getPhoneNumber() != null) {
      buyerProperties.setPhoneNumber(req.getPhoneNumber());
    }
    if (req.getPreferSidoCode() != null) {
      buyerProperties.setPreferSidoCode(req.getPreferSidoCode());
    }
    if (req.getPreferSigunguCode() != null) {
      buyerProperties.setPreferSigunguCode(req.getPreferSigunguCode());
    }
    if (req.getPreferBjdongCode() != null) {
      buyerProperties.setPreferBjdongCode(req.getPreferBjdongCode());
    }

    CustomerProperties customerProperties = member.getCustomerProperties();
    if (req.getName() != null) {
      customerProperties.setName(req.getName());
    }
    if (req.getPhoneNumber() != null) {
      customerProperties.setPhoneNumber(req.getPhoneNumber());
    }
  }

  public Page<Member> adminSearch(AdminBuyerSearchOptions options) {
    return memberRepository.adminSearchBuyer(options);
  }
}
