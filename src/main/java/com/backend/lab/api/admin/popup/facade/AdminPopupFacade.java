package com.backend.lab.api.admin.popup.facade;

import com.backend.lab.common.entity.dto.resp.PageResp;
import com.backend.lab.domain.admin.core.entity.Admin;
import com.backend.lab.domain.admin.core.service.AdminService;
import com.backend.lab.domain.popup.entity.Popup;
import com.backend.lab.domain.popup.entity.dto.req.PopupCreateReq;
import com.backend.lab.domain.popup.entity.dto.req.PopupUpdateReq;
import com.backend.lab.domain.popup.entity.dto.req.SearchPopupOptions;
import com.backend.lab.domain.popup.entity.dto.resp.PopupResp;
import com.backend.lab.domain.popup.entity.vo.PopupViewType;
import com.backend.lab.domain.popup.service.PopupService;
import com.backend.lab.domain.uploadFile.entity.UploadFile;
import com.backend.lab.domain.uploadFile.service.UploadFileService;
import java.util.Comparator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminPopupFacade {

  private final PopupService popupService;
  private final AdminService adminService;
  private final UploadFileService uploadFileService;


  @Transactional(readOnly = true)
  public PageResp<PopupResp> search(SearchPopupOptions options) {
    Page<Popup> page = popupService.search(options, PopupViewType.ALL);

    List<PopupResp> data = page.getContent().stream()
        .sorted(Comparator.nullsLast(Comparator.comparing(Popup::getCreatedAt).reversed()))
        .map(this::popupResp)
        .toList();
    return new PageResp<>(page, data);
  }

  @Transactional(readOnly = true)
  public PopupResp getById(Long popupId) {
    Popup popup = popupService.getById(popupId);
    return this.popupResp(popup);
  }

  @Transactional
  public void create(Long adminId, PopupCreateReq req) {
    Admin admin = adminService.getById(adminId);
    if (req.getImageId() != null) {
      req.setImage(uploadFileService.getById(req.getImageId()));
    }
    popupService.create(admin, req);
  }

  @Transactional
  public void update(Long popupId, PopupUpdateReq req) {

    if (req.getImageId() != null) {
      req.setImage(uploadFileService.getById(req.getImageId()));
    }
    popupService.update(popupId, req);
  }

  @Transactional
  public void delete(Long popupId) {
    popupService.delete(popupId);
  }

  public PopupResp popupResp(Popup popup) {
    UploadFile image = popup.getImage();
    try {
      Admin admin = popup.getCreatedBy();
      return popupService.popupResp(
          popup,
          adminService.adminResp(admin, uploadFileService.uploadFileResp(admin != null ? admin.getProfileImage() : null)),
          uploadFileService.uploadFileResp(image)
      );
    } catch (Exception e) {
      return popupService.popupResp(popup, adminService.adminResp(null, null), uploadFileService.uploadFileResp(image));
    }
  }
}
