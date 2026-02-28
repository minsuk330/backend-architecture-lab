package com.backend.lab.common.nice;

import com.backend.lab.common.nice.store.EncryptInformation;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class NiceAuthFormData {
  private String tokenVersionId;
  private String encData;
  private String integrityValue;
  private EncryptInformation encryptInformation;
}