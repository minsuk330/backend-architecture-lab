package com.backend.lab.domain.admin.permission.service;

import com.backend.lab.common.exception.BusinessException;
import com.backend.lab.common.exception.ErrorCode;
import com.backend.lab.common.util.NullUtils;
import com.backend.lab.domain.admin.permission.entity.Permission;
import com.backend.lab.domain.admin.permission.entity.dto.req.PermissionCreateReq;
import com.backend.lab.domain.admin.permission.entity.dto.req.PermissionRerankReq;
import com.backend.lab.domain.admin.permission.entity.dto.req.PermissionUpdateReq;
import com.backend.lab.domain.admin.permission.entity.dto.resp.PermissionResp;
import com.backend.lab.domain.admin.permission.repository.PermissionRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PermissionService {

  private final PermissionRepository permissionRepository;

  public Permission getById(Long id) {
    return permissionRepository.findById(id)
        .orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND, "Permission"));
  }

  public List<Permission> gets() {
    return permissionRepository.findAll(Sort.by("rank").ascending());
  }

  public List<Permission> gets(List<Long> ids) {
    return permissionRepository.findAllById(ids);
  }

  @Transactional
  public void create(PermissionCreateReq req) {
    permissionRepository.save(
        Permission.builder()
            .rank(req.getRank())
            .name(req.getName())
            .active(req.getActive())
            .migrationPermissionId(req.getMigrationPermissionId())

            .maemoolActive(true)
            .canManageMaemool(req.getCanManageMaemool())
            .secretMemoExposeLevel(req.getSecretMemoExposeLevel())

            .interestActive(req.getInterestActive())
            .canManageInterestGroup(req.getCanManageInterestGroup())

            .requestRegisterActive(req.getRequestRegisterActive())
            .canManageRegisterState(req.getCanManageState())

            .customerActive(req.getCustomerActive())
            .canManageCustomer(req.getCanManageCustomer())

            .boardActive(req.getBoardActive())
            .canManageNotice(req.getCanManageNotice())
            .canManageEvent(req.getCanManageEvent())
            .canManagePopup(req.getCanManagePopup())
            .canManageTodayNews(req.getCanManageTodayNews())

            .homePageActive(req.getHomePageActive())
            .canManageCategory(req.getCanManageCategory())
            .canManagePrint(req.getCanManagePrint())
            .canManageMarketingThumbnail(req.getCanManageMarketingThumbnail())
            .canManageMarketingSlogan(req.getCanManageMarketingSlogan())

            .taskAndCustomerHistoryActive(req.getTaskAndCustomerHistoryActive())

            .purchaseHistoryActive(req.getPurchaseHistoryActive())
            .usageHistoryActive(req.getUsageHistoryActive())

            .build()
    );
  }

  @Transactional
  public void update(Long id, PermissionUpdateReq req) {
    Permission permission = this.getById(id);

    permission.setRank(req.getRank());
    permission.setName(req.getName());
    permission.setActive(req.getActive());
    permission.setMigrationPermissionId(req.getMigrationPermissionId());

    permission.setMaemoolActive(true);
    permission.setCanManageMaemool(NullUtils.getBoolean(req.getCanManageMaemool()));
    permission.setSecretMemoExposeLevel(req.getSecretMemoExposeLevel());

    permission.setInterestActive(NullUtils.getBoolean(req.getInterestActive()));
    permission.setCanManageInterestGroup(NullUtils.getBoolean(req.getCanManageInterestGroup()));

    permission.setRequestRegisterActive(NullUtils.getBoolean(req.getRequestRegisterActive()));
    permission.setCanManageRegisterState(NullUtils.getBoolean(req.getCanManageState()));

    permission.setCustomerActive(NullUtils.getBoolean(req.getCustomerActive()));
    permission.setCanManageCustomer(NullUtils.getBoolean(req.getCanManageCustomer()));

    permission.setBoardActive(NullUtils.getBoolean(req.getBoardActive()));
    permission.setCanManageNotice(NullUtils.getBoolean(req.getCanManageNotice()));
    permission.setCanManageEvent(NullUtils.getBoolean(req.getCanManageEvent()));
    permission.setCanManagePopup(NullUtils.getBoolean(req.getCanManagePopup()));
    permission.setCanManageTodayNews(NullUtils.getBoolean(req.getCanManageTodayNews()));

    permission.setHomePageActive(NullUtils.getBoolean(req.getHomePageActive()));
    permission.setCanManageCategory(NullUtils.getBoolean(req.getCanManageCategory()));
    permission.setCanManagePrint(NullUtils.getBoolean(req.getCanManagePrint()));
    permission.setCanManageMarketingThumbnail(NullUtils.getBoolean(req.getCanManageMarketingThumbnail()));
    permission.setCanManageMarketingSlogan(NullUtils.getBoolean(req.getCanManageMarketingSlogan()));

    permission.setTaskAndCustomerHistoryActive(NullUtils.getBoolean(req.getTaskAndCustomerHistoryActive()));

    permission.setPurchaseHistoryActive(NullUtils.getBoolean(req.getPurchaseHistoryActive()));
    permission.setUsageHistoryActive(NullUtils.getBoolean(req.getUsageHistoryActive()));
  }

  @Transactional
  public void rerank(PermissionRerankReq req) {
    List<Long> orderedIds = req.getIds();
    List<Permission> permissions = this.gets(req.getIds());

    for (int i = 0; i < orderedIds.size(); i++) {
      Long id = orderedIds.get(i);
      Permission permission = permissions.stream()
          .filter(p -> p.getId().equals(id))
          .findFirst()
          .orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND, "Permission"));

      permission.setRank(i * 10);
    }

  }

  @Transactional
  public void delete(Long id) {
    Permission permission = this.getById(id);
    permissionRepository.delete(permission);
  }

  public PermissionResp permissionResp(Permission permission) {

    if (permission == null) {
      return null;
    }

    PermissionResp permissionResp = PermissionResp.builder()
        .id(permission.getId())
        .createdAt(permission.getCreatedAt())
        .updatedAt(permission.getUpdatedAt())

        .rank(permission.getRank())
        .name(permission.getName())
        .isActive(permission.getActive())
        .migrationPermissionId(permission.getMigrationPermissionId())

        .isMaemoolActive(permission.getMaemoolActive())
        .canManageMaemool(permission.getCanManageMaemool())
        .secretMemoExposeLevel(permission.getSecretMemoExposeLevel())

        .isInterestActive(permission.getInterestActive())
        .canManageInterestGroup(permission.getCanManageInterestGroup())

        .isRequestRegisterActive(permission.getRequestRegisterActive())
        .canManageState(permission.getCanManageRegisterState())

        .isCustomerActive(permission.getCustomerActive())
        .canManageCustomer(permission.getCanManageCustomer())

        .isBoardActive(permission.getBoardActive())
        .canManageNotice(permission.getCanManageNotice())
        .canManageEvent(permission.getCanManageEvent())
        .canManagePopup(permission.getCanManagePopup())
        .canManageTodayNews(permission.getCanManageTodayNews())

        .isHomePageActive(permission.getHomePageActive())
        .canManageCategory(permission.getCanManageCategory())
        .canManagePrint(permission.getCanManagePrint())
        .canManageMarketingThumbnail(permission.getCanManageMarketingThumbnail())
        .canManageMarketingSlogan(permission.getCanManageMarketingSlogan())

        .isTaskAndCustomerHistoryActive(permission.getTaskAndCustomerHistoryActive())
        .isUsageHistoryActive(permission.getUsageHistoryActive())

        .isPurchaseHistoryActive(permission.getPurchaseHistoryActive())
        .build();

    Long migrationPermissionId = permission.getMigrationPermissionId();
    if( migrationPermissionId != null) {
      Permission next = this.getById(migrationPermissionId);
      permissionResp.setMigrationPermissionName(next.getName());
    }
    return permissionResp;
  }
}
