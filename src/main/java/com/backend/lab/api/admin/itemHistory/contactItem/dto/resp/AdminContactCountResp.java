package com.backend.lab.api.admin.itemHistory.contactItem.dto.resp;

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
public class AdminContactCountResp {

  private Long id;
  private String businessName;
  private Long count;
}
