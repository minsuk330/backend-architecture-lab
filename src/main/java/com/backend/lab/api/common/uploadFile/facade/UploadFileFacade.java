package com.backend.lab.api.common.uploadFile.facade;

import com.backend.lab.api.common.uploadFile.dto.req.FileUploadReq;
import com.backend.lab.api.common.uploadFile.dto.resp.FileUploadResp;
import com.backend.lab.common.s3.dto.PreSignedUrlResp;
import com.backend.lab.common.s3.S3Service;
import com.backend.lab.domain.uploadFile.entity.UploadFile;
import com.backend.lab.domain.uploadFile.entity.dto.req.UploadFileReq;
import com.backend.lab.domain.uploadFile.service.UploadFileService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UploadFileFacade {

  private final UploadFileService uploadFileService;
  private final S3Service s3Service;

  @Transactional
  public FileUploadResp getPreSignedUrl(FileUploadReq req) {
    PreSignedUrlResp resp = s3Service.getPreSignedUrl(req);
    UploadFile uploadFile = uploadFileService.save(UploadFileReq.builder()
        .id(null)
        .fileUrl(resp.getAccessUrl())
        .fileName(resp.getFileName())
        .build());

    return FileUploadResp.builder()
        .file(uploadFileService.uploadFileResp(uploadFile))
        .uploadUrl(resp.getUploadUrl())
        .contentDisposition(resp.getContentDisposition())
        .build();
  }

  @Transactional
  public void delete(Long fileId) {
    UploadFile uploadFile = uploadFileService.getById(fileId);
    s3Service.deleteByAccessUrl(uploadFile.getFileUrl());
    uploadFileService.delete(fileId);
  }
}
