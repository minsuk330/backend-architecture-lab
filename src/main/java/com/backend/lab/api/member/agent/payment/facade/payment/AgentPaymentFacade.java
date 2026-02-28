package com.backend.lab.api.member.agent.payment.facade.payment;

import com.backend.lab.api.member.agent.payment.facade.payment.dto.NicePaymentReq;
import com.backend.lab.api.member.agent.payment.facade.payment.dto.PaymentResp;
import com.backend.lab.common.auth.jwt.Jwt;
import com.backend.lab.common.auth.jwt.Jwt.Claims;
import com.backend.lab.common.auth.jwt.UserType;
import com.backend.lab.common.exception.BusinessException;
import com.backend.lab.common.exception.ErrorCode;
import com.backend.lab.common.payment.NicePaymentService;
import com.backend.lab.common.payment.dto.NicePaymentApprovalReq;
import com.backend.lab.common.payment.dto.NicePaymentData;
import com.backend.lab.domain.agentItem.core.service.AgentItemService;
import com.backend.lab.domain.member.core.entity.Member;
import com.backend.lab.domain.member.core.service.AgentService;
import com.backend.lab.domain.purchase.core.entity.Purchase;
import com.backend.lab.domain.purchase.core.entity.PurchaseItem;
import com.backend.lab.domain.purchase.core.entity.vo.PurchaseMethod;
import com.backend.lab.domain.purchase.core.entity.vo.PurchaseStatus;
import com.backend.lab.domain.purchase.core.service.PurchaseService;
import com.backend.lab.domain.purchase.item.entity.PurchaseProduct;
import com.backend.lab.domain.purchase.item.service.PurchaseProductService;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AgentPaymentFacade {

  private final Jwt jwt;
  private final NicePaymentService nicePaymentService;
  private final AgentService agentService;
  private final PurchaseProductService productService;
  private final PurchaseService purchaseService;
  private final AgentItemService agentItemService;

  @Transactional
  public NicePaymentData getFormData(NicePaymentReq req) {

    Claims claims = jwt.verify(req.getAccessToken());
    Long agentId = claims.getMemberId();
    UserType userType = claims.getUserType();

    if (agentId == null || userType == null || !userType.equals(UserType.MEMBER)) {
      throw new BusinessException(ErrorCode.UNAUTHORIZED);
    }

    Member member = agentService.getById(agentId);
    List<Long> productIds = req.getProductIds();

    Map<PurchaseProduct, Integer> countPerProduct = new HashMap<>();
    for (Long productId : productIds) {
      PurchaseProduct product = productService.getById(productId);
      countPerProduct.put(product, countPerProduct.getOrDefault(product, 0) + 1);
    }

    String orderNumber = UUID.randomUUID().toString();
    Purchase purchase = purchaseService.create(
        member.getId(),
        req.getMethod(),
        orderNumber,
        countPerProduct
    );

    String goodsName = purchase.getName();
    int amount = purchase.getTotalPrice();
    return nicePaymentService.getFormData(
        goodsName,
        amount,
        orderNumber,
        req.getMethod(),
        member,
        req.getRedirectUrl()
    );
  }

  @Transactional
  public PaymentResp approve(NicePaymentApprovalReq req, PurchaseMethod purchaseMethod) {
    String orderNumber = req.getMoid();
    Purchase purchase = purchaseService.getByOrderNumber(orderNumber);

    String redirectUrl = req.getReqReserved();
    String authResultCode = req.getAuthResultCode();

    if (!"0000".equals(authResultCode)) {
      return PaymentResp.builder()
          .success(false)
          .message(req.getAuthResultMsg())
          .redirectUrl(redirectUrl)
          .build();
    }

    PaymentResp paymentResp = nicePaymentService.approve(req, purchaseMethod);
    purchase.setPurchasedAt(LocalDateTime.now());

    boolean success = paymentResp.isSuccess();
    if (success) {
      purchase.setStatus(PurchaseStatus.SUCCESS);

      Map<PurchaseProduct, Integer> products = new HashMap<>();
      for (PurchaseItem item : purchase.getItems()) {
        PurchaseProduct product = productService.getById(item.getProduct().getId());
        products.put(product, products.getOrDefault(product, 0) + item.getAmount());
      }
      agentItemService.applyPurchase(agentService.getById(purchase.getMemberId()), products);
    } else {
      purchase.setStatus(PurchaseStatus.FAILED);
    }

    paymentResp.setRedirectUrl(redirectUrl);
    return paymentResp;
  }
}
