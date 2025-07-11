# JWT (JSON Web Token) 개념 정리

## ✅ JWT란?
    JWT는 JSON Web Token의 약자로,  
    사용자 인증 및 권한 부여 정보를 안전하게 주고받기 위한 토큰 기반 인증 방식임.
    JWT는 주로 로그인 이후 인증 상태를 유지하거나, 마이크로서비스 간 인증 정보를 전달할 때 쓰임.  
    Stateless하고 Self-contained(자기 포함형) 구조를 가짐.

## JWT 구조
    JWT는 아래와 같이 `.`으로 구분된 3개의 문자열로 구성됨:
    <Header>.<Payload>.<Signature>
    각 부분 설명:
    Header	서명 알고리즘, 타입 정보 (ex. HS256, JWT)
    Payload	실제 인증 정보, 클레임들 (예: 사용자 ID, 권한 등)
    Signature	위조 방지를 위한 서명 (secret key로 HMAC 또는 RSA 등 적용)

## JWT 사용 흐름
    [1] 로그인 성공 시
         ↓
    [2] 서버가 JWT 생성
         ↓
    [3] 클라이언트에 응답으로 JWT 전달
         ↓
    [4] 이후 모든 요청에 JWT 포함 (Authorization 헤더)
         ↓
    [5] 서버에서 JWT 유효성 검사 후 사용자 인증 처리

## JWT의 장점
    간편한 인증: 토큰만 있으면 별도 DB 조회 없이 인증 가능
    확장성 좋은 구조: Stateless 구조로 서버 확장에 유리
    다양한 클라이언트 지원: 웹, 모바일, IoT 등 다양한 환경에서 사용 가능
    표준화된 포맷: JWT는 RFC 7519 표준에 정의되어 있어 호환성 좋음
## JWT의 단점
    보안 취약점: 토큰 탈취 시 위조 가능성 존재
    토큰 크기 증가: 인증 정보가 토큰에 포함되어 크기가 커질 수 있음
    만료 처리 어려움: 토큰 만료 후 클라이언트가 다시 인증해야 함
    상태 관리 어려움: Stateless 구조로 인해 서버에서 상태를 관리하기 어려움

## JWT와 세션의 차이점
| 항목          | JWT (JSON Web Token) | 세션 (Session) |
|---------------|-----------------------|----------------|
| 저장 위치     | 클라이언트 (토큰)      | 서버 (메모리/DB) |
| 인증 방식     | Stateless (토큰 기반)   | Stateful (세션 기반) |
| 확장성        | 서버 확장에 유리        | 서버 확장에 제약 있음 |
| 보안          | 토큰 탈취 시 위조 가능성 존재 | 세션 ID 탈취 시 위조 가능성 존재 |

## JWT 사용 예시
    1. 사용자 로그인 요청
    2. 서버에서 인증 성공 후 JWT 생성
    3. 클라이언트에 JWT 전달
    4. 클라이언트는 이후 모든 요청에 JWT 포함 (Authorization 헤더)
    5. 서버는 JWT를 검증하여 사용자 인증 처리

# Redis 분산락 구조 요약
## 단계	동작 내용
    setIfAbsent 시도	msg:lock:{phone} key가 Redis에 있는지 확인
    없다면 → 메시지 Kafka로 전송 & expire 1분 설정
    있다면 → 이미 전송된 사용자이므로 전송 생략

## 왜 Redis를 써야 할까?
    Spring Boot 인스턴스가 여러 개 떠 있을 수 있음 (e.g. 도커 컨테이너 여러 개)
    이 경우 동시에 같은 사용자에게 중복 메시지를 보낼 수 있음
    Redis는 중앙 저장소라서 → 모든 인스턴스에서 락 공유 가능
    따라서 동시성 제어와 중복 방지 역할을 완벽하게 해줌

## 예시 흐름 (전화번호 01012345678)
    Kafka 전송 전 msg:lock:01012345678 존재 여부 확인
    없다면:
        Redis에 저장 (setIfAbsent)
        TTL 1분 설정 (expire)
    Kafka 전송
    있다면:
        이미 보냈으므로 무시 (중복 전송 방지)

## Kafka 토픽 분리 개념 (연령대별 메시지 처리용)

### 개요
    Kafka는 메시지를 전송/수신할 수 있는 중앙 메시지 허브 역할을 함.  
    그 중심 단위는 토픽(topic)이며, Producer는 이 토픽으로 메시지를 보내고, 
    Consumer는 해당 토픽을 구독하여 메시지를 처리함.
    이번 시스템에서는 연령대별 메시지를 독립적으로 처리하기 위해 Kafka 토픽을 연령대별로 분리함.

### 연령대별 Kafka 토픽 분리 구조
| 연령대        | Kafka 토픽            | Consumer 클래스        |
|------------|------------------------|-------------------------|
| 20대        | `message-topic-20s`    | `MessageConsumer20s`    |
| 30대        | `message-topic-30s`    | `MessageConsumer30s`    |
| (추가가능) 40대 | `message-topic-40s` | `MessageConsumer40s` |

    → Producer는 메시지를 전송할 때 사용자 연령대에 따라 토픽을 선택함  
    → Consumer는 각각의 토픽만 구독함으로써 연령대별 처리 분리가 가능해짐

##  왜 토픽을 나누는가?
    - RateLimit 분리 처리용  
    → 카카오톡/SMS API는 분당 호출 제한이 있음  
    → 연령대별로 나누면 개별 제한량을 초과하지 않도록 관리 가능

    - 장애 격리  
    → 30대 토픽에서 오류 발생 시, 20대 토픽에는 영향 없음

    - 비즈니스 로직 분리 용이  
    → 연령대별로 메시지 포맷, 대상, 방식이 다를 경우 처리 코드를 분리할 수 있음

    - 확장성 확보  
    → 40대, 50대 추가 시 토픽 + Consumer만 추가하면 구조 유지 가능

## ✅ Kafka 토픽 vs 파티션 차이
| 항목     | 의미 |
|----------|------|
| Topic    | 메시지를 기능별/속성별로 구분하는 논리적 단위 |
| Partition | 하나의 토픽을 물리적으로 병렬처리하기 위한 단위 |
    
    Topic: message-topic-20s
     ├─ Partition 0
     ├─ Partition 1
     └─ Partition 2

## MACOS는 리눅스랑 도커 컨테이너 경로가 달라서 경로를 다르게 설정해줘야함

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

## 젠킨스 세팅
    jenkins:
    image: jenkins/jenkins:lts
    container_name: jenkins
    user: root
    ports:
    - "8084:8080"
      - "50000:50000"
      volumes:
      - jenkins_home:/var/jenkins_home
      - /var/run/docker.sock:/var/run/docker.sock  # 도커 빌드 가능하게
      environment:
      - JAVA_OPTS=-Djenkins.install.runSetupWizard=false
      networks:
      - app-network  # 또는 elk, 필요한 쪽에 붙이기
    
    volumes:
    esdata:
    jenkins_home:

## ELK 관련 명령어
### Elasticsearch 인덱스 생성 확인
    curl -X GET "localhost:9200/_cat/indices?v"

### Kibana 접속
    http://localhost:5601

### Logstash 접속
    http://localhost:9600

### Logstash 로그 확인
    docker logs -f full-project-logstash-1

### Logstash 설정 파일 확인
    docker exec -it full-project-logstash-1 cat /usr/share/logstash/config/logstash.conf

### Logstash 설정 파일 수정
    docker exec -it full-project-logstash-1 bash
    vi /usr/share/logstash/config/logstash.conf

### Logstash 설정 파일 적용
    docker restart full-project-logstash-1

### Logstash 설정 파일 적용 확인
    docker logs -f full-project-logstash-1

### Logstash 설정 파일 적용 후 Elasticsearch 인덱스 확인
    curl -X GET "localhost:9200/_cat/indices?v"

### Kibana에서 인덱스 패턴 생성
    1.http://localhost:5601 접속 (Kibana)
    2.왼쪽 메뉴 → Stack Management > Index Patterns
    3.→ Create index pattern
    4.인덱스 이름에 filebeat-* 입력
    5.타임필드로 @timestamp 선택
    6.Discover 메뉴에서 container.name : "spring-boot-app" 등록후 로그 확인 가능

### 컨테이너 이름 확인
    container.name : "spring-boot-app"
    container.name : "kafka"
    container.name : "redis"
    container.name : "spring-boot-app"
    container.name : "spring-boot-app"

### kibana에서 실시간 컨테이너 로그 시각화
    🛠️ 만들기 순서 (Step-by-step)
    1. Kibana → Visualize → Create new visualizatio Lens 선택
    
    2. X축 (Horizontal axis) 설정
    Field: @timestamp

    Aggregation: Date Histogram
    
    Interval: auto 또는 30s, 1m (실시간성 조절)
    
    3. Y축 (Vertical axis) 설정
       Function: Count (기본값)
    
    4. Break down by 설정
       Field: container.name
       → 컨테이너 이름별로 색깔이 다른 라인 그래프 or 막대그래프로 분리됨

    5. Visualization 타입 선택
       Bar chart (막대), Line chart (선형), Area chart (누적) 중 선택 가능
       → 보통 Bar chart로 비교 분석이 직관적
    
    💡 실시간성 높이려면
    Lens 상단에서 Refresh every: 10 seconds 설정
    
    Time range는 Last 15 minutes 또는 Last 1 hour 등

### filebeat
    filebeat:
    image: docker.elastic.co/beats/filebeat:7.17.3
    container_name: filebeat
    user: root
    volumes:
    - /var/lib/docker/containers:/var/lib/docker/containers:ro
      - /var/run/docker.sock:/var/run/docker.sock
      - ./filebeat/filebeat.yml:/usr/share/filebeat/filebeat.yml:ro
      depends_on:
      - logstash
      networks:
      - elk
