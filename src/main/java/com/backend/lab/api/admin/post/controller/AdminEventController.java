package com.backend.lab.api.admin.post.controller;

import static com.backend.lab.common.util.AuthUtil.getUserId;

import com.backend.lab.api.admin.post.facade.AdminPostFacade;
import com.backend.lab.common.auth.annotation.RequireAdminRole;
import com.backend.lab.common.entity.dto.resp.PageResp;
import com.backend.lab.domain.post.entity.dto.req.PostCreateReq;
import com.backend.lab.domain.post.entity.dto.req.PostUpdateReq;
import com.backend.lab.domain.post.entity.dto.req.SearchPostOptions;
import com.backend.lab.domain.post.entity.dto.resp.PostResp;
import com.backend.lab.domain.post.entity.vo.PostType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequireAdminRole
@RestController
@RequestMapping("/admin/event")
@RequiredArgsConstructor
@Tag(name = "[관리자] 이벤트 관리")
public class AdminEventController {

  private final AdminPostFacade adminPostFacade;

  private final PostType TYPE = PostType.EVENT;

  @Operation(summary = "이벤트 검색")
  @GetMapping
  public PageResp<PostResp> search(
      @ModelAttribute SearchPostOptions options
  ) {
    options.setType(TYPE);
    return adminPostFacade.search(options);
  }

  @Operation(summary = "이벤트 상세 조회")
  @GetMapping("/{id}")
  public PostResp getPostById(
      @PathVariable("id") Long id
  ) {
    return adminPostFacade.getById(id);
  }

  @PreAuthorize("hasAuthority('MANAGE_EVENT')")
  @Operation(summary = "이벤트 생성")
  @PostMapping
  public void create(
      @RequestBody PostCreateReq req
  ) {
    req.setType(TYPE);
    adminPostFacade.create(getUserId(), req);
  }

  @PreAuthorize("hasAuthority('MANAGE_EVENT')")
  @Operation(summary = "이벤트 수정")
  @PutMapping("/{id}")
  public void update(
      @PathVariable("id") Long id,
      @RequestBody PostUpdateReq req
  ) {
    adminPostFacade.update(id, req);
  }

  @PreAuthorize("hasAuthority('MANAGE_EVENT')")
  @Operation(summary = "이벤트 삭제")
  @DeleteMapping("/{id}")
  public void delete(
      @PathVariable("id") Long id
  ) {
    adminPostFacade.delete(id);
  }
}
