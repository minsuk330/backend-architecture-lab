package com.backend.lab.domain.print.entity.dto.req;

import com.backend.lab.domain.uploadFile.entity.UploadFile;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PrintUpdateReq {

  private List<Long> firstPageImageIds = new ArrayList<>();

  private List<Long> lastPageImageIds = new ArrayList<>();

  private String lastPageMainTitle;
  private String lastPageMainColor;

  private String lastPageSubTitle;
  private String lastPageSubColor;

  @Setter
  @JsonIgnore
  private List<UploadFile> firstPageImages = new ArrayList<>();
  @Setter
  @JsonIgnore
  private List<UploadFile> lastPageImages = new ArrayList<>();

}
