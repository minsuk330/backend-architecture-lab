package com.backend.lab.api.admin.property.info.facade;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import com.backend.lab.api.admin.property.info.dto.resp.PropertyFloorInfoItem;
import com.backend.lab.api.admin.property.info.dto.resp.PropertyFloorInfoResp;
import com.backend.lab.api.admin.property.info.dto.resp.PropertyMoveInfoItem;
import com.backend.lab.api.admin.property.info.dto.resp.PropertyMoveInfoResp;
import com.backend.lab.common.openapi.service.gong.FloorApiPort;
import com.backend.lab.common.openapi.service.gong.LedgerApiPort;
import com.backend.lab.common.openapi.service.toji.LandCharacteristicApiPort;
import com.backend.lab.common.openapi.service.toji.LandPossessionApiPort;
import com.backend.lab.common.openapi.service.toji.LandUsePlanApiPort;
import com.backend.lab.common.openapi.service.tojiList.LandMoveApiPort;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * PropertyInfoFacade 단위 테스트
 *
 * Outbound Port 인터페이스(FloorApiPort, LandMoveApiPort 등)를 람다/Mock으로 대체.
 * 실제 외부 API 없이 Spring 컨텍스트 없이 동작 — Port 패턴의 핵심 이점을 보여주는 테스트.
 */
class PropertyInfoFacadeTest {

    private static final String TEST_PNU = "1168010100101390000";

    private FloorApiPort floorApiPort;
    private LandMoveApiPort landMoveApiPort;
    private LedgerApiPort ledgerApiPort;
    private LandCharacteristicApiPort landCharacteristicApiPort;
    private LandPossessionApiPort landPossessionApiPort;
    private LandUsePlanApiPort landUsePlanApiPort;

    private PropertyInfoFacade facade;

    @BeforeEach
    void setUp() {
        floorApiPort = mock(FloorApiPort.class);
        landMoveApiPort = mock(LandMoveApiPort.class);
        ledgerApiPort = mock(LedgerApiPort.class);
        landCharacteristicApiPort = mock(LandCharacteristicApiPort.class);
        landPossessionApiPort = mock(LandPossessionApiPort.class);
        landUsePlanApiPort = mock(LandUsePlanApiPort.class);

        facade = new PropertyInfoFacade(
            ledgerApiPort,
            floorApiPort,
            landMoveApiPort,
            landCharacteristicApiPort,
            landPossessionApiPort,
            landUsePlanApiPort
        );
    }

    @Test
    @DisplayName("층 정보 조회 — FloorApiPort Mock으로 실제 API 없이 테스트")
    void getFloors_returns_data_from_port() {
        // given: Port를 람다로 구현 — 실제 API 호출 없음
        List<PropertyFloorInfoItem> mockFloors = List.of(
            PropertyFloorInfoItem.builder().floor("1").areaMeter(150.0).upjong("근린생활시설").build(),
            PropertyFloorInfoItem.builder().floor("2").areaMeter(120.0).upjong("사무소").build()
        );
        given(floorApiPort.getFloorInfo(TEST_PNU))
            .willReturn(CompletableFuture.completedFuture(mockFloors));

        // when
        PropertyFloorInfoResp result = facade.getFloors(TEST_PNU);

        // then
        assertThat(result.getData()).hasSize(2);
        assertThat(result.getData().get(0).getFloor()).isEqualTo("1");
        assertThat(result.getData().get(1).getUpjong()).isEqualTo("사무소");
    }

    @Test
    @DisplayName("층 정보 조회 — 빈 결과")
    void getFloors_empty_result() {
        // given
        given(floorApiPort.getFloorInfo(TEST_PNU))
            .willReturn(CompletableFuture.completedFuture(List.of()));

        // when
        PropertyFloorInfoResp result = facade.getFloors(TEST_PNU);

        // then
        assertThat(result.getData()).isEmpty();
    }

    @Test
    @DisplayName("토지이동정보 조회 — LandMoveApiPort Mock으로 실제 API 없이 테스트")
    void getMoves_returns_data_from_port() {
        // given
        List<PropertyMoveInfoItem> mockMoves = List.of(
            PropertyMoveInfoItem.builder().moveReason("분할").moveDate("20200101").build(),
            PropertyMoveInfoItem.builder().moveReason("합병").moveDate("20210601").build()
        );
        given(landMoveApiPort.getLandMovesInfo(TEST_PNU))
            .willReturn(CompletableFuture.completedFuture(mockMoves));

        // when
        PropertyMoveInfoResp result = facade.getMoves(TEST_PNU);

        // then
        assertThat(result.getData()).hasSize(2);
        assertThat(result.getData().get(0).getMoveReason()).isEqualTo("분할");
        assertThat(result.getData().get(1).getMoveDate()).isEqualTo("20210601");
    }
}
