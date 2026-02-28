package com.backend.lab.api.admin.organization.user.facade;

import com.backend.lab.api.admin.global.dto.resp.AdminGlobalResp;
import com.backend.lab.api.admin.global.facade.AdminGlobalFacade;
import com.backend.lab.common.entity.dto.resp.PageResp;
import com.backend.lab.domain.admin.core.entity.Admin;
import com.backend.lab.domain.admin.core.entity.DeletedAdmin;
import com.backend.lab.domain.admin.core.entity.dto.req.SearchAdminOptions;
import com.backend.lab.domain.admin.core.service.AdminService;
import com.backend.lab.domain.admin.core.service.DeletedAdminService;
import com.backend.lab.domain.uploadFile.service.UploadFileService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminDeletedUserFacade {

  private final DeletedAdminService deletedAdminService;
  private final AdminGlobalFacade adminCommonFacade;
  private final UploadFileService uploadFileService;
  private final AdminService adminService;

  public PageResp<AdminGlobalResp> search(SearchAdminOptions options) {
    Page<DeletedAdmin> page = deletedAdminService.search(options);

    List<AdminGlobalResp> data = page.getContent().stream()
        .map(adminCommonFacade::adminGlobalResp)
        .toList();

    return new PageResp<>(page, data);
  }

  @Transactional
  public void restore(Long adminId) {
    deletedAdminService.restore(adminId);
  }

  @Transactional
  public void remove(Long adminId) {
    Admin admin = adminService.getById(adminId);
    uploadFileService.delete(admin.getProfileImage());
    deletedAdminService.remove(adminId);
  }
}
