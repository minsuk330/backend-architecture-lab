package com.backend.lab.api.admin.promotionText.controller;

import com.backend.lab.api.admin.promotionText.facade.PromotionMapper;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("admin/property/prom")
@Tag(name = "[공통] 홍보 문구")
public class PropertyPromotionTextController {

  private final PromotionMapper promotionMapper;





}
