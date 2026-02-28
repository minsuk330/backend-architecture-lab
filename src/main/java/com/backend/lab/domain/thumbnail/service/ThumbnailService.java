package com.backend.lab.domain.thumbnail.service;

import com.backend.lab.domain.thumbnail.entity.Thumbnail;
import com.backend.lab.domain.thumbnail.entity.dto.req.ThumbnailReq;
import com.backend.lab.domain.thumbnail.entity.vo.ThumbnailType;
import com.backend.lab.domain.thumbnail.repository.ThumbnailRepository;
import com.backend.lab.domain.uploadFile.entity.UploadFile;
import com.backend.lab.domain.uploadFile.service.UploadFileService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ThumbnailService {

  private final ThumbnailRepository thumbnailRepository;
  private final UploadFileService uploadFileService;

  //관리자 공통
  @Transactional
  public Thumbnail ensureAdminThumbnailExist() {
    return thumbnailRepository.findByThumbnailTypeAndAgentIdIsNull(ThumbnailType.ADMIN)
        .orElseGet(this::createDefaultAdminThumbnail);
  }

  private Thumbnail createDefaultAdminThumbnail() {
    return thumbnailRepository.save(
        Thumbnail.builder()
            .agentId(null)
            .thumbnailType(ThumbnailType.ADMIN)
            .build()
    );
  }

  //각 공인중개사별
  @Transactional
  public Thumbnail ensureAgentThumbnailExist(Long agentId) {
    return thumbnailRepository.findByThumbnailTypeAndAgentId(ThumbnailType.AGENT,agentId)
        .orElseGet(() -> createDefaultAgentThumbnail(agentId));
  }

  private Thumbnail createDefaultAgentThumbnail(Long agentId) {
    UploadFile firstImage = uploadFileService.getById(8296L);
    UploadFile lastImage = uploadFileService.getById(8297L);
    return thumbnailRepository.save(
        Thumbnail.builder()
            .agentId(agentId)
            .thumbnails(List.of(firstImage, lastImage))
            .thumbnailType(ThumbnailType.AGENT)
            .build()
    );
  }
  @Transactional
  public void updateImages(ThumbnailReq req, ThumbnailType thumbnailType, Long agentId)  {
    Thumbnail thumbnail;

    if (thumbnailType == ThumbnailType.ADMIN) {
      thumbnail = ensureAdminThumbnailExist();

    }
    else {
      thumbnail = ensureAgentThumbnailExist(agentId);
    }

    if(req.getThumbnails() != null) {
      thumbnail.getThumbnails().clear();
      thumbnail.getThumbnails().addAll(req.getThumbnails());
    }

  }
  //이거 퍼사드로 옮기자
  @Transactional(readOnly = true)
  public UploadFile getMainThumbnail(ThumbnailType thumbnailType, Long agentId) {
    if (thumbnailType == ThumbnailType.ADMIN) {
      return thumbnailRepository.findByThumbnailTypeAndAgentIdIsNull(ThumbnailType.ADMIN)
          .map(Thumbnail::getMainThumbnail)  // 첫 번째 원소 반환
          .orElse(null);
    } else {
      return thumbnailRepository.findByThumbnailTypeAndAgentId(ThumbnailType.AGENT, agentId)
          .map(Thumbnail::getMainThumbnail)
          .orElse(null);
    }
  }






}
