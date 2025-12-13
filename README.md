# heboard – 자유 게시판 서비스

> Spring Boot 기반 게시판 서비스 (개인 프로젝트, 2024.12.01 ~ 2024.12.13)

---

## 🔍 진단 및 개선 목표

### 기존 코드의 문제점과 개선 방향

| 문제점 | 개선 방법 |
|--------|-----------|
| OFFSET 기반 페이징으로 인한 성능 저하 | 커서 기반 무한 스크롤로 전환 |
| 최신순 정렬만 지원하여 정보 탐색이 제한적 | 조회수·댓글순 등 다양한 정렬 기준 추가 |
| 통합 검색으로 인한 검색 정확도 부족 | 제목/내용/작성자 단위 세분화 검색 |
| 평면 구조 댓글로 대화 맥락 파악 어려움 | Self-Join 기반 대댓글 구조 구현 |

---

## 🎯 적용 기술 및 구현 근거

### 1. 커서 기반 페이지네이션 (Cursor-based Pagination)

**구현 근거**:
- OFFSET 페이징은 페이지 번호가 커질수록 DB가 건너뛸 행을 모두 스캔해야 하므로 성능 저하 발생
- 예: 10,000번째 페이지 조회 시 `OFFSET 100000`은 100,000개 행을 스캔 후 버림

**기술적 이점**:
- `WHERE id < lastId` 쿼리로 인덱스를 활용한 빠른 조회
- 페이지 위치와 관계없이 일정한 조회 속도 유지
- 무한 스크롤 UX 구현으로 모바일 환경 최적화

**핵심 구현**:
```java
// ArticleServiceImpl.java
List<Article> articles = articleRepository.findArticlesWithCursor(
    lastId, pageable, sortType.name().toLowerCase()
);
```

### 2. 동적 검색 및 다중 정렬

**구현 근거**:
- 단일 정렬 기준으로는 다양한 사용자 요구 충족 불가
- 통합 검색은 원하지 않는 결과까지 포함하여 정확도 저하

**기술적 이점**:
- `searchType` 파라미터로 제목/내용/작성자 독립 검색
- Enum 기반 정렬 타입(`ArticleSortType`)으로 타입 안정성 확보
- JPA 동적 쿼리로 다양한 검색 조건 조합 가능

### 3. 계층형 댓글 구조 (Self-Join)

**구현 근거**:
- 평면 댓글 구조는 댓글 간 연결 관계 표현 불가

**기술적 이점**:
- `@ManyToOne` Self-Join으로 확장 가능한 대댓글 구조
- 추후 N차 대댓글까지 확장 가능

**핵심 구현**:
```java
// Comment.java
@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "parent_id")
private Comment parent;
```

---

## 📡 API 명세

### 인증

#### 회원가입
```
POST /api/auth/signup
```
**요청**:
```json
{
  "email": "user@example.com",
  "password": "password123",
  "nickname": "홍길동"
}
```
**응답**:
```json
{
  "userId": 1,
  "email": "user@example.com",
  "nickname": "홍길동"
}
```

#### 로그인
```
POST /api/auth/login
```
**요청**:
```json
{
  "email": "user@example.com",
  "password": "password123"
}
```
**응답**:
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiIs...",
  "tokenType": "Bearer",
  "userInfo": {
    "id": 1,
    "email": "user@example.com",
    "nickname": "홍길동"
  }
}
```

---

### 게시글

#### 게시글 목록 조회 (커서 기반)
```
GET /api/articles?lastId={id}&size={size}&sort={sort}&searchType={type}&keyword={keyword}
```
**파라미터**:
- `lastId` (optional): 마지막 게시글 ID
- `size` (default: 10): 조회 개수
- `sort` (default: latest): 정렬 기준 (latest/views/comments)
- `searchType` (optional): 검색 대상 (title/content/author, 콤마로 복수 선택)
- `keyword` (optional): 검색어

**응답**:
```json
{
  "posts": [
    {
      "articleId": 100,
      "title": "게시글 제목",
      "contentPreview": "내용 미리보기...",
      "writerId": 3,
      "writerName": "홍길동",
      "viewCount": 123,
      "commentCount": 5,
      "createdAt": "2024-12-08T12:34:56"
    }
  ],
  "nextCursor": 90,
  "hasMore": true
}
```

#### 게시글 상세 조회
```
GET /api/articles/{id}
```
**응답**:
```json
{
  "articleId": 100,
  "title": "게시글 제목",
  "content": "게시글 전체 내용",
  "writerId": 3,
  "writerName": "홍길동",
  "viewCount": 124,
  "commentCount": 5,
  "createdAt": "2024-12-08T12:34:56",
  "updatedAt": "2024-12-08T13:00:00"
}
```

#### 게시글 작성
```
POST /api/articles
Authorization: Bearer {token}
```
**요청**:
```json
{
  "title": "게시글 제목",
  "content": "게시글 내용"
}
```
**응답**:
```json
{
  "articleId": 101,
  "title": "게시글 제목",
  "content": "게시글 내용",
  "writerId": 3,
  "writerName": "홍길동",
  "createdAt": "2024-12-08T14:00:00"
}
```

#### 게시글 수정
```
PATCH /api/articles/{id}
Authorization: Bearer {token}
```
**요청**:
```json
{
  "title": "수정된 제목",
  "content": "수정된 내용"
}
```
**응답**: 게시글 상세 조회와 동일

#### 게시글 삭제
```
DELETE /api/articles/{id}
Authorization: Bearer {token}
```
**응답**: 204 No Content

---

### 댓글

#### 댓글 목록 조회
```
GET /api/articles/{articleId}/comments?page={page}&size={size}
```
**응답**:
```json
{
  "content": [
    {
      "id": 1,
      "articleId": 100,
      "content": "댓글 내용",
      "parentId": null,
      "writer": {
        "id": 3,
        "name": "홍길동"
      },
      "createdAt": "2024-12-08T12:00:00",
      "updatedAt": "2024-12-08T12:00:00"
    }
  ],
  "page": 0,
  "size": 10,
  "totalElements": 5,
  "totalPages": 1
}
```

#### 댓글/대댓글 작성
```
POST /api/comments
Authorization: Bearer {token}
```
**요청**:
```json
{
  "articleId": 100,
  "content": "댓글 내용",
  "parentId": null
}
```
> `parentId`를 지정하면 대댓글로 작성됩니다.

**응답**:
```json
{
  "id": 10,
  "articleId": 100,
  "content": "댓글 내용",
  "parentId": null,
  "writer": {
    "id": 3,
    "name": "홍길동"
  },
  "createdAt": "2024-12-08T14:00:00",
  "updatedAt": "2024-12-08T14:00:00"
}
```

#### 댓글 수정
```
PATCH /api/comments/{id}
Authorization: Bearer {token}
```
**요청**:
```json
{
  "content": "수정된 댓글 내용"
}
```
**응답**: 댓글 작성 응답과 동일

#### 댓글 삭제
```
DELETE /api/comments/{id}
Authorization: Bearer {token}
```
**응답**: 204 No Content

---

## 💻 로컬 실행 방법

### 1. 사전 준비
- Java 21
- PostgreSQL 15+
- Git

### 2. 저장소 클론
```bash
git clone https://github.com/your-username/heboard.git
cd heboard
```

### 3. 데이터베이스 설정
```sql
CREATE DATABASE heboard;
CREATE USER heboard WITH PASSWORD 'your-password';
GRANT ALL PRIVILEGES ON DATABASE heboard TO heboard;
```

### 4. 환경 설정
`src/main/resources/application.yml` 수정:
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/heboard
    username: heboard
    password: your-password
```

### 5. 실행
```bash
./gradlew bootRun
```

### 6. 접속
- API: http://localhost:8080
- Swagger: http://localhost:8080/swagger-ui.html

---

## 🛠️ 기술 스택
- **Backend**: Java 21, Spring Boot 3.3.2, Spring Data JPA, Spring Security
- **Database**: PostgreSQL 15+
- **Auth**: JWT
- **API Doc**: Swagger/OpenAPI
