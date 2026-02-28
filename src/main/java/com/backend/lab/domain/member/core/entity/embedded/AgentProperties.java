package com.backend.lab.domain.member.core.entity.embedded;

import com.backend.lab.domain.uploadFile.entity.UploadFile;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AgentProperties {

  @Column(name = "agent_name")
  private String name;
  @Column(name = "agent_phone_number")
  private String phoneNumber;

  @Column(name ="agent_business_name")
  private String businessName; // 사업자명

  @JoinColumn(name = "agent_business_registration_file_id")
  @ManyToOne(fetch = FetchType.LAZY)
  private UploadFile businessRegistration; // 사업자 등록증

  @JoinColumn(name = "agent_certification_file_id")
  @ManyToOne(fetch = FetchType.LAZY)
  private UploadFile certification; // 공인중개사 자격증

  @JoinColumn(name = "agent_registration_certification_file_id")
  @ManyToOne(fetch = FetchType.LAZY)
  private UploadFile registrationCertification; // 개설 등록증

  @Column(name ="agent_address")
  private String address; // 주소

  @Column(name = "agent_address_detail")
  private String addressDetail;

  @Column(name = "agent_manager_id")
  private Long adminId;
}
