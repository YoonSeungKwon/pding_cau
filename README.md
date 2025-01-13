![로고](https://github.com/YoonSeungKwon/Capstone1/blob/master/pding4.png)
# 프딩 Pding

## 1. 프로젝트 개요 
  프딩(프레젠트 펀딩, Present Funding)은 원하는 선물을 등록하면 친구들이 그 금액을 채워주는 크라우드 펀딩 형태의 모바일 중접 웹 서비스입니다.
  기존 선물 문화의 문제를 해결하고, 사용자들이 더 나은 선물 경험을 즐길 수 있도록 돕기 위해 제작되었습니다.

## 2. 팀 구성 (3인)
  + **팀장 윤상희**: 기획, 프론트엔드 개발
  + **팀원 기나연**: UX/UI 디자인
  + **팀원 윤승권**: 프론트엔드, 백엔드 개발 

 ## 3. 개발 환경 및 기술
  + **협업** :     Figma, Notion, Git, Swagger
  + **언어** :     Java(jdk 17.), html/css, javascript
  + **FE** :       React, Nginx
  + **BE** :       Spring Boot(3.3.), JPA/Hibernate
  + **DB** :       MariaDB, Redis
  + **SEVER** :    Apache Tomcat(10.), AWS EC2, AWS S3, Docker, RabbitMQ
  + **API** :      KAKAO Oauth2, KAKAO PAY
  + **MQ**  :      RabbitMQ

 ## 4. 주요 기술

  **1. AES암호화**: AES 암호화를 통해 휴대폰 번호, 결제 코드 등을 안전하게 저장

  
  **2. Façade패턴**: Façade 패턴을 이용하여 트랜잭션 시작 전에 Lock을 요청하도록 설계

  
  **3. 로깅**: 로깅을 통해 메시지 큐와 결제 API 관련 에러 발생 시 추후 조치할 수 있도록 설계


  **4. 트리거, Audit Table**: Hard Delete시 주문 관련 데이터를 백업하기 위해 트리거와 audit table을 활용

  
  **5. Docker WatchTower**: Docker compose 파일에 있는 Docker Hub의 이미지들을 감시하여 최신의 컨테이너를 자동으로 배포


  **6. 모니터링**: Prometheus와 Grafana를 이용한 모니터링 환경을 구축하고, Redis 모니터링을 위하여 Redis Exporter 추가

  
 ## 5. 성능 향상
 
  **1. JPA N+1 문제 처리**: Lazy Loading과 Fetch Join, LEFT JOIN 등을 이용한 Eagle Loading을 이용하는 메소드를 구분하여 JPQL을 작성하여 N+1문제로 인한 문제를 예방하였다.

    
  **2. SQL 복합 INDEX설정**: 사용되는 쿼리들을 최대한 일관되도록 리팩터링 한 뒤, 테이블에 테스트 데이터를 이용하여 실행계획과 프로파일링을 분석하여 적절한 인덱스를 설정하여 정렬, 스캔 시간을 최적화하였다.

  
  **3. Redis를 이용한 캐싱**: 검증 과정을 메인 데이터베이스 대신 Redis에 캐싱한 데이터로 처리하여 네트워크 응답 속도와 디스크 I/O를 줄였다.


  **4. 비동기 처리로 결제속도 향상**: 서비스 특성상 한 펀딩에 대한 결제 요청이 특정 날짜로 몰리는 구조이기 때문에, 
요청이 생길 시 Redis에 엔티티를 로딩하고, RLock을 통하여 동시성을 고려하며 매번 결제마다 DB커넥션과 디스크 I/O 없이 검증만으로 빠르게 응답할 수 있도록 하였고, 메시지 큐를 통하여 주문에 대한 데이터를 전송하여 외부 노드에서 배치 작업 등을 이용하여 DB 작업을 처리할 수 있도록 서비스를 설계하였다.

  ![메시지큐 시나리오](https://github.com/user-attachments/assets/c4b68b7b-c309-4dab-9ac5-f07047d87f05)

## 6. 이슈

  **1. 인증/인가 시 LazyInitializationException**
  
JWT를 이용하여 인증/인가를 구현하는 도중 SecurityFilter에서 JWT의 인증과 인가를 맡고 있는 JwtAuthenticationFilter에서 서비스인 JwtProvider를 이용하여 토큰의 클레임에 있는 정보로 UserDetails를 사용하는 시점에서 LazyInitializationException이 발생하였다. 이유는 SecurityFilter는 영속성 컨텍스트가 유지되는 OSIV의 범위 밖이고, 회원 엔티티인 Members엔티티에 양방향으로 연관된 엔티티가 지연로딩으로 선언되어 있어서 해당 에러가 발생하였다.

  + **Solution1:Members 객체의 Lazy Loading을 Eagle로 변경**

    + 장점: 구현이 간단하고, 코드 수정이 없음

    + 단점: 매 요청마다 Member의 사용하지 않더라도 연관된 엔티티들을 불러와야 하는 Disk I/O 비효율이 생긴다.

      
  + **Solution2: OSIV 인터셉터를 필터로 변경하여 SecurityFilterProxy 상단에 추가**
    
    + 장점: 구현이 간단하고, 코드 수정이 없음

    + 단점: DB connection이 오래동안 유지되므로 리소스 관리에 안 좋고, 트래픽이 많은 경우에 커넥션 풀이
         고갈 될 수 있다.

  + **Solution3:SecurityContextHolder의 Authentication에 저장되는 Principal을 UserDetails객체가 아닌 인증/인가를 위한 DTO로 변경**
    
    + 장점: OSIV를 해제 할 수 있고, 보안상 안전하게 관리 가능
  
    + 단점 : 서비스단과 필터의 코드를 전체적으로 수정해야 하고, 서비스단에서 UserDetails 객체가 사용될 시 
           DB에서 다시 읽어와야 한다.

  + **Choice : Solution 3**
View를 사용하지 않고 REST API로 데이터를 배포하는 서비스에서 OSIV 패턴을 사용하여 DB 커넥션을 유지하는 것을 피하고자 하였고,
 인증/인가를 위한 DTO의 프로퍼티에 UserDetails의 PK나 Email등의 프로퍼티를 추가하면 서비스단에서 중복적인 로드를 최소화 할 수 있으므로 장기적인 관점에서 리팩토링을 하는것이 적절하다고 판단하였다





## 7. ERD
![데이터베이스](https://github.com/YoonSeungKwon/Capstone1/blob/master/pding3.png)

## 8. 결과물

![경진대회](https://github.com/YoonSeungKwon/Capstone1/blob/master/pding6.png)
![과제전](https://github.com/YoonSeungKwon/Capstone1/blob/master/pding7.png)

## 9. 링크
+ **프론트엔드**: <https://github.com/YoonSeungKwon/PdingFE>


+ **포트폴리오**: <https://sites.google.com/view/cau-artech/3-2/%EC%BA%A1%EC%8A%A4%ED%86%A4%EB%94%94%EC%9E%90%EC%9D%B8-%ED%94%84%EB%A1%9C%EC%A0%9D%ED%8A%B81/2023/%ED%94%84%EB%94%A9>
