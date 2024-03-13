프딩 Pding

1. 프로젝트 개요 
  프딩(프레젠트 펀딩, Present Funding)은 원하는 선물을 등록하면 친구들이 그 금액을 채워주는 크라우드 펀딩 형태의 모바일 중접 웹 서비스입니다.
  기존 선물 문화의 문제를 해결하고, 사용자들이 더 나은 선물 경험을 즐길 수 있도록 돕기 위해 제작되었습니다.

2. 개발 기간 2023-09 ~ 2023-12 3개월

2. 팀 구성 (3인)
  <ul>
    <li>팀장 윤상희: 기획, 프론트엔드 개발</li>
    <li>팀원 기나연: UX/UI 디자인</li>
    <li>팀원 윤승권: 프론트엔드, 백엔드 개발</li>
  </ul> 

 3. 개발 환경 및 기술
  <ul>
    <li>협업:     Figma, Notion, Git, Swagger</li>
    <li>언어:     Java(jdk 17.x), html/css, javascript</li>
    <li>FE:       React, Nginx</li>
    <li>BE:       Spring Boot, Mybatis, Jpa</li>
    <li>DB:       MariaDB, Redis, AWS RDS</li>
    <li>SEVER:    Apache Tomcat(10.1.x), AWS EC2, AWS S3, Docker</li>
    <li>API:      KAKAO Oauth2, KAKAO PAY</li>
  </ul>

 4. 주요 기술 구현
  <ul>
    <li>회원가입: 폼 회원가입은 dto에 validation을 적용하여 무결성에 문제가 일어나지 않도록 하였고, validation 그룹들을 시퀀스를 통하여 순서상 문제가 일어나지 않도록 관리하였다. 비밀번호는 암호화를 통하여 DB에 저장하고 프로필은 아마존 S3에 사용자 개별의 디렉토리에 저장한다.</li>
    <li>로그인: 폼 로그인은 jjwt의 라이브러리를 활용하여 Access 토큰은 Authorization Header, Refresh 토큰은 커스텀 헤더에 전달한다. 프론트 엔드에서 request 인터셉터를 이용하여 api요청 헤더에 Access 토큰을 전달하고, 만료되었을 경우 인증 필터 앞에 위치한 JwtException필터에서 프론트로 401에러를 전달하면 다시 프론트엔드에서 Refresh토큰을 Access토큰과 같이 전달하여 새로운 Access 토큰을 발급 받는다. 소셜 로그인의 경우 KAKAO인증이 완료되면 KAKAO로 Access 토큰을 보내 사용자의 정보를 받아와 DB에 저장한다. 인증이 성공하면 SecurityContextHolder에 UsernamePasswordAuthenticationToken을 저장하여 서비스에서 사용할 수 있도록 한다.</li>
    <li>친구신청: 친구 신청은 행위자에 따라 toUser(받는 사람)와 fromUser(보내는 사람)로 구분하여 isFriend(상태)를 DB에 저장하고, 친구 수락시 toUser와 fromUser가 바뀐 복사본을 만들고 두 레코드의 상태를 업데이트 하여 친구 검색시 오류가 나지 않도록 한다.</li>
    <li>친구목록: 친구 목록을 db로 부터 불러올 때 Look Aside 캐싱 전략을 통하여 유저별 FriendList를 Docker의 공식 Redis 이미지로 생성한 컨테이너에 저장하였고, 친구 수락이나 삭제를 할 경우 @CachePut을 통하여 데이터 정합성에 문제가 일어나지 않도록 한다.</li>
    <li>프로젝트 생성: 프로젝트는 링크를 통하여 원하는 상품의 링크를 붙여넣어 크롤링을 통하여 항목들을 자동으로 채우려 했으나, 아직은 하나하나 작성하여 CRUD가 진행되는 방식으로 진행하였다.</li>
    <li>프로젝트 불러오기: 프로젝트 또한 자주 변하지 않지만 많이 사용되는 데이터라고 생각하여 Look Aside 읽기 전략을 통하여 projectList의 유저별 키를 가진 Redis 저장소에 캐싱하였고, 프로젝트를 생성하면 마찬가지로 @CachePut어노테이션을 통하여 캐시 저장소를 갱신하였다.</li>
    <li>펀딩하기: 펀딩하기는 더 나은 사용자 경험을 위하여 KAKAO PAY의 간편 결제 API를 이용하였고, 사업자 번호를 등록하지 않은 버전으로 결제 준비 과정과 결제 승인 과정을 공식문서에 따라서 올바른 절차로 진행되고, 상태에 따라 올바른 페이지로 리다이렉트 되도록 설계하였다.</li>
  </ul>
  
 5. 결과물
