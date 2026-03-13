package com.backend.lab.application.service.property.status;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import com.backend.lab.api.admin.property.core.dto.resp.PropertyPublicResp;
import com.backend.lab.domain.property.core.service.PropertyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * TogglePublicService 단위 테스트
 *
 * PropertyService를 Mock으로 대체해 순수 Java로 실행.
 * togglePublic() 호출 여부와 반환값 검증.
 */
class TogglePublicServiceTest {

    private PropertyService propertyService;
    private TogglePublicService service;

    @BeforeEach
    void setUp() {
        propertyService = mock(PropertyService.class);
        service = new TogglePublicService(propertyService);
    }

    @Test
    @DisplayName("비공개 → 공개로 토글")
    void toggle_from_private_to_public() {
        // given: togglePublic()이 true(공개)를 반환
        given(propertyService.togglePublic(1L)).willReturn(true);

        // when
        PropertyPublicResp result = service.toggle(1L);

        // then
        assertThat(result.getIsPublic()).isTrue();
        verify(propertyService).togglePublic(1L);
    }

    @Test
    @DisplayName("공개 → 비공개로 토글")
    void toggle_from_public_to_private() {
        // given: togglePublic()이 false(비공개)를 반환
        given(propertyService.togglePublic(2L)).willReturn(false);

        // when
        PropertyPublicResp result = service.toggle(2L);

        // then
        assertThat(result.getIsPublic()).isFalse();
        verify(propertyService).togglePublic(2L);
    }
}
