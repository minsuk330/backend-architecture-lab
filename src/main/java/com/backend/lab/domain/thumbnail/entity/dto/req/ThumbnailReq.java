package com.backend.lab.domain.thumbnail.entity.dto.req;

import com.backend.lab.domain.uploadFile.entity.UploadFile;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
public class ThumbnailReq {

  private List<Long> thumbnailIds = new ArrayList<>();

  @Setter
  @JsonIgnore
  private List<UploadFile> thumbnails = new ArrayList<>();
}
