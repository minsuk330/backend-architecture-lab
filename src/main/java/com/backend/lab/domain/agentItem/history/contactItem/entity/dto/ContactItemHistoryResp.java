package com.backend.lab.domain.agentItem.history.contactItem.entity.dto;

import com.backend.lab.domain.member.core.entity.vo.CompanyType;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContactItemHistoryResp {

  private Long id;
  private LocalDateTime createdAt;
  private String agentName;
  private Long propertyId;
  private String buildingName;
  private String address;

  @Builder.Default
  private List<ContactHistoryCustomer> customers = new ArrayList<>();

  private Boolean isUsed;
  private Integer beforeCount;
  private Integer afterCount;

  public ContactItemHistoryResp(ContactItemHistoryResp original) {
    this.id = original.getId();
    this.createdAt = original.getCreatedAt();
    this.agentName = original.getAgentName();
    this.propertyId = original.getPropertyId();
    this.buildingName = original.getBuildingName();
    this.address = original.getAddress();

    this.customers = original.getCustomers();

    this.isUsed = original.getIsUsed();
    this.beforeCount = original.getBeforeCount();
    this.afterCount = original.getAfterCount();
  }

  @Getter
  @Setter
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class ContactHistoryCustomer {
    private String name;
    private String phoneNumber;
    private CompanyType companyType;
  }
}
