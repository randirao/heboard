# heboard – 자유 게시판 서비스

> **배포 URL**: [배포된 서비스 URL]  
> **테스트 계정**: ID: `test@example.com` / PW: `test1234` (선택사항)

---

## 📌 프로젝트 소개

heboard는 gc-board 수업 프로젝트를 기반으로 만든 자유 게시판 서비스입니다.  
단순 CRUD 구현에서 끝나는 것이 아니라, 실제 서비스 상황을 가정해 기존 코드의 부족한 점을 직접 분석하고 개선하는 것을 목표로 삼았습니다.  
특히 게시글 수가 많아지는 환경에서 필요한 **정렬·검색 기능 개선**과 **커서 기반 무한 스크롤**을 중심으로 기능을 확장했습니다.

- **개발 기간**: 2024.12.01 ~ 2024.12.18
- **개발 인원**: 1인 (개인 프로젝트)

---

## 🔍 개선 사항

### 기존 코드의 문제점

| 문제점 | 개선 방법 |
|--------|-----------|
| 게시글 목록이 최신순 정렬만 가능해 정보 탐색이 어려움 | 조회수순·댓글순 등 다양한 정렬 조건 추가 |
| 검색 기능이 단순해 특정 게시글을 빠르게 찾기 어려움 | 제목/내용/작성자 단위로 세분화된 검색 적용 |
| 페이지 번호 기반 페이징은 스크롤 UX가 자연스럽지 않음 | lastId 기반 커서 페이징으로 전환하여 무한 스크롤 구현 |

---

## 개선 결과

### **[개선 1: 커서 기반 무한 스크롤 적용]**

- **개선 전**: 기존 OFFSET 기반 페이징은 페이지 번호가 커질수록 성능 저하가 발생하고, 모바일 환경에서 스크롤이 끊기는 등 UX의 자연스러움이 떨어짐
- **개선 후**: `lastId`를 기준으로 데이터를 조회하는 커서 페이징을 적용해 스크롤 동작이 부드러워지고, 대용량 데이터에서도 성능 저하가 크게 줄어듦

### **[개선 2: 정렬·검색 기능 강화]**

- **개선 전**: 최신순 정렬만 가능하며, 제목/내용/작성자가 통합 검색되어 원하는 게시글을 정확하게 찾기 어려움
- **개선 후**: 조회수순·댓글순 정렬을 추가하고 제목/내용/작성자별로 조건을 분리한 검색 기능을 제공해 게시글 탐색 효율이 크게 향상됨

---

## ✨ 주요 기능

### 1. 사용자 인증
- 회원가입 / 로그인 / 로그아웃
- JWT 토큰 기반 인증
- 작성자 본인만 수정·삭제 가능

### 2. 게시글 관리
- 게시글 CRUD
- 최신·조회수·댓글순 정렬
- 커서 기반 무한 스크롤
- 제목/내용/작성자 검색

### 3. 댓글 기능
- 댓글 CRUD
- 단일 구조의 기본 댓글 기능 제공

---

## 🛠️ 기술 스택

### Backend
- Java 21
- Spring Boot 3.3.2
- Spring Data JPA
- MySQL 8.0
- Spring Security + JWT

### Frontend
- 사용 예정 (React 기반)

### Deployment
- Backend: Render 또는 Railway
- Frontend: Vercel 또는 Netlify
- Database: PlanetScale 또는 AWS RDS

---

## 📂 프로젝트 구조

```
├── backend/
│   ├── src/main/java/com/example/heboard/
│   │   ├── domain/
│   │   │   ├── article/
│   │   │   └── comment/
│   │   ├── auth/
│   │   ├── user/
│   │   └── global/
│   ├── src/main/resources/
│   └── build.gradle
└── frontend/
├── src/
└── package.json
```

---

## 🔗 API 명세

### 인증

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/auth/signup` | 회원가입 |
| POST | `/api/auth/login`  | 로그인 |

### 게시글

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/articles?lastId={id}&size={size}` | 게시글 목록 조회(무한 스크롤) |
| GET | `/api/articles/{id}` | 게시글 상세 조회 |
| POST | `/api/articles` | 게시글 작성 |
| PUT | `/api/articles/{id}` | 게시글 수정 |
| DELETE | `/api/articles/{id}` | 게시글 삭제 |

### 댓글

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/articles/{articleId}/comments` | 댓글 목록 조회 |
| POST | `/api/comments` | 댓글 작성 |
| DELETE | `/api/comments/{id}` | 댓글 삭제 |

---

## 💻 로컬 실행 방법

### 1) 레포지토리 클론
### 1) 레포지토리 클론
```bash
git clone https://github.com/your-username/your-repo.git
cd your-repo
```

### 2) 백엔드 실행
```bash
cd backend

# application.yml 설정
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/heboard
    username: root
    password: your-password

./gradlew bootRun
```