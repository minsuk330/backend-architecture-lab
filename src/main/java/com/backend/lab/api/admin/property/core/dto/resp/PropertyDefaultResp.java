package com.backend.lab.api.admin.property.core.dto.resp;

import com.backend.lab.domain.property.category.entity.dto.resp.BigCategoryResp;
import com.backend.lab.domain.property.category.entity.dto.resp.SmallCategoryResp;
import com.backend.lab.domain.property.core.entity.vo.PropertyStatus;
import com.backend.lab.domain.uploadFile.entity.dto.resp.UploadFileResp;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PropertyDefaultResp {

  private Long adminId;
  private UploadFileResp thumbnailImage;
  private Boolean isPublic;
  private Boolean isExclusive;
  private Long exclusiveAgentId;
  private Boolean isMyProperty;//매도자의 경우에만

  private String sojeji;
  @Setter
  private Boolean isMywish;//내 관심?
  private Long wishCount;
  private String wishGroupName;
  private PropertyStatus status;
  private String buildingName;
  private BigCategoryResp bigCategory;
  private List<SmallCategoryResp> smallCategories;
  private LocalDateTime confirmedAt;
}
