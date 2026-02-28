package com.backend.lab.api.common.uploadFile.controller;

import com.backend.lab.api.common.uploadFile.dto.req.FileUploadReq;
import com.backend.lab.api.common.uploadFile.dto.resp.FileUploadResp;
import com.backend.lab.api.common.uploadFile.facade.UploadFileFacade;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/common/upload-file")
@RequiredArgsConstructor
@Tag(name = "[공통] 파일 관련")
public class UploadFileController {

  private final UploadFileFacade uploadFileFacade;

  @PostMapping("/get-upload-url")
  @Operation(summary = "업로드 URL 요청")
  public FileUploadResp getPreSignedUrl(
      @RequestBody FileUploadReq req
  ) {
    return uploadFileFacade.getPreSignedUrl(req);
  }

  @DeleteMapping("/{fileId}")
  @Operation(summary = "삭제")
  public void deleteFile(
      @PathVariable("fileId") Long fileId
  ) {
    uploadFileFacade.delete(fileId);
  }
}
