package com.backend.lab.domain.uploadFile.service;

import com.backend.lab.common.exception.BusinessException;
import com.backend.lab.common.exception.ErrorCode;
import com.backend.lab.domain.uploadFile.entity.UploadFile;
import com.backend.lab.domain.uploadFile.entity.dto.req.UploadFileReq;
import com.backend.lab.domain.uploadFile.entity.dto.resp.UploadFileResp;
import com.backend.lab.domain.uploadFile.repository.UploadFileRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UploadFileService {

  private final UploadFileRepository uploadFileRepository;


  public List<UploadFile> gets(List<Long> ids) {
    return uploadFileRepository.findAllById(ids);
  }

  public UploadFile getById(Long id) {
    return uploadFileRepository.findById(id)
        .orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND, "UploadFile"));
  }

  public List<UploadFile> getByIds(List<Long> fileIds) {
    if (fileIds == null || fileIds.isEmpty()) {
      return new ArrayList<>();
    }

    return uploadFileRepository.findAllById(fileIds);
  }

  public Optional<UploadFile> getOptional(Long id) {
    return uploadFileRepository.findById(id);
  }

  @Transactional
  public UploadFile save(UploadFileReq req) {

    Long id = req.getId();
    if (id != null) {
      Optional<UploadFile> optionalOriginal = getOptional(id);
      if (optionalOriginal.isPresent()) {
        UploadFile uploadFile = optionalOriginal.get();
        uploadFile.setFileName(req.getFileName());
        uploadFile.setFileUrl(req.getFileUrl());
        return uploadFile;
      }
    }

    return uploadFileRepository.save(
        UploadFile.builder()
            .fileName(req.getFileName())
            .fileUrl(req.getFileUrl())
            .build()
    );
  }

  @Transactional
  public void delete(UploadFile profileImage) {
    if (profileImage != null) {
      uploadFileRepository.delete(profileImage);
    }
  }

  @Transactional
  public void delete(Long id) {
    UploadFile uploadFile = this.getById(id);
    uploadFileRepository.delete(uploadFile);
  }

  @Transactional
  public void delete(List<Long> ids) {
    List<UploadFile> uploadFiles = this.gets(ids);
    uploadFileRepository.deleteAll(uploadFiles);
  }

  public UploadFileResp uploadFileResp(UploadFile uploadFile) {

    if (uploadFile == null) {
      return null;
    }

    return UploadFileResp.builder()
        .id(uploadFile.getId())
        .createdAt(uploadFile.getCreatedAt())

        .fileName(uploadFile.getFileName())
        .fileUrl(uploadFile.getFileUrl())
        .build();
  }


}
