# Entity 리팩토링 플랜

## 현황 진단 요약

| 문제 | 심각도 | 영향 범위 |
|------|--------|-----------|
| Member 엔티티에 4개 역할 @Embedded 혼재 | 높음 | Member 전체 |
| Long FK → @ManyToOne 관계 누락 (12곳+) | 높음 | Property, Purchase, Notification 등 |
| EAGER 페치 (10곳) | 높음 | Admin, Property, Category |
| 소프트 딜리트 3가지 패턴 혼재 | 중간 | 전체 엔티티 |
| Information 래퍼 엔티티 8개 (불필요한 조인) | 중간 | Property |
| Permission 엔티티 boolean 67개 | 높음 | 권한 시스템 |
| DeletedMember 중복 엔티티 | 중간 | Member 아카이브 |
| @Index 어노테이션 전무 | 중간 | 전체 DB 성능 |
| Cascade ALL 남용 | 중간 | PropertyWorkLog, Thumbnail |

---

## Phase 1 — 기반 설계 (최우선)

> 이 단계를 먼저 완료해야 이후 단계가 꼬이지 않음**
**
### 1-1. Member 역할 분리 (Single Table Inheritance)

**현재 구조 문제:**

```
Member
├── @Embedded CustomerProperties   ← 다른 역할이면 전부 null
├── @Embedded AgentProperties      ← 다른 역할이면 전부 null
├── @Embedded BuyerProperties      ← 다른 역할이면 전부 null
└── @Embedded SellerProperties     ← 다른 역할이면 전부 null
```

**변경 방향 (STI):**

```java
// 공통 필드만 유지
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "type", discriminatorType = DiscriminatorType.STRING)
@SQLRestriction("deleted_at IS NULL")
@SQLDelete(sql = "UPDATE member SET deleted_at = NOW() WHERE id = ?")
public abstract class Member extends BaseEntity {
    private String email;
    private String password;
    private String di;
    @ManyToOne(fetch = LAZY)
    private UploadFile profileImage;
}

@Entity
@DiscriminatorValue("CUSTOMER")
public class CustomerMember extends Member {
    private String name;
    private String phoneNumber;
    // CustomerProperties 필드 직접 선언
}

@Entity
@DiscriminatorValue("AGENT")
public class AgentMember extends Member {
    private String name;
    private String businessName;
    // AgentProperties 필드 직접 선언
}

@Entity
@DiscriminatorValue("SELLER")
public class SellerMember extends Member { ... }

@Entity
@DiscriminatorValue("BUYER")
public class BuyerMember extends Member { ... }
```

**작업 목록:**
- [ ] `Member` 추상 클래스로 전환, 공통 필드만 유지
- [ ] `CustomerMember`, `AgentMember`, `SellerMember`, `BuyerMember` 클래스 생성
- [ ] 기존 Embedded 클래스의 필드를 각 서브클래스로 이동
- [ ] `MemberRepository` → 역할별 Repository 분리 또는 타입 조회 메서드 추가
- [ ] 서비스 레이어에서 `(AgentMember) member` 캐스팅 사용하도록 수정

---

### 1-2. DeletedMember 제거

**현재 문제:** `Member`와 완전히 동일한 구조를 가진 `DeletedMember` 엔티티가 별도로 존재 → DRY 위반

**변경 방향:**
- `DeletedMember` 엔티티 삭제
- `Member.deletedAt IS NOT NULL` 조회로 대체
- 아카이브 조회가 필요하면 별도 Repository 메서드 추가

```java
// DeletedMemberRepository 대신
public interface MemberRepository extends JpaRepository<Member, Long> {
    @Query("SELECT m FROM Member m WHERE m.deletedAt IS NOT NULL")
    List<Member> findAllDeleted();
}
```

**작업 목록:**
- [ ] `DeletedMember.java` 삭제
- [ ] `DeletedMemberRepository.java` 삭제
- [ ] 참조하는 서비스 코드 `Member` 기반으로 리라이트

---

### 1-3. 소프트 딜리트 전략 통일

**현재 혼재 패턴:**

| 패턴 | 사용 엔티티 | 문제 |
|------|------------|------|
| `@FilterDef + @Filter + @SQLDelete` | Member, Property | 명시적 활성화 필요 → 누락 위험 |
| `@SQLRestriction + @SQLDelete` | Admin, Department | 자동 적용으로 안전 |
| 없음 | 나머지 대부분 | 소프트 딜리트 불가 |

**통일 방향:** 모든 엔티티를 `@SQLRestriction + @SQLDelete` 패턴으로

```java
// BaseEntity 공통 적용은 불가 (엔티티마다 테이블명이 다름)
// 각 엔티티에 개별 선언

@SQLRestriction("deleted_at IS NULL")
@SQLDelete(sql = "UPDATE {테이블명} SET deleted_at = NOW() WHERE id = ?")
public class SomeEntity extends BaseEntity { ... }
```

**작업 목록:**
- [ ] `Member`, `Property`의 `@FilterDef/@Filter` 제거 → `@SQLRestriction`으로 교체
- [ ] 소프트 딜리트가 필요한데 없는 엔티티에 어노테이션 추가
- [ ] `delete_persistent_at` (하드 딜리트용) 필드를 `BaseEntity`로 올릴지 검토

---

## Phase 2 — 관계 정합성 (데이터 무결성)

### 2-1. Long FK → @ManyToOne 전환

**대상 목록:**

| 엔티티 | 잘못된 필드 | 올바른 형태 |
|--------|------------|-------------|
| `Property` | `Long adminId` | `@ManyToOne(fetch=LAZY) Admin admin` |
| `Property` | `Long exclusiveAgentId` | `@ManyToOne(fetch=LAZY) AgentMember exclusiveAgent` |
| `Purchase` | `Long memberId` | `@ManyToOne(fetch=LAZY) Member member` |
| `Notification` | `Long memberId` | `@ManyToOne(fetch=LAZY) Member member` |
| `Thumbnail` | `Long agentId` | `@ManyToOne(fetch=LAZY) AgentMember agent` |
| `PropertyMember` | `Long propertyId`, `Long memberId` | `@ManyToOne` 각각 |
| `CustomerProperties` | `Long adminId` | `@ManyToOne(fetch=LAZY) Admin admin` |
| `AgentProperties` | `Long adminId` | `@ManyToOne(fetch=LAZY) Admin admin` |
| `SellerProperties` | `Long adminId` | `@ManyToOne(fetch=LAZY) Admin admin` |
| `PropertyRequest` | `Long approvedByAdminId` | `@ManyToOne(fetch=LAZY) Admin approvedBy` |
| `PropertyRequest` | `Long createdPropertyId` | `@ManyToOne(fetch=LAZY) Property createdProperty` |

**작업 목록:**
- [ ] 각 엔티티의 Long FK 필드를 @ManyToOne으로 교체
- [ ] DB 마이그레이션 스크립트 작성 (FK constraint 추가)
- [ ] 관련 쿼리/서비스 코드에서 ID 직접 참조 → 객체 참조로 수정

---

### 2-2. PropertyMember 재설계

**현재:**
```java
public class PropertyMember extends BaseEntity {
    private Long propertyId;  // FK지만 관계 없음
    private Long memberId;    // FK지만 관계 없음
}
```

**변경 방향:**
```java
public class PropertyMember extends BaseEntity {
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "property_id")
    private Property property;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    // 관계 메타데이터가 있으면 추가
    // private PropertyMemberRole role;
}
```

---

### 2-3. Cascade 정책 정리

**원칙:**

| 상황 | 설정 |
|------|------|
| 부모 저장 시 자식도 저장 | `cascade = PERSIST` |
| 부모 삭제 시 자식도 삭제 | `cascade = REMOVE, orphanRemoval = true` |
| 자식이 독립적으로 존재 가능 | cascade 없음 |
| `cascade = ALL` | **사용 금지** (의도치 않은 삭제 위험) |

**수정 대상:**

```java
// PropertyWorkLog (현재 cascade=ALL → 위험)
@OneToMany(cascade = {CascadeType.PERSIST, CascadeType.REMOVE}, orphanRemoval = true)
private List<PropertyWorkLogDetail> details;

// Thumbnail (cascade=ALL → UploadFile까지 삭제됨)
@OneToMany(cascade = CascadeType.REMOVE, orphanRemoval = true)
private List<UploadFile> thumbnails;
```

---

## Phase 3 — 성능 최적화

### 3-1. EAGER 페치 → LAZY 전환

**수정 대상:**

```java
// Property.java
@ManyToOne(fetch = FetchType.LAZY)  // EAGER → LAZY
private Category bigCategory;

// Admin.java
@ManyToOne(fetch = FetchType.LAZY)  // EAGER → LAZY
private Department department;

@ManyToOne(fetch = FetchType.LAZY)  // EAGER → LAZY
private Permission permission;

@ManyToOne(fetch = FetchType.LAZY)  // EAGER → LAZY
private JobGrade jobGrade;

// Category.java
@ElementCollection(fetch = FetchType.LAZY)  // EAGER → LAZY
private Set<MenuType> menuTypes;
```

변경 후 필요한 곳은 JPQL fetch join으로 명시적 로딩:
```java
@Query("SELECT a FROM Admin a JOIN FETCH a.department JOIN FETCH a.permission WHERE a.id = :id")
Optional<Admin> findByIdWithDetails(@Param("id") Long id);
```

---

### 3-2. 인덱스 추가

현재 `@Index` 어노테이션 전무. 최소 인덱스 추가:

```java
// Property
@Table(name = "property", indexes = {
    @Index(name = "idx_property_status", columnList = "status"),
    @Index(name = "idx_property_admin_id", columnList = "admin_id"),
    @Index(name = "idx_property_deleted_at", columnList = "deleted_at")
})

// Member
@Table(name = "member", indexes = {
    @Index(name = "idx_member_email", columnList = "email", unique = true),
    @Index(name = "idx_member_type", columnList = "type"),
    @Index(name = "idx_member_deleted_at", columnList = "deleted_at")
})

// Notification
@Table(name = "notification", indexes = {
    @Index(name = "idx_notification_member_id", columnList = "member_id"),
    @Index(name = "idx_notification_is_read", columnList = "is_read")
})
```

---

## Phase 4 — 구조 간소화

### 4-1. Information 래퍼 엔티티 제거

**현재 불필요한 계층:**
```
Property
├── @ManyToOne AddressInformation → @Embedded AddressProperties
├── @ManyToOne PriceInformation   → @Embedded PriceProperties
├── @ManyToOne RegisterInformation → @Embedded RegisterProperties
└── @ManyToOne TemplateInformation → @Embedded TemplateProperties
```

**변경 방향:**

```java
// 1:1 관계인 것들 → @Embedded로 직접 내장
public class Property extends BaseEntity {
    @Embedded
    private AddressProperties address;

    @Embedded
    private PriceProperties price;

    @Embedded
    private RegisterProperties register;

    @Embedded
    private TemplateProperties template;

    // 1:N인 것은 @Embeddable 컬렉션으로
    @ElementCollection
    @CollectionTable(name = "property_floor")
    private List<FloorProperties> floors;

    @ElementCollection
    @CollectionTable(name = "property_land")
    private List<LandProperties> lands;
}
```

**제거 대상 엔티티:**
- [ ] `AddressInformation.java`
- [ ] `PriceInformation.java`
- [ ] `RegisterInformation.java`
- [ ] `TemplateInformation.java`
- [ ] `FloorInformation.java` → `@ElementCollection`으로 대체
- [ ] `LedgeInformation.java` → `@ElementCollection`으로 대체
- [ ] 관련 Repository 4개 삭제

---

### 4-2. Permission 재설계

**현재 문제:**
```java
public class Permission extends BaseEntity {
    private Boolean maemoolActive;
    private Boolean canManageMaemool;
    private Boolean interestActive;
    private Boolean canManageInterestGroup;
    // ... boolean 67개
}
```

**권장: JSON 컬럼으로 전환 (MariaDB 5.7.8+ 지원)**

```java
@Entity
public class Permission extends BaseEntity {
    private String name;
    private String description;

    @Convert(converter = PermissionMapConverter.class)
    @Column(columnDefinition = "JSON")
    private Map<String, Boolean> features = new HashMap<>();

    public boolean hasPermission(String featureKey) {
        return Boolean.TRUE.equals(features.get(featureKey));
    }
}

// 권한 키는 enum 상수로 관리
public enum PermissionKey {
    MAEMOOL_ACTIVE, CAN_MANAGE_MAEMOOL,
    INTEREST_ACTIVE, CAN_MANAGE_INTEREST_GROUP,
    // ...
}
```

---

## Phase 5 — 마무리

### 5-1. Thumbnail @JoinTable 수정

```java
// 현재 (잘못된 사용 - @OneToMany에 @JoinTable)
@OneToMany(cascade = CascadeType.ALL)
@JoinTable(name = "thumbnail_images", ...)
private List<UploadFile> thumbnails;

// 변경
@OneToMany(cascade = {PERSIST, REMOVE}, orphanRemoval = true)
@JoinColumn(name = "thumbnail_id")
private List<UploadFile> thumbnails;
```

### 5-2. Validation 어노테이션 추가

서비스 경계 엔티티에 최소한의 검증:

```java
public class Member extends BaseEntity {
    @NotBlank
    @Email
    @Column(nullable = false, unique = true)
    private String email;

    @NotBlank
    @Column(nullable = false)
    private String password;
}
```

---

## 실행 순서 요약

```
Phase 1  Member 분리 + DeletedMember 제거 + 소프트딜리트 통일
    │
    ▼
Phase 2  Long FK → @ManyToOne + Cascade 정리
    │
    ▼
Phase 3  EAGER→LAZY + 인덱스 추가
    │
    ▼
Phase 4  Information 래퍼 제거 + Permission 재설계
    │
    ▼
Phase 5  Thumbnail 수정 + Validation 추가
```

> **주의:** Phase 1~2 완료 전에는 `ddl-auto: validate`로 전환하지 말 것.
> 각 Phase 완료 후 통합 테스트 및 SQL 마이그레이션 스크립트 작성 필요.
