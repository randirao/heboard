# heboard – 자유 게시판 서비스

> **배포 URL**: [배포된 서비스 URL]  
> **테스트 계정**: ID: `test@example.com` / PW: `test1234` (선택사항)

---

## 📌 프로젝트 소개

heboard는 gc-board 수업 프로젝트를 기반으로 만든 자유 게시판 서비스입니다.  
단순 CRUD 구현에서 끝나는 것이 아니라, 실제 서비스 상황을 가정해 기존 코드의 부족한 점을 직접 분석하고 개선하는 것을 목표로 삼았습니다.  
특히 게시글 수가 많아지는 환경에서 필요한 **정렬·검색 기능 개선**과 **커서 기반 무한 스크롤**을 중심으로 기능을 확장했습니다.

- **개발 기간**: 2024.12.01 ~ 2024.12.13
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

## 🎯 개선 결과 및 적용 기술

### **[개선 1: 커서 기반 무한 스크롤 적용]**

#### 문제 진단
- **OFFSET 기반 페이징의 성능 문제**: 페이지 번호가 커질수록 데이터베이스가 건너뛸 행 수가 증가하여 응답 속도가 느려짐
  - 예: 10,000번째 페이지 조회 시 `OFFSET 100000`을 사용하면 DB가 100,000개 행을 스캔한 후 버려야 함
- **모바일 UX 문제**: 페이지 번호 클릭 방식은 스크롤 기반 앱과 맞지 않아 사용성이 떨어짐

#### 개선 방법
- **커서 기반 페이지네이션 구현**: `WHERE id < lastId ORDER BY ... LIMIT size` 쿼리를 사용해 인덱스를 활용한 빠른 조회
- **복합 정렬 지원**: 정렬 기준(조회수, 댓글 수)에 따라 동적 쿼리 생성

#### 기술적 이점
- **성능 개선**: 인덱스를 활용하여 페이지 위치와 관계없이 일정한 조회 속도 유지
- **확장성**: 게시글이 수만 건 이상 쌓여도 성능 저하 없음
- **UX 향상**: 무한 스크롤 구현으로 모바일 환경에서 자연스러운 사용자 경험 제공

**구현 코드** (ArticleServiceImpl.java:186):
```java
List<Article> articles = articleRepository.findArticlesWithCursor(
    lastId, pageable, sortType.name().toLowerCase()
);
```

---

### **[개선 2: 정렬·검색 기능 강화]**

#### 문제 진단
- **제한적인 정렬 옵션**: 최신순만 가능하여 인기 게시글이나 댓글이 많은 게시글을 찾기 어려움
- **통합 검색의 한계**: 제목/내용/작성자가 모두 섞여 검색되어 정확한 검색이 불가능

#### 개선 방법
- **다중 정렬 기준 구현**: Enum 타입(`ArticleSortType`)으로 latest, views, comments 정렬 지원
- **세분화된 검색**: `searchType` 파라미터로 제목/내용/작성자를 독립적으로 또는 조합하여 검색

#### 기술적 이점
- **사용자 맞춤 검색**: 원하는 조건만 선택하여 검색 정확도 향상
- **유연한 쿼리 구성**: JPA를 활용한 동적 쿼리로 다양한 검색 조건 조합 가능
- **인덱스 최적화**: 검색 조건별로 적절한 인덱스 활용

**구현 코드** (ArticleController.java:338):
```java
@RequestParam(value = "sort", defaultValue = "latest") String sort,
@RequestParam(value = "searchType", required = false) String searchType,
@RequestParam(value = "keyword", required = false) String keyword
```

---

### **[개선 3: 대댓글 기능 구현]**

#### 문제 진단
- **평면 구조의 한계**: 기존 댓글은 단순 나열식으로 댓글 간 연결이 없어 대화 맥락 파악이 어려움

#### 개선 방법
- **Self-Join 구조 적용**: Comment 엔티티에 `parent` 필드를 추가하여 계층형 댓글 지원

#### 기술적 이점
- **확장 가능한 구조**: 추후 N차 대댓글까지 확장 가능
- **효율적인 조회**: 한 번의 쿼리로 부모-자식 댓글 관계 파악

**구현 코드** (Comment.java:25-27):
```java
@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "parent_id")
private Comment parent;
```

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
- 댓글 CRUD (작성, 수정, 삭제, 조회)
- **대댓글 기능** (계층형 댓글 구조 지원)
- 페이지 기반 댓글 목록 조회

---

## 🛠️ 기술 스택

### Backend
- Java 21
- Spring Boot 3.3.2
- Spring Data JPA
- PostgreSQL 15+ (Render/Local)
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
| GET | `/api/articles` | 게시글 목록 조회 (커서 기반 무한 스크롤) |
| GET | `/api/articles/{id}` | 게시글 상세 조회 |
| POST | `/api/articles` | 게시글 작성 |
| PATCH | `/api/articles/{id}` | 게시글 수정 |
| DELETE | `/api/articles/{id}` | 게시글 삭제 |

**게시글 목록 조회 파라미터**:
- `lastId` (optional): 커서 ID (이전 조회의 마지막 게시글 ID)
- `size` (default: 10): 한 번에 조회할 게시글 수 (1-50)
- `sort` (default: latest): 정렬 기준 (`latest`, `views`, `comments`)
- `searchType` (optional): 검색 대상 (`title`, `content`, `author` - 콤마로 구분하여 다중 선택 가능)
- `keyword` (optional): 검색어

**응답 예시**:
```json
{
  "posts": [
    {
      "articleId": 101,
      "title": "게시글 제목",
      "contentPreview": "내용 미리보기...",
      "writerId": 3,
      "writerName": "홍길동",
      "viewCount": 123,
      "commentCount": 5,
      "createdAt": "2024-12-08T12:34:56"
    }
  ],
  "nextCursor": 98,
  "hasMore": true
}
```

### 댓글

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/articles/{articleId}/comments` | 댓글 목록 조회 (페이지 기반) |
| POST | `/api/comments` | 댓글 작성 (대댓글 포함) |
| PATCH | `/api/comments/{id}` | 댓글 수정 |
| DELETE | `/api/comments/{id}` | 댓글 삭제 |

**댓글 작성 요청 예시**:
```json
{
  "articleId": 10,
  "content": "댓글 내용",
  "parentId": null
}
```
> 대댓글인 경우 `parentId`에 부모 댓글 ID를 지정

---

## 💻 로컬 실행 방법

### 1) 사전 준비
- **Java 21** 설치
- **PostgreSQL 15+** 설치 및 실행
- **Git** 설치

### 2) 레포지토리 클론
```bash
git clone https://github.com/your-username/heboard.git
cd heboard
```

### 3) 데이터베이스 설정
PostgreSQL에서 데이터베이스를 생성합니다:
```sql
CREATE DATABASE heboard;
CREATE USER heboard WITH PASSWORD 'your-password';
GRANT ALL PRIVILEGES ON DATABASE heboard TO heboard;
```

### 4) 환경 변수 설정
`src/main/resources/application.yml` 파일을 수정합니다:
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/heboard
    username: heboard
    password: your-password

  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true

  mail:
    host: smtp.gmail.com
    port: 587
    username: your-email@gmail.com
    password: your-app-password
```

### 5) 애플리케이션 실행
```bash
# Gradle 빌드 및 실행
./gradlew bootRun

# 또는 JAR 파일로 빌드 후 실행
./gradlew build
java -jar build/libs/heboard-0.0.1-SNAPSHOT.jar
```

### 6) 접속 확인
- **API 서버**: http://localhost:8080
- **Swagger UI**: http://localhost:8080/swagger-ui.html

---

## 📊 데이터 흐름

### 게시글 목록 조회 (커서 기반)
```
[Client] → GET /api/articles?lastId=100&size=10&sort=views
         ↓
[Controller] → 파라미터 검증 및 파싱
         ↓
[Service] → ArticleRepository.findArticlesWithCursor()
         ↓
[Repository] → WHERE id < 100 ORDER BY viewCount DESC, id DESC LIMIT 10
         ↓
[Response] → { posts: [...], nextCursor: 90, hasMore: true }
```

### 대댓글 작성
```
[Client] → POST /api/comments { articleId: 1, parentId: 5, content: "..." }
         ↓
[Controller] → JWT 인증 확인 → 사용자 ID 추출
         ↓
[Service] → 1. Article 존재 확인
           2. Parent Comment 존재 확인 (parentId가 있는 경우)
           3. Comment 저장 (parent 연결)
         ↓
[Response] → { id: 10, articleId: 1, parentId: 5, ... }
```

---

## 🔧 트러블슈팅

### 포트 충돌 에러
```bash
# 8080 포트를 사용 중인 프로세스 확인
lsof -i :8080

# 또는 application.yml에서 포트 변경
server:
  port: 8081
```

### 데이터베이스 연결 실패
- PostgreSQL 서비스가 실행 중인지 확인
- `application.yml`의 데이터베이스 정보가 정확한지 확인
- 방화벽 설정 확인

### JWT 토큰 관련 에러
- `application.yml`에 JWT Secret 키가 설정되어 있는지 확인
- 토큰 만료 시간 설정 확인

---

## 📝 라이센스

이 프로젝트는 개인 학습 목적으로 제작되었습니다.
