package com.backend.lab.domain.details.entity.dto.req;

import com.backend.lab.domain.uploadFile.entity.UploadFile;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
public class DetailReq {

  private String content;
  private String etc;

  private List<Long> propertyImageIds = new ArrayList<>();

  private List<Long> dataImageIds = new ArrayList<>();

  @Setter
  @JsonIgnore
  private List<UploadFile> propertyImages = new ArrayList<>();
  @Setter
  @JsonIgnore
  private List<UploadFile> dataImages = new ArrayList<>();

}
