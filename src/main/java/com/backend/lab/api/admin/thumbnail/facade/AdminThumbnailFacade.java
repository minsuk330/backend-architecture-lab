package com.backend.lab.api.admin.thumbnail.facade;

import com.backend.lab.domain.thumbnail.entity.Thumbnail;
import com.backend.lab.domain.thumbnail.entity.dto.req.ThumbnailReq;
import com.backend.lab.domain.thumbnail.entity.dto.resp.ThumbnailResp;
import com.backend.lab.domain.thumbnail.entity.vo.ThumbnailType;
import com.backend.lab.domain.thumbnail.service.ThumbnailService;
import com.backend.lab.domain.uploadFile.entity.UploadFile;
import com.backend.lab.domain.uploadFile.service.UploadFileService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminThumbnailFacade {

  private final ThumbnailService thumbnailService;
  private final UploadFileService uploadFileService;

  public ThumbnailResp get () {
    Thumbnail thumbnail = thumbnailService.ensureAdminThumbnailExist();
    return this.thumbnailResp(thumbnail);
  }

  @Transactional
  public void imageUpdate(ThumbnailReq req) {
    if(req.getThumbnailIds()!=null && !req.getThumbnailIds().isEmpty()) {
      List<UploadFile> images = req.getThumbnailIds().stream()
          .map(uploadFileService::getById)
          .toList();
      req.setThumbnails(images);
    }
    thumbnailService.updateImages(req, ThumbnailType.ADMIN,null);
  }


  public ThumbnailResp thumbnailResp(Thumbnail thumbnail) {
    return ThumbnailResp.builder()
        .thumbnails(thumbnail.getThumbnails().stream()
            .map(uploadFileService::uploadFileResp)
            .toList())
        .build();
  }

}
