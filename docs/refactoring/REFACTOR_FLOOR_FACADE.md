# Refactoring: FloorFacade 분리

## 1. 문제상황
`FloorFacade`가 Facade라는 이름을 가지고 있지만
Controller를 위한 래퍼 역할이 아닌 **도메인 비즈니스 로직**을 직접 구현하고 있었음

```java
// 층 순위 결정 도메인 규칙이 Facade에 존재
private int getFloorPriority(String floorName) {
    if (floorName.startsWith("옥탑")) { ... }
    else if (floorName.startsWith("지")) { ... }
    else { ... }
}
```

### 문제점
- 이름(Facade)과 역할(Domain Logic)이 불일치
- 도메인 로직이 도메인 레이어가 아닌 api 레이어에 위치
- `FloorProperties`가 자신과 관련된 규칙을 모르는 빈껍데기 객체 (Anemic Domain Model)
- `deleteAllFloor()`에서 `property.getFloors().clear()` 중복 호출 버그 존재

## 2. 원인분석
`FloorFacade`가 처음부터 Facade 패턴이 아닌 도메인 서비스 역할로 설계되었고
도메인 로직이 Value Object 안이 아닌 외부 클래스에 분산되었기 때문에

## 3. 목표
- 도메인 규칙(`getPriority`)을 `FloorProperties` Value Object 안으로 이동
- `floorsAssignRank` 로직을 `FloorInfoService` (도메인 서비스)로 이동
- `deleteAllFloor` 오케스트레이션 로직을 UseCase로 이동
- `FloorFacade` 제거

## 4. 고려한 결정들
- `FloorFacade` 이름만 변경 → 근본적인 위치 문제 해결 안 됨
- 로직 전체를 `FloorInfoService`로 이동 → 도메인 규칙과 서비스 로직 분리가 안 됨
- Value Object + Service 분리 → 각 레이어 책임이 명확해짐

## 5. 최종 결정

```
Before
FloorFacade
  ├── floorsAssignRank()    (도메인 서비스 로직)
  ├── deleteAllFloor()      (오케스트레이션)
  ├── getFloorPriority()    (도메인 규칙)
  └── extractNumber()       (도메인 규칙 헬퍼)

After
FloorProperties (Value Object)
  ├── getPriority()         (도메인 규칙 - 이 객체의 데이터만으로 계산 가능)
  └── extractNumber()       (헬퍼)

FloorInfoService (Domain Service)
  └── createWithRank()      (순위 부여 + 생성)

UseCase
  └── deleteAllFloor()      (오케스트레이션)
```

## 6. 변경 후 이점
- **SRP** → 각 클래스가 하나의 책임만 담당
- **Rich Domain Model** → `FloorProperties`가 자신의 규칙을 스스로 알게 됨
- **레이어 명확화** → 도메인 로직이 도메인 레이어에 위치
- **가독성 향상** → `getFloorPriority(floor.getFloor())` → `floor.getPriority()`

## 7. trade-offs
- `FloorFacade`를 의존하던 클래스들의 수정 필요
- 로직이 여러 클래스로 분산되어 처음 보는 사람은 흐름 파악이 어려울 수 있음

---

## 8. Value Object란?

값 자체가 동일성을 나타내는 객체. ID가 없고 값이 같으면 같은 객체로 취급한다.

```java
// Entity: ID로 동일성 판단
Property p1 = new Property(id: 1);
Property p2 = new Property(id: 1);
p1 == p2 → true (같은 ID)

// Value Object: 값으로 동일성 판단
PriceProperties p1 = new PriceProperties(mmPrice: 1000);
PriceProperties p2 = new PriceProperties(mmPrice: 1000);
p1 == p2 → true (같은 값)
```

이 프로젝트에서 `@Embeddable`이 붙은 클래스들이 Value Object이다.
(`FloorProperties`, `PriceProperties`, `AddressProperties` 등)

### Value Object에 메서드를 넣는 기준

```
이 계산에 필요한 데이터가 이 객체 안에만 있는가?  → YES: 이 객체의 메서드
외부 의존성(Service, Repository)이 필요한가?      → YES: 절대 넣으면 안 됨
```

`getPriority()`는 `this.floor` 하나만 필요하고 외부 의존성 없음
→ `FloorProperties` 메서드로 적합

`calculateYeonPyongPrice()`를 `PriceProperties`에 넣은 것과 같은 원리