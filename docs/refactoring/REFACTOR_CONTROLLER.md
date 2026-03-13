# Refactoring: Controller
## 1. 문제상황
이전에 facade가 godclass가 되면서 해당 facade안의 비즈니스 로직단위로
usecase로 쪼개서 만들어 놓았다.
이때 facade의 역할이 그저 wrapper역할만 하고 있는데 이게 필요할 까란 생각이 들었다.

애초에 facade가 godclass가 된 이유에 대해서는 하나의 controller가 너무 크기 때문이다.
해당 문제는 facade하나를 주입받았기 때문에 인지하지 못했고 지금에서야 리펙토링을 진행하게 된다.

## 2. 목표
Controller의 분리
- 각 controller를 역할별로 분리를 한다.
- property의 crud만 담은 controller
- 읽기 조회 전용 controller
- 매물 상태값 변경 controller
- 파일 입출력 관련 controller


## 3. 변경 후 이점
- facade의 비대가 사라지게 된다.

## 4. trade-offs
- 파일 수가 증가

---

## 4. 헥사고날 패턴 적용 (Inbound Port)

Controller 분리 이후, 각 Controller가 `AdminPropertyFacade` 구체 클래스를 직접 주입받는 문제를 해결하기 위해 Inbound Port(UseCase 인터페이스)를 도입했다.

### 구조

```
Controller
  → UseCase 인터페이스 (Inbound Port)
      ← Application Service (구현체)
          → Domain Service / Repository (기존 레이어)
```

### 생성된 패키지

```
application/
├── port/in/property/
│   ├── status/        # TogglePublicUseCase, GetPropertyStatusUseCase,
│   │                  # UpdatePropertyStatusUseCase, ConfirmPropertyUseCase, RemoveConfirmUseCase
│   ├── core/          # CreatePropertyUseCase, UpdatePropertyUseCase,
│   │                  # GetPropertyDetailUseCase, GetUpdatePropertyUseCase,
│   │                  # GetPropertyInfoUseCase, DeletePropertyUseCase, FloorUpdateUseCase
│   ├── search/        # SearchPropertyUseCase, GetPropertyListUseCase,
│   │                  # GetPropertyByMapUseCase, GetPropertyStatUseCase
│   ├── file/          # DownloadCsvUseCase, DownloadExcelUseCase,
│   │                  # UploadExcelUseCase, UploadTaskNoteUseCase
│   └── advertisement/ # AssignAdvertisementUseCase, RemoveAdvertisementUseCase
└── service/property/
    ├── status/        # 구현체 5개
    ├── core/          # 구현체 7개
    ├── search/        # 구현체 4개
    ├── file/          # 구현체 4개
    └── advertisement/ # 구현체 2개
```

### 변경된 Controller

| Controller | 제거 | 추가 |
|---|---|---|
| AdminPropertyStatusController | AdminPropertyFacade | UseCase 인터페이스 5개 |
| AdminPropertyController | AdminPropertyFacade | UseCase 인터페이스 7개 |
| AdminPropertySearchController | AdminPropertyFacade | UseCase 인터페이스 4개 |
| AdminPropertyFileController | AdminPropertyFacade | UseCase 인터페이스 4개 |
| AdminPropertyAdvertisementController | AdminPropertyFacade | UseCase 인터페이스 2개 |

### AdminPropertyFacade 변화

Facade에서 각 Controller가 사용하던 메서드가 모두 제거되었다.
현재 Facade에 남아있는 것은 `AdminPropertyDeletedController`가 사용하는 메서드들 뿐이다.

### trade-offs 추가

- UseCase 인터페이스 + Service 구현체가 쌍으로 생성되어 파일 수가 크게 증가
- Inbound Port의 실질적 이점(테스트 용이성)은 Outbound Port에 비해 작음
- 단일 Spring REST API에서는 구체 클래스 직접 주입도 실용적인 선택이나, 학습 목적으로 전체 패턴을 적용함

