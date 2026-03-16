🏢 HNT APP - 가맹점 관리 시스템

본사와 가맹점주 간의 정책 / 정산 / 재고 / 출퇴근을 통합 관리하는 시스템

이미지 표시
이미지 표시
이미지 표시
이미지 표시
이미지 표시

📋 목차

프로젝트 개요
기술 스택
브랜치 전략
패키지 구조
DB 설계
환경 설정
실행 방법
버전 히스토리


📌 프로젝트 개요
HNT APP은 본사와 가맹점 간의 업무를 효율적으로 관리하기 위한 통합 관리 시스템입니다.
대상 사용자
역할설명ADMIN본사 관리자 - 모든 권한FRANCHISEE가맹점주 - 자기 가맹점 관리STAFF가맹점 직원 - 출퇴근/재고 조회CUSTOMER고객 - 추후 확장 예정
주요 기능

🏪 가맹점 관리 (등록 / 수정 / 상태 관리)
📋 정책 관리 (가맹점별 정책 등록 / 수정)
💰 정산 관리 (월별 정산 내역 관리)
📦 재고 관리 (상품별 재고 현황 관리)
📢 공지사항 (본사 → 가맹점 공지)
💬 문의 관리 (가맹점 문의 / 본사 답변)
🕐 출퇴근 관리 (GPS 기반 출퇴근 체크)


🛠 기술 스택
Backend
기술버전용도Java17 (LTS)메인 언어Spring Boot4.0.3백엔드 프레임워크Spring Data JPA4.0.3DB ORMSpring Security7.0.3인증/인가Hibernate7.2.4JPA 구현체Lombok1.18.42코드 간소화
Database
기술버전용도PostgreSQL18.3메인 DBHikariCP7.0.2DB 커넥션 풀
DevOps
기술버전용도DockerLatestPostgreSQL 컨테이너 실행Gradle8.x빌드 툴GitHub-버전 관리
개발 도구
도구용도IntelliJ IDEA백엔드 개발 IDEAndroid Studio안드로이드 앱 개발Docker Desktop컨테이너 관리PostmanAPI 테스트DBeaverDB 관리dbdiagram.ioERD 설계
추후 개발 예정
기술용도JavaFX데스크톱 앱Android (Retrofit2)안드로이드 앱Supabase운영 DB 호스팅Railway.app백엔드 서버 배포

🌿 브랜치 전략
main          → 최종 배포용 (직접 push 금지)
develop       → 개발 통합 브랜치
feat/entity   → Entity 작성 (현재)
feat/auth     → 로그인/인증 API (예정)
feat/franchise → 가맹점 API (예정)
feat/policy   → 정책 API (예정)
feat/settlement → 정산 API (예정)
feat/inventory → 재고 API (예정)
feat/attendance → 출퇴근 API (예정)
작업 흐름
feat/* 작업 완료
→ develop 에 merge (PR)
→ 테스트 완료
→ main 에 merge (배포)

📁 패키지 구조
com.hnt.hntapp
├── config                  # 설정 클래스
├── common
│   ├── dto                 # 공통 DTO
│   ├── exception           # 예외 처리
│   └── util                # 유틸리티
└── domain
├── user                # 사용자
│   ├── entity
│   ├── repository
│   ├── service
│   ├── controller
│   └── dto
├── franchise           # 가맹점
├── policy              # 정책
├── settlement          # 정산
├── inventory           # 재고
├── item                # 상품 마스터
├── inquiry             # 문의
├── notice              # 공지사항
├── changelog           # 변경 이력
└── attendance          # 출퇴근

🗄 DB 설계
테이블 목록
테이블설명users사용자 (관리자/가맹점주/직원)franchises가맹점 기본 정보policies가맹점별 정책settlements가맹점별 월간 정산inventories가맹점별 재고 현황items상품 마스터inquiries문의/답변notices공지사항change_logs수정 이력attendances출퇴근 기록

⚙️ 환경 설정
필수 설치 목록
- JDK 17 (Eclipse Temurin)
- IntelliJ IDEA
- Docker Desktop
- Git
  application.yml 설정
  yamlspring:
  datasource:
  url: jdbc:postgresql://localhost:5432/hntdb
  username: postgres
  password: 1234
  driver-class-name: org.postgresql.Driver
  jpa:
  hibernate:
  ddl-auto: create
  show-sql: true
  properties:
  hibernate:
  format_sql: true
  server:
  port: 8080

🚀 실행 방법
1. PostgreSQL 실행 (Docker)
   bashdocker run --name hnt-db \
   -e POSTGRES_PASSWORD=1234 \
   -e POSTGRES_DB=hntdb \
   -e POSTGRES_USER=postgres \
   -p 5432:5432 \
   -d postgres
2. 프로젝트 클론
   bashgit clone https://github.com/HNT-COMPANY/hntapp.git
   cd hntapp
3. 브랜치 선택
   bashgit checkout develop
4. Spring Boot 실행
   bash./gradlew bootRun
5. 실행 확인
   http://localhost:8080

📌 버전 히스토리
v1.1.0 (2026-03-16)

프로젝트 초기 설정
기술 스택 확정
Entity 10개 작성

users, franchises, policies, settlements
inventories, items, inquiries, notices
change_logs, attendances


Docker PostgreSQL 연동
GitHub Organization (HNT-COMPANY) 생성
Private 레포지토리 생성
브랜치 전략 수립 (main / develop / feat/*)


👥 Contributors
이름역할GitHub지혁풀스택 개발@dev-hyuck


📧 문의: GitHub Issues 를 통해 문의해주세요.