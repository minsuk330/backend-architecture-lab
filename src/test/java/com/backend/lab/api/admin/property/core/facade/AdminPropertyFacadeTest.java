package com.backend.lab.api.admin.property.core.facade;

import static com.backend.lab.domain.member.core.entity.vo.CompanyType.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.backend.lab.domain.member.core.entity.Member;
import com.backend.lab.domain.member.core.entity.dto.req.SearchSellerAndCustomerOptions;
import com.backend.lab.domain.member.core.entity.embedded.CustomerProperties;
import com.backend.lab.domain.member.core.entity.embedded.SellerProperties;
import com.backend.lab.domain.member.core.entity.vo.MemberType;
import com.backend.lab.domain.member.core.repository.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;


class AdminPropertyFacadeTest {

  @Autowired
  private MemberRepository memberRepository;

  @Test
  @DisplayName("매도자 검색 - 이름으로 검색")
  void searchSeller_ByName() {
    // Given


    SearchSellerAndCustomerOptions options = SearchSellerAndCustomerOptions.builder()
        .query("이순")
        .build();

    // When
    Page<Member> result = memberRepository.searchSellerAndCustomer(options);

    // Then
    assertThat(result.getContent()).hasSize(1);
    assertThat(result.getContent().get(0).getSellerProperties().getName()).isEqualTo("이순신");
  }

  @Test
  @DisplayName("매도자 검색 - 전화번호로 검색")
  void searchSeller_ByPhoneNumber() {
    // Given


    SearchSellerAndCustomerOptions options = SearchSellerAndCustomerOptions.builder()

        .build();

    // When
    Page<Member> result = memberRepository.searchSellerAndCustomer(options);

    // Then
    assertThat(result.getContent()).hasSize(6);

  }

  @Test
  @DisplayName("매도자 검색 - 회원 타입 필터")
  void searchSeller_ByMemberType() {
    // Given


    SearchSellerAndCustomerOptions options = SearchSellerAndCustomerOptions.builder()
        .memberType(MemberType.SELLER)
        .build();

    // When
    Page<Member> result = memberRepository.searchSellerAndCustomer(options);

    // Then
    assertThat(result.getContent()).hasSize(3);
    assertThat(result.getContent().get(0).getType()).isEqualTo(MemberType.SELLER);
  }

  // 테스트 데이터 생성 헬퍼 메서드들
  private Member createTestSeller(String name, String phoneNumber) {
    SellerProperties sellerProps = SellerProperties.builder()
        .name(name)
        .phoneNumber(phoneNumber)
        .companyType(INDIVIDUAL)
        .build();

    return Member.builder()
        .type(MemberType.SELLER)
        .sellerProperties(sellerProps)
        .build();
  }

  private Member createTestCustomer(String name, String phoneNumber) {
    CustomerProperties customerProps = CustomerProperties.builder()
        .name(name)
        .phoneNumber(phoneNumber)
        .build();

    return Member.builder()
        .type(MemberType.CUSTOMER)
        .customerProperties(customerProps)
        .build();
  }

}