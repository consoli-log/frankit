# Frankit API - 상품 및 옵션 관리

안녕하세요, **프랜킷 지원자 엄솔이**입니다.  
요구사항을 기반으로 API를 구현하였으며, 확장성과 유지보수를 고려한 설계를 반영하였습니다.  

---

## 1. 프로젝트 개요
**Frankit API**는 상품 및 옵션을 관리하는 RESTful API입니다.  

📌 **주요 기능**  
- **회원가입 및 로그인** (JWT 인증)  
- **상품 관리** → 등록, 수정, 삭제, 조회 (페이징 포함)  
- **옵션 관리** → 입력형/선택형 옵션 등록, 수정, 삭제, 조회  
- **상세 옵션 관리** → 선택형 옵션의 상세 항목 등록, 수정, 삭제, 조회  
- **주문 여부에 따른 옵션 수정/삭제 제한 및 활성화/비활성화 관리**  
- **에러 처리 및 로깅** (`SLF4J` 적용)  
- **Swagger API 문서 제공**  

---

## 2. 기술 스택  
- **Backend**: Java 23, Spring Boot 3.3.9, Gradle, JPA, Spring Security 6.3.7  
- **DB**: MySQL (운영 가능)  
- **Auth**: JWT (JSON Web Token)  
- **API 문서화**: Swagger  
- **Logging**: SLF4J  

---

## 3. 설치 및 실행 방법

### 1) 프로젝트 클론  
```sh
git clone https://github.com/consoli-log/frankit.git
cd frankit
```
### 2) 환경 설정  
프로젝트에 제공된 `.env.example` 파일을 `.env`로 복사한 후, 환경 변수 값을 설정하세요.  
그 후 .env 파일을 열어 DB 정보 및 JWT Secret Key를 수정하세요.


```ini
# Database Configuration
DB_PORT=3306
DB_NAME=your_database_name
DB_USERNAME=your_database_user
DB_PASSWORD=your_database_password

# JWT Secret Key 
JWT_SECRET=your_jwt_secret_key
```

주의: `.env` 파일은 보안상 `.gitignore`에 추가되어 있어야 합니다.

---

## 4. 데이터베이스 설정  

프로젝트 실행 전에 MySQL에서 **데이터베이스 및 사용자 계정을 설정해야 합니다.**  
이를 위해 `init.sql` 및 `schema.sql` 파일을 실행하세요.

### 1) 데이터베이스 및 사용자 생성 (init.sql)  
`init.sql`을 실행하여 데이터베이스와 MySQL 계정을 자동으로 설정할 수 있습니다.
```sh
mysql -u root -p < init.sql
```
### 2) 테이블 및 기본 데이터 생성 (schema.sql)
데이터베이스를 설정한 후, `schema.sql`을 실행하여 테이블을 생성하세요.
```sh
mysql -u frankit -p frankit < schema.sql
```
실행 후, `users`, `products`, `product_options`, `option_details` 테이블이 생성됩니다.

---

## 5. 애플리케이션 실행
DB 설정이 완료되었으면, 애플리케이션을 실행하세요.
```sh
./gradlew bootRun
```
---

## 6. API 문서 
Swagger API 문서를 통해 API 명세를 확인할 수 있습니다.  
**[Swagger UI 바로가기](http://localhost:8080/swagger-ui/index.html)**

---

## 7. 테스트 실행 방법 
단위 테스트를 실행하려면 아래 명령어를 사용하세요.
```sh
./gradlew test
```
- Service, Controller 단위 테스트 포함
- 주요 비즈니스 로직 및 예외 처리 검증 완료

---

## 8. 프로젝트 디렉토리 구조 
```plaintext
com.soli.frankit  
 ┣ 📂 config            # 설정 관련 파일  
 ┣ 📂 controller        # API 컨트롤러  
 ┣ 📂 domain            # 엔티티 및 JPA 매핑  
 ┣ 📂 dto               # 요청/응답 DTO  
 ┣ 📂 repository        # JPA Repository  
 ┣ 📂 service           # 비즈니스 로직  
 ┣ 📂 exception         # 커스텀 예외 처리  
 ┗ 📂 util              # 유틸리티 클래스  
```
