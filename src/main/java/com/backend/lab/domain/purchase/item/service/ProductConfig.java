package com.backend.lab.domain.purchase.item.service;

import com.backend.lab.domain.purchase.item.entity.PurchaseProduct;
import com.backend.lab.domain.purchase.item.entity.vo.ProductType;
import jakarta.annotation.PostConstruct;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.transaction.annotation.Transactional;

@Configuration
@RequiredArgsConstructor
public class ProductConfig {

  private final PurchaseProductService productService;

  @PostConstruct
  @Transactional
  public void initialize() {

    final int PRODUCTION_CNT = 6;

    List<PurchaseProduct> products = productService.gets();
    if (!products.isEmpty() && products.size() >= PRODUCTION_CNT) {
      return;
    }

    // 이용권
    products.add(
        PurchaseProduct.builder()
            .name("1개월(30일)")
            .type(ProductType.TICKET)
            .price(100000)
            .amount(30)
            .build()
    );
    products.add(
        PurchaseProduct.builder()
            .name("6개월(180일)")
            .type(ProductType.TICKET)
            .price(500000)
            .amount(180)
            .build()
    );
    products.add(
        PurchaseProduct.builder()
            .name("12개월(365일)")
            .type(ProductType.TICKET)
            .price(1000000)
            .amount(365)
            .build()
    );

    // 매도자 연락처 열람권
    products.add(
        PurchaseProduct.builder()
            .name("열람권(10개)")
            .type(ProductType.SHOW_CONTACT)
            .price(50000)
            .amount(10)
            .build()
    );

    // 매물중개담당 광고권
    products.add(
        PurchaseProduct.builder()
            .name("기본팩(30개)")
            .type(ProductType.ADVERTISE)
            .price(300000)
            .amount(30)
            .build()
    );
    products.add(
        PurchaseProduct.builder()
            .name("추가팩(10개)")
            .type(ProductType.ADVERTISE)
            .price(100000)
            .amount(10)
            .build()
    );

    productService.save(products);

  }

  @Configuration
  @Profile({"local", "develop"})
  @RequiredArgsConstructor
  public static class TestConfig {

    private final PurchaseProductService productService;

    @PostConstruct
    @Transactional
    public void addTestProduct() {
      Optional<PurchaseProduct> optionalTestProduct = productService.gets().stream()
          .filter(p -> p.getPrice() == 1000)
          .findAny();

      if (optionalTestProduct.isEmpty()) {

        productService.save(
            PurchaseProduct.builder()
                .name("테스트 이용권")
                .type(ProductType.TICKET)
                .price(1000)
                .amount(30)
                .build()

        );
        productService.save(
            PurchaseProduct.builder()
                .name("테스트 연락처 열람권")
                .type(ProductType.SHOW_CONTACT)
                .price(1000)
                .amount(10)
                .build()

        );
        productService.save(
            PurchaseProduct.builder()
                .name("테스트 광고권")
                .type(ProductType.ADVERTISE)
                .price(1000)
                .amount(30)
                .build()

        );
      }
    }
  }
}
