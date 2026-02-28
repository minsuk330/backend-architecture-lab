package com.backend.lab.api.admin.property.core.dto.req;

import com.backend.lab.domain.property.core.entity.vo.PropertyStatus;
import com.backend.lab.domain.uploadFile.entity.UploadFile;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
public class PropertyDefaultReq {

  private Long thumbnailId;
  @Setter
  @JsonIgnore
  private UploadFile thumbnailImageUrl;
  //담당자, 직급도 표시, 상태
  private Long adminId;
  private String buildingName;
  private Boolean isPublic;
  private Long exclusiveId;
  private PropertyStatus status;
  private Long bigCategoryId;
  private List<Long> smallCategoryIds;
}
