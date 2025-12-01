# heboard 백엔드 컨벤션

본 문서는 heboard 프로젝트 백엔드 개발 시 최소한으로 지켜야 할 규칙만 간단히 정리한 컨벤션입니다.

---

## 1. 📦 패키지 구조 규칙

~~~
com.example.heboard
├── domain/
│ ├── article/
│ └── comment/
├── auth/
├── user/
└── global/
├── config/
├── exception/
└── security/
~~~

- 도메인(article, comment) 중심으로 구성
- 인증(auth), 사용자(user)는 독립 패키지
- 공통 설정 및 예외(global)는 별도 관리

---

## 2. ✏ 코드 스타일 규칙

### ✔ 클래스명
- PascalCase  
  예) `ArticleController`, `AuthService`

### ✔ 메서드명 / 변수명
- camelCase  
  예) `createArticle()`, `articleId`, `lastId`

### ✔ 들여쓰기
- 4 spaces

### ✔ 예외 처리
- 공통 예외는 `global/exception`에서 관리
- 서비스에서는 `orElseThrow()` + 커스텀 예외 사용

---

## 3. 🔗 API 작성 규칙

- 모든 API는 `/api` prefix 사용  
  예) `/api/articles`, `/api/auth/login`

- RESTful 규칙 준수  
  | Method | 의미 |
  |--------|------|
  | GET | 조회 |
  | POST | 생성 |
  | PUT | 수정 |
  | DELETE | 삭제 |

- 응답 형식은 일관되게 유지 (성공·실패 메시지 포함)

---

## 4. 🪵 로그 규칙

- `System.out.println` 금지
- 반드시 `@Slf4j` 사용
- 민감정보 로그 출력 금지

---

## 5. 🧪 테스트 규칙

- 서비스 단위 테스트 우선
- Mocking 사용 권장
- 테스트 메서드명은 설명형  
  예) `shouldCreateArticleSuccessfully()`

---

## 6. 📨 커밋 메시지 규칙


### ✔ 타입 목록
| 타입 | 의미 |
|------|------|
| feat | 새로운 기능 |
| fix | 버그 수정 |
| refactor | 리팩토링 |
| docs | 문서 수정 |
| test | 테스트 코드 |
| chore | 설정/환경 변경 |

### ✔ 예시  
~~~
feat(article): 게시글 생성 API 구현
fix(auth): 로그인 검증 오류 수정
docs(readme): 소개 문서 업데이트
~~~


---

## 7. 🔧 환경 설정 규칙

- DB 정보는 `application.yml`에서 관리
- MySQL 8.0 기준
- 민감정보는 절대 Git에 업로드 금지

---