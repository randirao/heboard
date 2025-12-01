# [프로젝트 제목]

> **배포 URL**: [배포된 서비스 URL]
> **테스트 계정**: ID: `test@example.com` / PW: `test1234` (선택사항)

## 📌 프로젝트 소개

[프로젝트에 대한 간단한 설명을 작성하세요]

- **개발 기간**: 2024.XX.XX ~ 2024.12.18
- **개발 인원**: 1인 (개인 프로젝트)

---

## 🔍 개선 사항

### 기존 코드의 문제점

| 문제점 | 개선 방법 |
|--------|----------|
| [예: 일관성 없는 예외 처리] | @ControllerAdvice를 활용한 전역 예외 처리 구현 |
| [문제점 2] | [개선 방법 2] |

### 개선 결과

**[개선 1: 예: 전역 예외 처리]**

- **개선 전**: 모든 예외가 500 에러로 반환되어 클라이언트가 원인 파악 불가
- **개선 후**: 비즈니스 예외별로 적절한 HTTP 상태 코드(400, 404 등)와 명확한 에러 메시지 제공

---

## ✨ 주요 기능

### 1. 사용자 인증
- 회원가입 / 로그인 / 로그아웃
- JWT 토큰 기반 인증

### 2. 게시글 관리
- 게시글 CRUD
- [선택한 심화 기술: 커서 기반 무한 스크롤]

### 3. 댓글 기능
- 댓글 CRUD
- [선택한 심화 기술: 계층형 댓글 또는 재귀적 삭제]

---

## 🛠️ 기술 스택

### Backend
- Java 21
- Spring Boot 3.3.2
- Spring Data JPA
- MySQL 8.0
- Spring Security + JWT

### Frontend
- [사용한 프레임워크: React / Vue / 기타]
- [스타일링 도구: Tailwind CSS / 기타]

### Deployment
- Backend: [AWS EC2 / Render / Railway]
- Frontend: [Vercel / Netlify]
- Database: [AWS RDS / PlanetScale]

---

## 📂 프로젝트 구조
```
├── backend/
│   ├── src/main/java/com/example/board/
│   │   ├── domain/
│   │   │   ├── article/
│   │   │   └── comment/
│   │   └── global/
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
| POST | `/api/auth/login` | 로그인 |

### 게시글

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/articles?lastId={id}&size={size}` | 게시글 목록 조회 |
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

### 1. 레포지토리 클론
```bash
git clone https://github.com/your-username/your-repo.git
cd your-repo

### 2. 백엔드 실행
```bash
cd backend

# application.yml에 DB 정보 설정
# spring.datasource.url=jdbc:mysql://localhost:3306/board
# spring.datasource.username=root
# spring.datasource.password=your-password

./gradlew bootRun
```

### 3. 프론트엔드 실행
```bash

cd backend

# application.yml에 DB 정보 설정
# spring.datasource.url=jdbc:mysql://localhost:3306/board
# spring.datasource.username=root
# spring.datasource.password=your-password

./gradlew bootRun

cd frontend
npm install

# .env 파일에 API URL 설정
# VITE_API_URL=http://localhost:8080

npm run dev
```
---

## 🎥 시연 영상

[YouTube 링크]

---

## 📚 참고 자료

- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [React Documentation](https://react.dev/) (해당되는 경우)