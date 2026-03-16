# 🏢 HNT APP - 가맹점 관리 시스템

> 본사와 가맹점주 간의 정책 / 정산 / 재고 / 출퇴근을 통합 관리하는 시스템

![Version](https://img.shields.io/badge/version-v1.1.0-blue)
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
- 📋 정책 관리 (가맹점별 정책 등록 / 수정)
- 💰 정산 관리 (월별 정산 내역 관리)
- 📦 재고 관리 (상품별 재고 현황 관리)
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
feat/auth       → 로그인/인증 API (예정)
feat/franchise  → 가맹점 API (예정)
feat/policy     → 정책 API (예정)
feat/settlement → 정산 API (예정)
feat/inventory  → 재고 API (예정)
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
├── config                  # 설정 클래스
├── common
│   ├── dto                 # 공통 DTO
│   ├── exception           # 예외 처리
│   └── util                # 유틸리티
└── domain
    ├── user                # 사용자
    ├── franchise           # 가맹점
    ├── policy              # 정책
    ├── settlement          # 정산
    ├── inventory           # 재고
    ├── item                # 상품 마스터
    ├── inquiry             # 문의
    ├── notice              # 공지사항
    ├── changelog           # 변경 이력
    └── attendance          # 출퇴근
```

---

## 🗄 DB 설계

| 테이블 | 설명 |
|--------|------|
| `users` | 사용자 (관리자/가맹점주/직원) |
| `franchises` | 가맹점 기본 정보 |
| `policies` | 가맹점별 정책 |
| `settlements` | 가맹점별 월간 정산 |
| `inventories` | 가맹점별 재고 현황 |
| `items` | 상품 마스터 |
| `inquiries` | 문의/답변 |
| `notices` | 공지사항 |
| `change_logs` | 수정 이력 |
| `attendances` | 출퇴근 기록 |

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
      ddl-auto: create
    show-sql: true
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
```

---

### 3단계
오른쪽 상단 **Commit changes** 클릭
```
Commit message: docs: README.md v1.1.0 작성
→ Commit changes 클릭
