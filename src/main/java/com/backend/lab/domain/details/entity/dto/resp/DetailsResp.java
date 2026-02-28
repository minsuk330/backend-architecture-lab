package com.backend.lab.domain.details.entity.dto.resp;

import com.backend.lab.domain.uploadFile.entity.dto.resp.UploadFileResp;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DetailsResp {

  private String content;

  private String etc;

  private List<UploadFileResp> propertyImages = new ArrayList<>();
  private List<UploadFileResp> dataImages = new ArrayList<>();

}
 