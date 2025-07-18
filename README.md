# 프로젝트 개요
본 프로젝트는 연령대별 사용자에게 대규모 메시지를 발송하는 시스템을 구현. Kafka를 통해 연령대별 토픽으로 메시지를 비동기 분산 처리하며, Redis를 활용해 중복 전송을 방지하고 분당 전송 제한(Rate Limiting)을 제어. Spring Boot 기반의 마이크로서비스 구조로 구성되어 있으며, 카카오톡 및 문자 메시지 발송은 Mock 서버를 통해 시뮬레이션, 전체 시스템은 Docker 환경에서 컨테이너화하여 쉽게 실행하고 테스트할 수 있도록 구성

# 프로젝트 구조
<img width="1365" height="434" alt="1" src="https://github.com/user-attachments/assets/aa1daad7-fc87-4d2c-b590-1e6e6e60d528" />
<img width="1822" height="644" alt="2" src="https://github.com/user-attachments/assets/fe25d6a9-f545-4937-a898-887b338e0289" />
<img width="1794" height="640" alt="3" src="https://github.com/user-attachments/assets/0f4b7cd1-786f-41b1-b35a-f1338eb6df7c" />

# 기술 스택
| 항목            | 사용 기술                            | 설명                                         | 
|-----------------|--------------------------------------|--------------------------------------------|
| 언어            | Java 17                             | 백엔드 시스템 구현에 사용된 주요 프로그래밍 언어                |
| 프레임워크      | Spring Boot 3.5.3, Spring Data JPA   | 웹 애플리케이션 및 데이터 액세스 계층 구현에 사용됨              |
| 빌드 도구       | Gradle                               | 프로젝트 의존성 관리 및 빌드 자동화 도구                    |
| 데이터베이스     | H2 Database, MariaDB                 | H2는 테스트용 인메모리 DB, MariaDB는 운영용 RDB로 사용     |
| 인증/보안       | JWT, Spring Security                 | 로그인 인증 및 API 접근 권한 제어 구현                   |
| 메시지 큐       | Apache Kafka                         | 연령대별 메시지 비동기 분산 처리를 위한 메시지 브로커             |
| 캐시/속도 제한   | Redis                                | 메시지 전송 중복 방지 및 Rate Limiting 처리에 사용        |
| 문서화 도구     | Swagger UI (OpenAPI 3)              | REST API 명세 및 테스트용 UI 제공                   |
| 기타 도구       | Lombok, Docker                       | Lombok은 보일러플레이트 제거, Docker는 컨테이너 기반 서비스 실행 |


# 프로젝트 실행 방법
## 1. 도커 설치
    - Docker Desktop 설치: https://www.docker.com/products/docker-desktop

## 2. 쉘 or 배치 파일(도커) 실행 
### 쉘파일 실행 (macOS, Linux)
    chmod +x docker-init.sh
    ./docker-init.sh
<img width="1311" alt="스크린샷 2025-07-04 오후 4 18 05" src="https://github.com/user-attachments/assets/0980ba2b-e6b3-4366-accb-9c688ab55515" />

## 컨테이너 실행 종료
    docker-compose -f docker-compose.yml down

## 3. curl 명령어로 API 테스트 (macOS, Linux)
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
<img width="949" height="180" alt="스크린샷 2025-07-11 오전 9 59 26" src="https://github.com/user-attachments/assets/70dbc426-48c8-4975-9264-2585852e4532" />

    
#### Kafka 토픽 매핑 정보
    - 20대 → topic: message-topic-20s
    - 30대 → topic: message-topic-30s

#### 20대 대상 메시지 전송
    curl -X POST http://localhost:8080/admin/messages \
    -u admin:1212 \
    -H "Content-Type: application/json" \
    -d '{"ageGroup": 20, "message": "blabla"}'
<img width="1096" height="801" alt="스크린샷 2025-07-11 오전 10 01 10" src="https://github.com/user-attachments/assets/f9975067-e403-4f0c-9dbd-b520d179162b" />
<img width="1111" height="801" alt="스크린샷 2025-07-11 오전 10 01 22" src="https://github.com/user-attachments/assets/fb78760e-82a3-4ace-aba6-c0a05d1456a9" />

#### 30대 대상 메시지 전송
    curl -X POST http://localhost:8080/admin/messages \
    -u admin:1212 \
    -H "Content-Type: application/json" \
    -d '{"ageGroup": 30, "message": "blabla"}'
<img width="1353" height="83" alt="스크린샷 2025-07-11 오전 10 08 44" src="https://github.com/user-attachments/assets/34ee4d56-715e-46d1-8ef3-c8f367683b0d" />


# 부록 (Kafka, MariaDB, Redis 관련 명령어)
## 도커 관련 명령어
### 도커 컨테이너 실행 확인
    docker ps
<img width="1345" height="452" alt="스크린샷 2025-07-11 오전 10 09 02" src="https://github.com/user-attachments/assets/e4d43bcc-0466-42fa-85f5-3159ac5ef7f3" />

### 컨테이너 로그 확인
    docker logs -f 컨테이너명
<img width="709" height="403" alt="스크린샷 2025-07-11 오전 10 10 12" src="https://github.com/user-attachments/assets/a666dbbf-39f0-4bba-bdc8-9f94eecaad04" />


### 도커 컨테이너 단일 재시작
    docker-compose restart logstash

## 카프카 관련 명령어
### 카프카 토픽 전체 조회
    docker exec -it <kafka-container-name> kafka-topics.sh --bootstrap-server localhost:9092 --list
<img width="1126" height="77" alt="스크린샷 2025-07-11 오전 10 10 41" src="https://github.com/user-attachments/assets/d3b572d3-825d-4dc9-ba1e-a43850578ffd" />


### 특정 토픽 내용 조회
    docker exec -it <kafka-container-name> kafka-console-consumer.sh \
    --bootstrap-server localhost:9092 \
    --topic message-topic-30s \
    --from-beginning
<img width="899" height="355" alt="스크린샷 2025-07-11 오전 10 11 50" src="https://github.com/user-attachments/assets/8ef9a0b6-f5fc-4801-b2f1-bd62121a1c1b" />

    

## MariaDB 관련 명령어
### MariaDB 컨테이너에 접속
    docker exec -it mariadb bash
<img width="734" height="47" alt="스크린샷 2025-07-11 오전 10 24 15" src="https://github.com/user-attachments/assets/16ae5bd1-cc56-4985-abde-15375e19ae4a" />

### mysql 클라이언트 실행 (컨테이너 안에서)
    mysql -u root -p
    password: rootpass
<img width="626" height="207" alt="스크린샷 2025-07-11 오전 10 24 35" src="https://github.com/user-attachments/assets/b3431afd-b784-43c6-8dc2-d5b2eb67fc7b" />

### 원하는 데이터베이스 선택 후 쿼리 실행
    show databases;
    use userdb; 
    SELECT * FROM users;
<img width="948" height="375" alt="스크린샷 2025-07-11 오전 10 24 46" src="https://github.com/user-attachments/assets/aa375f34-de51-44f4-9915-4b0ea4504791" />

## Redis 관련 명령어
### 모든 키 확인
    docker exec -it <redis> redis-cli
    127.0.0.1:6379> keys *
<img width="420" height="414" alt="스크린샷 2025-07-11 오전 10 31 50" src="https://github.com/user-attachments/assets/92a2d5e2-fd11-4f2e-8f3c-f5844f751939" />

## ELK 관련 세팅 (only spring-boot)
### Kibana에서 인덱스 패턴 생성
    1.http://localhost:5601 접속 (Kibana)
    2.왼쪽 메뉴 → Stack Management > Index Patterns
    3.→ Create index pattern
    4.인덱스 이름에 springboot-logs-* 입력
    5.타임필드로 @timestamp 선택
    6.Discover 메뉴에서 container.name : "spring-boot-app" 등록후 로그 확인 가능
