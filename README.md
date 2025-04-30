![로고](https://github.com/YoonSeungKwon/Capstone1/blob/master/pding4.png)

# 🎁 프딩 (Pding) — Present Funding

> **원하는 선물을 등록하고 친구들과 함께 펀딩하는 웹 서비스**

프딩은 사용자가 받고 싶은 선물을 등록하면 친구들이 함께 금액을 채워주는 **크라우드 펀딩 기반 모바일 중심 웹 서비스**입니다. 기존 선물 문화의 번거로움과 비효율을 개선하고, 더 나은 선물 경험을 제공합니다.

---

## 🔗 링크

- 📌 [성능 개선 기술 노트](https://www.notion.so/1cbaf94f960c80fa97f8d71034f264e1)
- 💻 [프론트엔드 GitHub](https://github.com/YoonSeungKwon/PdingFE)

---

## 👥 팀 구성 (3인)

| 이름       | 역할                         |
|------------|------------------------------|
| 윤상희     | 팀장, 기획, 프론트엔드 개발   |
| 기나연     | UX/UI 디자인                  |
| 윤승권     | 백엔드 개발, 프론트엔드 지원 |

---

## ⚙️ 기술 스택

- **Frontend**: JavaScript, React, Nginx  
- **Backend**: Java 17, Spring Boot 3.3, JPA (Hibernate)  
- **Infra**: MariaDB, AWS EC2, S3, Docker, RabbitMQ  
- **API**: Kakao Oauth2, Kakao Pay  
- **Collaboration**: Figma, Notion, Git, Swagger  

---

## 🌟 주요 특징

### 1. 기술 확장을 고려한 설계
- 서비스 레이어에서 추상화를 통해 구조와 기능을 분리  
- 기술 교체 및 확장에 유연한 구조  

### 2. 테스트 중심 개발
- 외부 서비스 및 객체 추상화
- 빌더 패턴 활용으로 단위 테스트의 효율성 확보

---

## 🛠️ 성능 개선 및 문제 해결

### ✅ 1. 응답 속도 개선
- **Caffeine 캐시**  
  펀딩 글 등록 시 마감일까지 TTL 설정하여 엔티티 캐싱 → 빠른 결제 검증 처리  
- **RabbitMQ 도입**  
  주문 정보 WRITE 작업을 안전하게 버퍼링, 서버 재시작에도 데이터 손실 없이 처리  
- **DB 튜닝**  
  복합 인덱스 설정 및 정렬 최소화로 장기적인 쿼리 효율 향상  
- **성능 향상 결과**  
  RabbitMQ 도입 후 단일 요청 기준 평균 주문 처리 시간 **약 100ms → 50ms로 개선**

### ✅ 2. OSIV 문제 해결
- 인증에 사용되던 엔티티가 필터에서 사용되며 `LazyInitializationException` 발생  
- 인증용 DTO 분리 및 쿼리에서 직접 DTO로 조회하도록 구조 리팩터링  

### ✅ 3. 단위 테스트 개선
- Repository와 강한 결합 → 추상화 후 Fake Repository 구현  
- 내부 컬렉션 기반으로 독립적 테스트 가능하게 설계  

---

## 📊 ERD

![ERD](https://github.com/YoonSeungKwon/Capstone1/blob/master/pding3.png)

---

## 🏆 결과물

![경진대회](https://github.com/YoonSeungKwon/Capstone1/blob/master/pding6.png)

---

