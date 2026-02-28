package com.backend.lab.domain.popup.service;

import com.backend.lab.common.exception.BusinessException;
import com.backend.lab.common.exception.ErrorCode;
import com.backend.lab.domain.admin.core.entity.Admin;
import com.backend.lab.domain.admin.core.entity.dto.resp.AdminResp;
import com.backend.lab.domain.popup.entity.Popup;
import com.backend.lab.domain.popup.entity.dto.req.PopupCreateReq;
import com.backend.lab.domain.popup.entity.dto.req.PopupUpdateReq;
import com.backend.lab.domain.popup.entity.dto.req.SearchPopupOptions;
import com.backend.lab.domain.popup.entity.dto.resp.PopupResp;
import com.backend.lab.domain.popup.entity.vo.PopupViewType;
import com.backend.lab.domain.popup.repository.PopupRepository;
import com.backend.lab.domain.uploadFile.entity.dto.resp.UploadFileResp;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PopupService {

  private final PopupRepository popupRepository;

  public Popup getById(Long id) {
    return popupRepository.findById(id)
        .orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND, "Popup"));
  }

  public List<Popup> getsAvailable(LocalDate date, PopupViewType viewType) {
    return popupRepository.getsAvailable(date, viewType);
  }

  public Page<Popup> search(SearchPopupOptions options, PopupViewType viewType) {
    return popupRepository.search(options, viewType);
  }

  @Transactional
  public void create(Admin admin, PopupCreateReq req) {
    popupRepository.save(
        Popup.builder()
            .createdBy(admin)
            .image(req.getImage())
            .url(req.getUrl())

            .canAnonymousView(req.getCanAnonymousView())
            .canAgentView(req.getCanAgentView())
            .canBuyerView(req.getCanBuyerView())
            .canSellerView(req.getCanSellerView())

            .startAt(req.getStartAt())
            .endAt(req.getEndAt())
            .build()
    );
  }

  @Transactional
  public void update(Long id, PopupUpdateReq req) {
    Popup popup = getById(id);

    popup.setImage(req.getImage());
    popup.setUrl(req.getUrl());
    popup.setStartAt(req.getStartAt());
    popup.setEndAt(req.getEndAt());

    popup.setCanAnonymousView(req.getCanAnonymousView());
    popup.setCanAgentView(req.getCanAgentView());
    popup.setCanBuyerView(req.getCanBuyerView());
    popup.setCanSellerView(req.getCanSellerView());

    popupRepository.save(popup);
  }

  @Transactional
  public void delete(Long id) {
    Popup popup = getById(id);
    popupRepository.delete(popup);
  }

  public PopupResp popupResp(Popup popup, AdminResp admin, UploadFileResp image) {
    return PopupResp.builder()
        .id(popup.getId())
        .createdAt(popup.getCreatedAt())

        .createdBy(admin)
        .image(image)
        .url(popup.getUrl())

        .canAnonymousView(popup.getCanAnonymousView())
        .canAgentView(popup.getCanAgentView())
        .canBuyerView(popup.getCanBuyerView())
        .canSellerView(popup.getCanSellerView())

        .startAt(popup.getStartAt())
        .endAt(popup.getEndAt())
        .build();
  }
}
