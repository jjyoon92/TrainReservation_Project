# TrainReservation-Server

# SRT Clone 프로젝트

## 1. 프로젝트 개요
- 프로젝트명: 기차 예매 앱
- 목적: 사용자가 편리하게 기차 예매를 할 수 있는 안드로이드 애플리케이션 제작
- 팀 구성: 4명 (프로젝트 매니저, 안드로이드 개발자, 백엔드 개발자, 디자인 개발자)
- 프로젝트 기간: 2개월 (2023년 5월 15일부터 2023년 06월 30일까지)

## 2. 요구 사항 분석
- 사용자 관리 기능: 회원가입, 로그인, 비밀번호 변경, 회원번호 찾기, 이메일 인증
- 예매 기능: 출발역, 도착역, 출발일, 편도/왕복 선택, 기차시간표 조회, 좌석 예매, 예약 취소, 결제, 환불
- 게시판 기능 : 저장된 게시판 정보를 조회할 수 있으며 키워드를 통해 원하는 정보만 추줄할 수 있다.
- 추가 기능: 관광지 추천, 커뮤니티 기능, 환승 시스템, 알림 기능 (미구현)

## 3. 설계 및 아키텍처
- 시스템 아키텍처: 안드로이드 애플리케이션과 스프링부트 웹 애플리케이션의 클라이언트-서버 구조
- 데이터베이스 구조: 마리아DB를 사용하여 사용자 정보, 예매 정보, 시간표 등을 저장하는 데이터베이스 구조 설계
- 외부 API : 공공데이터 포털에서 제공하는 열차 정보 API를 사용

## 4. 개발 과정
- 애자일 스크럼 방법론을 적용하여 2주마다 스프린트를 진행
- 각 스프린트마다 개발, 테스트, 검토 등의 과정 수행

## 5. 구현된 기능

### 회원

    - 회원가입: 사용자 정보를 입력받아 데이터베이스에 저장   
    - 로그인: 등록된 사용자 정보로 로그인 기능 제공   
    - 비밀번호 변경: 사용자가 비밀번호를 변경할 수 있는 기능
    - 회원번호 찾기 : 사용자의 회원번호를 찾을 수 있는 기능   
    - 이메일 인증 : 입력받은 이메일에 인증 코드를 요청할 수 있으며 요청받은 인증코드와 입력된 인증코드를 비교 가능

### 열차 예매

    - 예매 기능: 출발역, 도착역, 출발일, 편도/왕복 선택에 따라 기차시간표 조회 및 좌석 예매 가능    
    - 예약 취소: 예매한 좌석을 취소하고 예매 내역을 업데이트    
    - 결제: 예매한 좌석에 대한 결제 기능    
    - 환불: 예매를 취소하고 결제된 금액을 환불하는 기능 (미구현)

### 게시판

    - 게시판 조회 : 저장된 게시판 목록들을 조회할 수 있다
    - 게시판 검색 : 키워들 통해 원하는 게시판 정보들을 조회 할 수 있다    
    - 게시판 상세 : 원하는 목록을 선택하여 해당 게시물에 대한 상세한 정보를 알 수 있다


## 6. 성과 및 성능 평가
- 프로젝트 목표 중 회원가입, 로그인, 예매 기능, 게시판 기능 등을 성공적으로 구현
- 기능에 대한 사용자 테스트 결과, 사용자들이 편리하게 기차 예매를 할 수 있었음
- 성능 측정 결과, 애플리케이션의 응답 시간과 처리량이 적절한 수준임을 확인

## 7. 사용자 피드백 및 개선 사항
- 사용자 테스트 결과를 바탕으로 피드백을 수집하였으며, 알림 기능과 관광지 추천 기능의 필요성이 나타남
- 개선 사항: 알림 기능 추가, 관광지 추천 기능 구현, 성능 향상을 위한 최적화 작업

## 8. 프로젝트 결론
- 기차예매 어플 프로젝트는 안드로이드와 스프링부트를 활용하여 성공적으로 개발되었음
- 사용자들에게 편리한 기차 예매 서비스를 제공하고, 추가 기능의 구현을 위한 계획을 수립함

## 9. 참고 문서
- 스프링부트 공식 문서: https://spring.io/projects/spring-boot
- 안드로이드 개발자 문서: https://developer.android.com/docs
- 코틀린 개발자 문서 : https://kotlinlang.org/docs/home.html

## 10. 서버 
- Git URL : https://github.com/kktyal/TrainReservation-Server


### 타이틀
![portfolio01.png](screenshots%2Fportfolio01.png)
### 목차
![portfolio02.png](screenshots%2Fportfolio02.png)
### 팀원 소개
![portfolio03.png](screenshots%2Fportfolio03.png)
### 프로젝트 소개
![portfolio04.png](screenshots%2Fportfolio04.png)
### 방법론
![portfolio05.png](screenshots%2Fportfolio05.png)
### 세부 일정
![portfolio06.png](screenshots%2Fportfolio06.png)
### 언어 및 개발 도구
![portfolio07.png](screenshots%2Fportfolio07.png)
### 시스템 흐름도(회원가입)
![portfolio08.png](screenshots%2Fportfolio08.png)
### 시스템 흐름도(회원번호, 비밀번호 찾기)
![portfolio09.png](screenshots%2Fportfolio09.png)
### 시스템 흐름도(열차 예매)
![portfolio10.png](screenshots%2Fportfolio10.png)
### 시스템 흐름도(승차권 확인)
![portfolio11.png](screenshots%2Fportfolio11.png)
### 앱 디자인(와이어 프레임)
![portfolio12.png](screenshots%2Fportfolio12.png)
### 앱 디자인(목업)
![portfolio13.png](screenshots%2Fportfolio13.png)
### 앱 디자인(실제 실행 화면)
![portfolio14.png](screenshots%2Fportfolio14.png)
### 화면 구성
![portfolio15.png](screenshots%2Fportfolio15.png)
### 유스케이스
![portfolio16.png](screenshots%2Fportfolio16.png)
### ERD
![portfolio17.png](screenshots%2Fportfolio17.png)
### 기능 명세서(로그인)
![portfolio18.png](screenshots%2Fportfolio18.png)
### 기능 명세서(기차 조회)
![portfolio19.png](screenshots%2Fportfolio19.png)
### 기능 명세서(기차 예약)
![portfolio20.png](screenshots%2Fportfolio20.png)
### 기능 명세서(승차권 확인, 결제, 게시판)
![portfolio21.png](screenshots%2Fportfolio21.png)
### 고민 & 문제 해결
![portfolio22.png](screenshots%2Fportfolio22.png)
### 향후 계획
![portfolio23.png](screenshots%2Fportfolio23.png)