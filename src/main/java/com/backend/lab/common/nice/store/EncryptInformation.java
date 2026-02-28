package com.backend.lab.common.nice.store;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EncryptInformation {
  private String tokenVersionId;
  private String requestNo;
  private String key;
  private String iv;
}
