# 윈도우 실행 
### 배치파일 실행 (Windows cmd 실행)
    docker-init.bat

## curl 명령어로 API 테스트 (windows cmd 실행)

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

# 세팅 
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

## Minikube 설치 및 실행
### MacOS에서 kompose 설치
    brew install kompose

### kompose를 사용하여 도커 컴포즈 파일을 Kubernetes YAML로 변환하는 방법
    mkdir -p k8s
    kompose convert -o k8s/

### Kubernetes 클러스터에 배포
    kubectl apply -f k8s/

# Minikube 상태 확인
    minikube status

# Minikube 시작 (미니쿠브 m1, m2 이슈)
    minikube start --driver=docker --container-runtime=containerd

# minikube stop 상태
    minikube stop
