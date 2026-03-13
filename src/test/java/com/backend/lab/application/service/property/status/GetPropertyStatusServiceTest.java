package com.backend.lab.application.service.property.status;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import com.backend.lab.api.admin.property.core.dto.resp.PropertyStatusResp;
import com.backend.lab.domain.property.core.entity.Property;
import com.backend.lab.domain.property.core.entity.vo.PropertyStatus;
import com.backend.lab.domain.property.core.service.PropertyService;
import com.backend.lab.domain.property.sale.entity.Sale;
import com.backend.lab.domain.property.sale.entity.dto.SaleResp;
import com.backend.lab.domain.property.sale.service.SaleService;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * GetPropertyStatusService 단위 테스트
 *
 * Port 인터페이스 덕분에 Spring 컨텍스트 없이 순수 Java로 테스트 가능.
 * PropertyService, SaleService를 Mockito로 대체 — 외부 의존성 없음.
 */
class GetPropertyStatusServiceTest {

    private PropertyService propertyService;
    private SaleService saleService;
    private GetPropertyStatusService service;

    @BeforeEach
    void setUp() {
        propertyService = mock(PropertyService.class);
        saleService = mock(SaleService.class);
        service = new GetPropertyStatusService(propertyService, saleService);
    }

    @Test
    @DisplayName("READY 상태 조회 — 날짜 필드 없이 status만 반환")
    void getStatus_READY() {
        // given
        Property property = Property.builder()
            .status(PropertyStatus.READY)
            .build();
        given(propertyService.getById(1L)).willReturn(property);

        // when
        PropertyStatusResp result = service.getStatus(1L);

        // then
        assertThat(result.getStatus()).isEqualTo(PropertyStatus.READY);
        assertThat(result.getCompletedAt()).isNull();
        assertThat(result.getPendingAt()).isNull();
        assertThat(result.getSale()).isNull();
    }

    @Test
    @DisplayName("COMPLETE 상태 조회 — completedAt 포함")
    void getStatus_COMPLETE_with_completedAt() {
        // given
        LocalDate completedDate = LocalDate.of(2024, 6, 1);
        Property property = Property.builder()
            .status(PropertyStatus.COMPLETE)
            .completedAt(completedDate)
            .build();
        given(propertyService.getById(2L)).willReturn(property);

        // when
        PropertyStatusResp result = service.getStatus(2L);

        // then
        assertThat(result.getStatus()).isEqualTo(PropertyStatus.COMPLETE);
        assertThat(result.getCompletedAt()).isEqualTo(completedDate);
    }

    @Test
    @DisplayName("PENDING 상태 조회 — pendingAt 포함")
    void getStatus_PENDING_with_pendingAt() {
        // given
        LocalDate pendingDate = LocalDate.of(2024, 7, 15);
        Property property = Property.builder()
            .status(PropertyStatus.PENDING)
            .pendingAt(pendingDate)
            .build();
        given(propertyService.getById(3L)).willReturn(property);

        // when
        PropertyStatusResp result = service.getStatus(3L);

        // then
        assertThat(result.getStatus()).isEqualTo(PropertyStatus.PENDING);
        assertThat(result.getPendingAt()).isEqualTo(pendingDate);
    }

    @Test
    @DisplayName("SOLD 상태 조회 — Sale 정보 포함")
    void getStatus_SOLD_with_sale() {
        // given
        Property property = Property.builder()
            .status(PropertyStatus.SOLD)
            .build();
        Sale sale = mock(Sale.class);
        SaleResp saleResp = SaleResp.builder()
            .propertyId(4L)
            .salePrice(500_000_000L)
            .build();
        given(propertyService.getById(4L)).willReturn(property);
        given(saleService.getByProperty(4L)).willReturn(sale);
        given(saleService.saleResp(sale)).willReturn(saleResp);

        // when
        PropertyStatusResp result = service.getStatus(4L);

        // then
        assertThat(result.getStatus()).isEqualTo(PropertyStatus.SOLD);
        assertThat(result.getSale()).isNotNull();
        assertThat(result.getSale().getSalePrice()).isEqualTo(500_000_000L);
    }

    @Test
    @DisplayName("SOLD 상태이지만 Sale 데이터 없음 — sale 필드 null")
    void getStatus_SOLD_without_sale() {
        // given
        Property property = Property.builder()
            .status(PropertyStatus.SOLD)
            .build();
        given(propertyService.getById(5L)).willReturn(property);
        given(saleService.getByProperty(5L)).willReturn(null);

        // when
        PropertyStatusResp result = service.getStatus(5L);

        // then
        assertThat(result.getStatus()).isEqualTo(PropertyStatus.SOLD);
        assertThat(result.getSale()).isNull();
    }
}
