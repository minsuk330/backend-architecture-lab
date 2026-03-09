# Refactoring: PropertyInfoFacade 서비스 인터페이스화 (OCP + ISP)

## 1. 문제상황
`PropertyInfoFacade`가 외부 공공 API를 호출하는 구체 클래스 6개에 직접 의존하고 있음
- `LedgerApiService`
- `FloorApiService`
- `LandMoveApiService`
- `LandCharacteristicApiService`
- `LandPossessionApiService`
- `LandUsePlanApiService`

각 서비스는 `WebClient`, `@Value` 등 외부 인프라에 강하게 결합되어 있어
테스트 및 구현체 교체 시 Facade 내부를 수정해야 하는 문제가 발생했다.

### 문제점
- 구현체 교체 시 Facade 수정 필요 (OCP 위반)
- 단위 테스트 시 `@SpringBootTest` 전체 컨텍스트 로드 필요
- 구체 클래스 변경이 Facade 재컴파일로 전파됨
- 특정 메서드만 교체하고 싶어도 관련 없는 메서드까지 함께 구현해야 함 (ISP 위반 가능성)

## 2. 원인분석
외부 API 호출 서비스가 인터페이스 없이 구체 클래스로만 설계되었고,
Facade가 구현 세부사항에 직접 의존하도록 작성되었기 때문에

## 3. 목표
각 API 서비스에 인터페이스를 도입하여
Facade가 구현체가 아닌 추상화에 의존하도록 한다. → OCP + ISP + DIP

## 4. 고려한 결정들
도메인(토지/건물) 기준으로 2개의 인터페이스로 묶기
- `LandApiPort` (토지 4개 서비스 메서드 통합)
- `BuildingApiPort` (건물 2개 서비스 메서드 통합)
- 파일 수는 적어지지만, 일부 메서드만 교체할 때 관련 없는 메서드까지 구현 강제 → ISP 위반

서비스당 1개씩 6개의 인터페이스로 분리
- 각 인터페이스가 하나의 책임만 가짐 → ISP 충족
- 필요한 구현체만 선택적으로 교체 가능

## 5. 최종 결정
서비스당 1개씩 6개의 인터페이스 도입

```
LedgerApiPort          ← LedgerApiService implements
FloorApiPort           ← FloorApiService implements
LandMoveApiPort        ← LandMoveApiService implements
LandCharacteristicApiPort ← LandCharacteristicApiService implements
LandPossessionApiPort  ← LandPossessionApiService implements
LandUsePlanApiPort     ← LandUsePlanApiService implements
```

구현체 내부 로직은 그대로 유지하고 `implements` 선언과 Facade의 주입 타입만 변경한다.

## 6. 변경 후 이점
- OCP 충족 → 새 공공 API나 캐시 구현체 추가 시 Facade 수정 없이 새 클래스만 추가
- ISP 충족 → 인터페이스당 메서드 1~2개로 불필요한 의존 강제 없음
- DIP 충족 → Facade가 구체 클래스 대신 추상화(인터페이스)에 의존
- 단위 테스트 용이 → Spring 없이 `mock(LandCharacteristicApiPort.class)` 주입으로 테스트 가능

```java
// Before — 전체 컨텍스트 로드 필요
@SpringBootTest
class PropertyInfoFacadeTest {
    @MockBean LandCharacteristicApiService service;
}

// After — 순수 단위 테스트
class PropertyInfoFacadeTest {
    LandCharacteristicApiPort port = mock(LandCharacteristicApiPort.class);
    PropertyInfoFacade facade = new PropertyInfoFacade(port, ...);
}
```

## 7. trade-offs
- 파일 수 증가 (인터페이스 6개 추가)
- 구현 추적 시 인터페이스 → 구현체로 한 단계 더 이동 필요
- 구현체가 현재 1개뿐이므로 단기적으로 체감 효과가 크지 않을 수 있음