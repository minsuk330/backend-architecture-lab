package com.backend.lab.api.member.agent.thumbnail.facade;

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
public class AgentThumbnailFacade {

  private final ThumbnailService thumbnailService;
  private final UploadFileService uploadFileService;

  @Transactional
  public ThumbnailResp get (Long agentId) {
    Thumbnail thumbnail = thumbnailService.ensureAgentThumbnailExist(agentId);
    return this.thumbnailResp(thumbnail);
  }
  @Transactional
  public void imageUpdate(ThumbnailReq req, Long agentId) {

    // 기존id 들어올 수 있음
    if(req.getThumbnailIds()!=null && !req.getThumbnailIds().isEmpty()) {
      List<UploadFile> images = req.getThumbnailIds().stream()
          .map(uploadFileService::getById)
          .toList();
      req.setThumbnails(images);
    }
    thumbnailService.updateImages(req, ThumbnailType.AGENT,agentId);
  }

  public ThumbnailResp thumbnailResp(Thumbnail thumbnail) {
    return ThumbnailResp.builder()
        .thumbnails(thumbnail.getThumbnails().stream()
            .map(uploadFileService::uploadFileResp)
            .toList())
        .build();
  }
}
