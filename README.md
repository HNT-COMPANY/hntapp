# 🏢 HNT APP - 가맹점 관리 시스템

> 본사와 가맹점주 간의 정책 / 정산 / 재고 / 출퇴근을 통합 관리하는 시스템

![Version](https://img.shields.io/badge/version-v1.3.0-blue)
![Java](https://img.shields.io/badge/Java-17-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.0.3-green)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-18.3-blue)
![License](https://img.shields.io/badge/license-Private-red)

---

## 📋 목차
- [프로젝트 개요](#프로젝트-개요)
- [기술 스택](#기술-스택)
- [브랜치 전략](#브랜치-전략)
- [패키지 구조](#패키지-구조)
- [DB 설계](#db-설계)
- [API 엔드포인트](#api-엔드포인트)
- [환경 설정](#환경-설정)
- [실행 방법](#실행-방법)
- [버전 히스토리](#버전-히스토리)

---

## 📌 프로젝트 개요

HNT APP은 본사와 가맹점 간의 업무를 효율적으로 관리하기 위한 통합 관리 시스템입니다.

### 대상 사용자
| 역할 | 설명 |
|------|------|
| `ADMIN` | 본사 관리자 - 모든 권한 |
| `FRANCHISEE` | 가맹점주 - 자기 가맹점 관리 |
| `STAFF` | 가맹점 직원 - 출퇴근/재고 조회 |
| `CUSTOMER` | 고객 - 추후 확장 예정 |

### 주요 기능
- 🏪 가맹점 관리 (등록 / 수정 / 상태 관리)
- 👤 사용자 관리 (가맹점주 권한 부여 / 회수)
- 📋 정책 관리 (가맹점별 정책 등록 / 수정)
- 💰 정산 관리 (월별 정산 내역 관리)
- 📦 창고 관리 (휴대폰 모델 / 재고 배분 / 차감)
- 📄 개통 전표 (판매 전표 작성 / 승인 / 보류)
- 📢 공지사항 (본사 → 가맹점 공지)
- 💬 문의 관리 (가맹점 문의 / 본사 답변)
- 🕐 출퇴근 관리 (GPS 기반 출퇴근 체크)

---

## 🛠 기술 스택

### Backend
| 기술 | 버전 | 용도 |
|------|------|------|
| Java | 17 (LTS) | 메인 언어 |
| Spring Boot | 4.0.3 | 백엔드 프레임워크 |
| Spring Data JPA | 4.0.3 | DB ORM |
| Spring Security | 7.0.3 | 인증/인가 |
| Hibernate | 7.2.4 | JPA 구현체 |
| Lombok | 1.18.42 | 코드 간소화 |

### Database
| 기술 | 버전 | 용도 |
|------|------|------|
| PostgreSQL | 18.3 | 메인 DB |
| HikariCP | 7.0.2 | DB 커넥션 풀 |

### DevOps
| 기술 | 버전 | 용도 |
|------|------|------|
| Docker | Latest | PostgreSQL 컨테이너 실행 |
| Gradle | 8.x | 빌드 툴 |
| GitHub | - | 버전 관리 |

### 개발 도구
| 도구 | 용도 |
|------|------|
| IntelliJ IDEA | 백엔드 개발 IDE |
| Android Studio | 안드로이드 앱 개발 |
| Docker Desktop | 컨테이너 관리 |
| Postman | API 테스트 |
| DBeaver | DB 관리 |
| dbdiagram.io | ERD 설계 |

### 추후 개발 예정
| 기술 | 용도 |
|------|------|
| JavaFX | 데스크톱 앱 |
| Android (Retrofit2) | 안드로이드 앱 |
| Supabase | 운영 DB 호스팅 |
| Railway.app | 백엔드 서버 배포 |

---

## 🌿 브랜치 전략
```
main            → 최종 배포용 (직접 push 금지)
develop         → 개발 통합 브랜치
feat/entity     → Entity 작성 (완료)
feat/auth       → 로그인/인증 API (완료)
feat/franchise  → 가맹점 API (완료)
feat/user       → 사용자/권한 API (완료)
feat/warehouse  → 창고/재고 API (완료)
feat/activation → 개통 전표 API (완료)
feat/policy     → 정책 API (예정)
feat/settlement → 정산 API (예정)
feat/attendance → 출퇴근 API (예정)
```

### 작업 흐름
```
feat/* 작업 완료
→ develop 에 merge (PR)
→ 테스트 완료
→ main 에 merge (배포)
```

---

## 📁 패키지 구조
```
com.hnt.hntapp
├── config                  # 설정 클래스 (Security, JWT)
├── common
│   ├── dto                 # 공통 DTO (ApiResponse)
│   ├── exception           # 예외 처리
│   └── util                # 유틸리티
└── domain
    ├── user                # 사용자 (인증/권한)
    ├── franchise           # 가맹점
    ├── warehouse           # 창고/재고 (신규)
    │   ├── entity          # PhoneModel, PhoneStorage, PhoneColor, WarehouseStock
    │   ├── dto             # WarehouseDto
    │   ├── repository      # PhoneModelRepository, WarehouseStockRepository
    │   ├── service         # WarehouseService
    │   └── controller      # WarehouseController
    ├── activation          # 개통 전표 (신규)
    │   ├── entity          # Activation, Carrier, ActivationType, ActivationStatus
    │   ├── dto             # ActivationDto
    │   ├── repository      # ActivationRepository
    │   ├── service         # ActivationService
    │   └── controller      # ActivationController
    ├── policy              # 정책
    ├── settlement          # 정산
    ├── inventory           # 일반 재고 (소모품)
    ├── item                # 상품 마스터
    ├── inquiry             # 문의
    ├── notice              # 공지사항
    ├── changelog           # 변경 이력
    └── attendance          # 출퇴근
```

---

## 🗄 DB 설계

### 기존 테이블
| 테이블 | 설명 |
|--------|------|
| `users` | 사용자 (관리자/가맹점주/직원) |
| `franchises` | 가맹점 기본 정보 |
| `policies` | 가맹점별 정책 |
| `settlements` | 가맹점별 월간 정산 |
| `inventories` | 일반 재고 현황 (소모품) |
| `items` | 상품 마스터 |
| `inquiries` | 문의/답변 |
| `notices` | 공지사항 |
| `change_logs` | 수정 이력 |
| `attendances` | 출퇴근 기록 |

### 신규 테이블 (v1.3.0)
| 테이블 | 설명 |
|--------|------|
| `phone_models` | 휴대폰 모델 마스터 (제조사/모델명) |
| `phone_storages` | 용량 마스터 (128GB/256GB 등) |
| `phone_colors` | 컬러 마스터 (컬러명/HEX) |
| `warehouse_stocks` | 가맹점별 재고 수량 (공식qty/현재qty/상태) |
| `activations` | 개통 전표 (판매 1건 = 전표 1건) |

### 테이블 관계
```
phone_models
  └── phone_storages (1:N)
        └── phone_colors (1:N)
              └── warehouse_stocks (1:N, 가맹점별)
                    ← activations.phone_color_id (FK)
```

---

## 🔌 API 엔드포인트

### 인증 (`/api/auth`)
| Method | URL | 설명 | 권한 |
|--------|-----|------|------|
| `POST` | `/api/auth/login` | 로그인 (JWT 발급) | ALL |
| `POST` | `/api/auth/register` | 회원가입 | ALL |

### 사용자 (`/api/users`)
| Method | URL | 설명 | 권한 |
|--------|-----|------|------|
| `GET` | `/api/users` | 전체 사용자 목록 | ADMIN |
| `PATCH` | `/api/users/{id}/approve` | 사용자 승인 | ADMIN |
| `PATCH` | `/api/users/{id}/assign-franchisee` | 가맹점주 권한 부여 | ADMIN |
| `PATCH` | `/api/users/{id}/revoke-franchisee` | 가맹점주 권한 회수 | ADMIN |

### 가맹점 (`/api/franchises`)
| Method | URL | 설명 | 권한 |
|--------|-----|------|------|
| `GET` | `/api/franchises` | 전체 가맹점 목록 | ADMIN |
| `POST` | `/api/franchises` | 가맹점 등록 | ADMIN |
| `PUT` | `/api/franchises/{id}` | 가맹점 수정 | ADMIN |
| `PATCH` | `/api/franchises/{id}/status` | 상태 변경 | ADMIN |

### 창고/재고 (`/api/warehouse`)
| Method | URL | 설명 | 권한 |
|--------|-----|------|------|
| `GET` | `/api/warehouse/models` | 전체 모델 목록 | ALL |
| `POST` | `/api/warehouse/models` | 모델 등록 (용량/컬러 동시) | ADMIN |
| `PATCH` | `/api/warehouse/models/{id}/deactivate` | 모델 단종 처리 | ADMIN |
| `GET` | `/api/warehouse/stocks` | 전체 가맹점 재고 현황 | ADMIN |
| `GET` | `/api/warehouse/stocks/franchise/{id}` | 가맹점별 재고 조회 | ADMIN/FRANCHISEE |
| `POST` | `/api/warehouse/stocks/distribute` | 가맹점 배분 확정 | ADMIN |

### 개통 전표 (`/api/activations`)
| Method | URL | 설명 | 권한 |
|--------|-----|------|------|
| `POST` | `/api/activations` | 전표 저장 (임시저장/즉시제출) | FRANCHISEE/STAFF |
| `PATCH` | `/api/activations/{id}/submit` | 임시저장 → 제출 | FRANCHISEE/STAFF |
| `PATCH` | `/api/activations/{id}/resubmit` | 보류 후 재제출 | FRANCHISEE/STAFF |
| `GET` | `/api/activations/pending` | 검토 대기 목록 | ADMIN |
| `PATCH` | `/api/activations/{id}/approve` | 전표 승인 | ADMIN |
| `PATCH` | `/api/activations/{id}/reject` | 전표 보류 | ADMIN |
| `GET` | `/api/activations/franchise/{id}/daily` | 일별 전표 목록 | ADMIN/FRANCHISEE |
| `GET` | `/api/activations/franchise/{id}/monthly` | 월별 전표 목록 | ADMIN/FRANCHISEE |
| `GET` | `/api/activations/franchise/{id}/daily-summary` | 일 마감 요약 | ADMIN/FRANCHISEE |

---

## ⚙️ 환경 설정

### 필수 설치 목록
```
- JDK 17 (Eclipse Temurin)
- IntelliJ IDEA
- Docker Desktop
- Git
```

### application.yml 설정
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/hntdb
    username: postgres
    password: 1234
    driver-class-name: org.postgresql.Driver
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true
    properties:
      hibernate:
        format_sql: true

jwt:
  secret: hntapp-secret-key-2026-must-be-at-least-32-characters-long
  expiration: 86400000

server:
  port: 8080
```

---

## 🚀 실행 방법

### 1. PostgreSQL 실행 (Docker)
```bash
docker run --name hnt-db \
  -e POSTGRES_PASSWORD=1234 \
  -e POSTGRES_DB=hntdb \
  -e POSTGRES_USER=postgres \
  -p 5432:5432 \
  -d postgres
```

### 2. 프로젝트 클론
```bash
git clone https://github.com/HNT-COMPANY/hntapp.git
cd hntapp
```

### 3. 브랜치 선택
```bash
git checkout develop
```

### 4. Spring Boot 실행
```bash
./gradlew bootRun
```

---

## 📌 버전 히스토리

### v1.3.0 (2026-03-25)
- `warehouse` 도메인 신규 구현
  - PhoneModel / PhoneStorage / PhoneColor 모델 마스터 계층 구조
  - WarehouseStock 가맹점별 재고 수량 관리
  - StockStatus enum (NORMAL / LOW / OUT_OF_STOCK)
  - 재고 배분 확정 / 차감 / 복구 비즈니스 로직
  - REST API 6개 엔드포인트
- `activation` 도메인 신규 구현
  - 개통 전표 엔티티 (전표 양식 전 필드 반영)
  - Carrier enum (SK/KT/LG 거래처 자동 구분)
  - ActivationType enum (신규/기기변경/번호이동)
  - ActivationStatus enum (DRAFT/SUBMITTED/APPROVED/REJECTED)
  - 전표 제출 시 재고 자동 차감 연동
  - 전표 보류 시 재고 자동 복구 연동
  - 실마진 자동 계산 (마진 + 수수료 - 차감)
  - REST API 9개 엔드포인트
- `ddl-auto` create → update 변경

### v1.2.0 (2026-03-16)
- 가맹점주 권한 부여 / 회수 API 구현
- 관리자 사이드바 탭별 분리
- 가맹점 수정 다이얼로그 상태 변경 기능
- User 도메인 dirty checking 방식 리팩토링
- Franchise.updateOwnerName() 비즈니스 메서드 추가

### v1.1.0 (2026-03-16)
- 프로젝트 초기 설정
- 기술 스택 확정
- Entity 10개 작성
- Docker PostgreSQL 연동
- GitHub Organization (HNT-COMPANY) 생성
- Private 레포지토리 생성
- 브랜치 전략 수립

---

## 👥 Contributors

| 이름 | 역할 | GitHub |
|------|------|--------|
| 지혁 | 풀스택 개발 | @dev-hyuck |
