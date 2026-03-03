# Refactoring: Strategy Pattern 적용

## 1. 문제상황
`AdminPropertyMemberFacade.createPropertyMember()`에서 멤버 타입에 따라
`if/else` 분기로 서로 다른 로직을 처리하고 있었음

```java
members.forEach(member -> {
    if (member.getIsNew()) {
        // 신규 고객 생성 + propertyMember 생성 + workLog 생성
    } else {
        // 기존 고객 연결 + propertyMember 생성 + workLog 업데이트
    }
});
```

### 문제점
- 새로운 멤버 타입 추가 시 `if/else` 블록을 계속 수정해야 함
- 분기마다 로직이 길어지면서 메서드 가독성 저하
- 단일 책임 원칙 위반 (하나의 메서드가 여러 타입의 처리 로직을 모두 담당)
- 테스트 어려움 (하나의 메서드에 모든 경우의 수가 섞여 있음)

## 2. 원인분석
멤버 타입(신규/기존)에 따른 처리 로직이 분기문으로만 구분되었고
유스케이스 단위 분리 없이 하나의 메서드 안에 모든 처리가 집중되었기 때문에

## 3. 목표
`isNew` 분기를 Strategy 패턴으로 분리하여
각 클래스가 하나의 멤버 타입 처리 책임만 갖도록 한다.

## 4. 고려한 결정들
- if/else 유지 → 새 타입 추가 시 기존 코드를 계속 수정해야 함 (OCP 위반)
- Strategy 패턴 적용 → 파일 수 증가, 하지만 새 타입 추가 시 새 클래스만 추가하면 됨
- Domain Event 적용 → 완전한 분리가 가능하지만 현 단계에서 복잡도가 너무 높음

## 5. 최종 결정
Strategy 패턴 적용

```
PropertyMemberStrategy        (인터페이스)
  ├── NewMemberStrategy       (신규 고객 처리)
  └── ExistingMemberStrategy  (기존 고객 처리)
```

`AdminPropertyMemberFacade`는 `List<PropertyMemberStrategy>`를 주입받아
`supports()`로 적합한 전략을 선택하고 `handle()`로 실행한다.

```java
// Before
if (member.getIsNew()) { ... }
else { ... }

// After
PropertyMemberStrategy strategy = strategies.stream()
    .filter(s -> s.supports(member))
    .findFirst()
    .orElseThrow(...);
strategy.handle(member, propertyId, admin, clientIp);
```

## 6. 변경 후 이점
- **SRP** → `NewMemberStrategy`, `ExistingMemberStrategy` 각각 하나의 책임만 담당
- **OCP** → 새 멤버 타입 추가 시 기존 코드 수정 없이 새 Strategy 클래스만 추가
- **DIP** → `AdminPropertyMemberFacade`가 구체 클래스 대신 `PropertyMemberStrategy` 인터페이스에 의존
- **테스트 용이** → 각 Strategy를 독립적으로 단위 테스트 가능, Mock 범위 최소화

## 7. trade-offs
- 파일 수 증가 (Strategy 클래스가 타입마다 하나씩 생김)
- 단순한 분기라면 오히려 과설계가 될 수 있음
- 새 팀원이 Strategy 패턴에 익숙하지 않으면 흐름 파악이 어려울 수 있음

---

## 8. Spring DI 동작 원리

### 앱 시작 시 (1회)
`@Component`가 붙은 구현체를 Spring이 모두 스캔하여 빈으로 등록한다.

```java
@Component
public class NewMemberStrategy implements PropertyMemberStrategy { ... }

@Component
public class ExistingMemberStrategy implements PropertyMemberStrategy { ... }
```

`List<PropertyMemberStrategy>`로 선언하면 Spring이 해당 인터페이스를 구현한
**모든 빈을 리스트로 묶어서** 자동 주입한다.

```java
// AdminPropertyMemberFacade 생성 시 Spring이 자동으로 주입
private final List<PropertyMemberStrategy> strategies;
// strategies = [NewMemberStrategy, ExistingMemberStrategy]
```

### 요청 시 (매번)
`supports()`로 현재 요청에 맞는 전략을 런타임에 선택한다.

```java
// member.getIsNew() = true 인 경우
// NewMemberStrategy.supports(member)      → true  ← 선택
// ExistingMemberStrategy.supports(member) → false

PropertyMemberStrategy strategy = strategies.stream()
    .filter(s -> s.supports(member))  // supports()가 true인 것만 통과
    .findFirst()                       // 첫 번째 선택
    .orElseThrow(...);

strategy.handle(member, propertyId, admin, clientIp);
// → NewMemberStrategy.handle() 실행
```

### 전체 흐름
```
앱 시작 시
  Spring → @Component 스캔
         → NewMemberStrategy 빈 등록
         → ExistingMemberStrategy 빈 등록
         → AdminPropertyMemberFacade에 List로 주입

요청 시
  createPropertyMember() 호출
    → member.getIsNew() = true
    → strategies 순회
    → NewMemberStrategy.supports() = true → 선택
    → NewMemberStrategy.handle() 실행
```

### 핵심
`AdminPropertyMemberFacade`는 구체 클래스(`NewMemberStrategy`)를 전혀 모른다.
인터페이스(`PropertyMemberStrategy`)만 알고,
어떤 구현체가 동작할지는 **`supports()`가 런타임에 결정**한다.
→ 이것이 DIP(의존성 역전)가 실제로 동작하는 방식이다.