# 기술 스택
| 항목        | 사용 기술                                                    |
|-------------|--------------------------------------------------------------|
| 언어        | Java 17                                                     |
| 프레임워크  | Spring Boot 3.5.3, Spring Data JPA                           |
| 빌드 도구   | Gradle                                                      |
| 데이터베이스 | H2 Database, MariaDB                                        |
| 인증/보안   | JWT, Spring Security                                        |
| 메시지 큐   | Apache Kafka                                                |
| 캐시/속도 제한 | Redis                                                     |
| 문서화 도구 | Swagger UI (OpenAPI 3)                                      |
| 기타 도구   | Lombok, Docker                                              |

# 프로젝트 실행 방법
## 1. 도커 설치
    - Docker Desktop 설치: https://www.docker.com/products/docker-desktop

## 2. 쉘 or 배치 파일(도커) 실행 
### 쉘파일 실행 (macOS, Linux)
    chmod +x docker-init.sh
    ./docker-init.sh
<img width="1311" alt="스크린샷 2025-07-04 오후 4 18 05" src="https://github.com/user-attachments/assets/0980ba2b-e6b3-4366-accb-9c688ab55515" />

### 배치파일 실행 (Windows cmd 실행)
    docker-init.bat

## 3. curl 명령어로 API 과제 테스트 (windows cmd 실행)

### 1) 회원가입 API 테스트
    curl -X POST http://localhost:8080/users/signup -H "Content-Type: application/json" -d "{\"account\":\"test123\",\"password\":\"1234\",\"name\":\"하창현\",\"residentRegistrationNumber\":\"9001011234567\",\"phoneNumber\":\"01012345678\",\"address\":\"서울특별시 금천구\"}"

### 2) 시스템 관리자 API 테스트
#### 2-1. 전체 사용자 조회 (페이지=1, size=30)
    curl -X GET "http://localhost:8080/admin/users?page=1&size=30" -u admin:1212

#### 2-2. 사용자 정보 수정
##### 주소만 변경
    curl -X PUT http://localhost:8080/admin/users/3000 -u admin:1212 -H "Content-Type: application/json" -d "{\"address\":\"경기도 수원시\"}"

##### 비밀번호만 변경:
    curl -X PUT http://localhost:8080/admin/users/3000 -u admin:1212 -H "Content-Type: application/json" -d "{\"password\":\"newPassword123\"}"

##### 비밀번호 + 주소 변경:
    curl -X PUT http://localhost:8080/admin/users/3000 -u admin:1212 -H "Content-Type: application/json" -d "{\"password\":\"newPassword123\",\"address\":\"서울시 송파구\"}"

#### 2-3. 사용자 삭제
    curl -X DELETE http://localhost:8080/admin/users/3000 -u admin:1212

#### 3) 로그인 → JWT 토큰 획득
    curl -X POST http://localhost:8080/users/login -H "Content-Type: application/json" -d "{\"account\":\"test123\",\"password\":\"1234\"}"
##### 로그인 결과에서 message에 있는 JWT 토큰 추출 필요

#### 4) 로그인 사용자 자신의 회원 정보 조회 (JWT 토큰 필요)
    curl -X GET http://localhost:8080/users/me -H "Authorization: Bearer jwt"
##### 위 명령의 jwt 자리에 실제 토큰 문자열 삽입

#### 5) 관리자 API – 연령대별 Kafka 메시지 전송

##### 20대 메시지 전송:
    curl -X POST http://localhost:8080/admin/messages -u admin:1212 -H "Content-Type: application/json" -d "{\"ageGroup\":20,\"message\":\"blabla\"}"

##### 30대 메시지 전송:
    curl -X POST http://localhost:8080/admin/messages -u admin:1212 -H "Content-Type: application/json" -d "{\"ageGroup\":30,\"message\":\"blabla\"}"


## 3. curl 명령어로 API 과제 테스트 (macOS, Linux)
### 1).회원가입 API 테스트
    curl -X POST http://localhost:8080/users/signup \
    -H "Content-Type: application/json" \
    -d '{
    "account": "test123",
    "password": "1234",
    "name": "하창현",
    "residentRegistrationNumber": "9001011234567",
    "phoneNumber": "01012345678",
    "address": "서울특별시 금천구"
    }'
<img width="889" alt="스크린샷 2025-07-04 오후 4 19 37" src="https://github.com/user-attachments/assets/c4fd343f-ede7-488a-b200-863267b5232e" />


### 2).시스템 관리자 API 테스트
#### 2-1. 전체 사용자 조회 (예: 페이징1 페이지당 30명)
    curl -X GET "http://localhost:8080/admin/users?page=1&size=30" -u admin:1212
<img width="1347" alt="스크린샷 2025-07-04 오후 5 42 44" src="https://github.com/user-attachments/assets/b4b38a2b-7af8-41a1-889b-19141d2d99e3" />


#### 2-2. 사용자 정보 수정 (예: userId=3000 가정)
##### 주소만 변경
    curl -X PUT http://localhost:8080/admin/users/3000 \
    -u admin:1212 \
    -H "Content-Type: application/json" \
    -d '{"address": "경기도 수원시"}'
<img width="947" alt="스크린샷 2025-07-04 오후 5 46 19" src="https://github.com/user-attachments/assets/35464065-bf1f-405c-9d53-abaad0792f9a" />


##### 비밀번호 변경
    curl -X PUT http://localhost:8080/admin/users/3000 \
    -u admin:1212 \
    -H "Content-Type: application/json" \
    -d '{"password": "newPassword123"}'
<img width="940" alt="스크린샷 2025-07-04 오후 5 46 31" src="https://github.com/user-attachments/assets/76034ff6-9fa9-49c2-af21-e88d4cf4cc63" />


##### 비밀번호, 주소 변경
    curl -X PUT http://localhost:8080/admin/users/3000 \
    -u admin:1212 \
    -H "Content-Type: application/json" \
    -d '{"password": "newPassword123", "address": "서울시 송파구"}'
<img width="943" alt="스크린샷 2025-07-04 오후 5 46 44" src="https://github.com/user-attachments/assets/363664b7-6820-4745-ae4c-c23903cc15ff" />


#### 2-3. 사용자 삭제 (예: userId=3000 가정)
    curl -X DELETE http://localhost:8080/admin/users/3000 -u admin:1212
<img width="1346" alt="스크린샷 2025-07-04 오후 5 47 44" src="https://github.com/user-attachments/assets/09f28933-0c85-480d-aab0-7701458fe11b" />


### 3).로그인 → JWT 토큰 획득
    curl -X POST http://localhost:8080/users/login \
    -H "Content-Type: application/json" \
    -d '{
    "account": "test123",
    "password": "1234"
    }'
<img width="1176" alt="스크린샷 2025-07-04 오후 5 48 01" src="https://github.com/user-attachments/assets/0600ccd8-334d-4da3-9547-529631b62a93" />


#### 위 명령 결과에서 "message" 필드에 있는 토큰 값을 추출한 후, 아래 명령에서 jwt 자리에 넣어주세요.

### 4).로그인 한 사용자의 자신의 회원 상세 정보 조회 테스트
    curl -X GET http://localhost:8080/users/me \
    -H "Authorization: Bearer jwt"
<img width="1285" alt="스크린샷 2025-07-04 오후 5 48 30" src="https://github.com/user-attachments/assets/5ef4ce75-ef1e-464c-9521-47739f421f3e" />


### 5).관리자API) 연령대별 kafka 메시지 전송 테스트
#### 테스트용 더미 유저 정보
    - 총 3000명 생성
    - 20대: 700명 (생년월일 2000년생, 주민번호 앞자리 "000101", 성별코드 3 → 2000년대 남성)
    - 30대: 700명 (생년월일 1990년생, 주민번호 앞자리 "900101", 성별코드 1 → 1900년대 남성)
    - 기타: 1600명은 10대~70대 무작위 생성
    
#### Kafka 토픽 매핑 정보
    - 20대 → topic: message-topic-20s
    - 30대 → topic: message-topic-30s

#### 20대 대상 메시지 전송
    curl -X POST http://localhost:8080/admin/messages \
    -u admin:1212 \
    -H "Content-Type: application/json" \
    -d '{"ageGroup": 20, "message": "blabla"}'

#### 30대 대상 메시지 전송
    curl -X POST http://localhost:8080/admin/messages \
    -u admin:1212 \
    -H "Content-Type: application/json" \
    -d '{"ageGroup": 30, "message": "blabla"}'

# 부록 (카프카, MariaDB, Redis 관련 명령어)
## 도커 로그 확인
### 도커 컨테이너 실행 확인
    docker ps
    
### 컨테이너 로그 확인
    docker logs -f 컨테이너명

## 카프카 관련 명령어
### 카프카 토픽 전체 조회
    docker exec -it <kafka-container-name> kafka-topics.sh --bootstrap-server localhost:9092 --list

### 특정 토픽 내용 조회
    docker exec -it <kafka-container-name> kafka-console-consumer.sh \
    --bootstrap-server localhost:9092 \
    --topic message-topic \
    --from-beginning

## MariaDB 관련 명령어
### MariaDB 컨테이너에 접속
    docker exec -it mariadb bash

### mysql 클라이언트 실행 (컨테이너 안에서)
    mysql -u root -p
    password: rootpass

### 원하는 데이터베이스 선택 후 쿼리 실행
    show databases;
    use userdb; 
    SELECT * FROM users;

## Redis 관련 명령어
### 모든 키 확인
    docker exec -it full-project-redis-1 redis-cli
    127.0.0.1:6379> keys *
