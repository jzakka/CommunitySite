# CommunitySite [![Build Status](https://app.travis-ci.com/Jzakka/CommunitySite.svg?branch=main)](https://app.travis-ci.com/Jzakka/CommunitySite)

데모사이트는 [여기](http://communitys1te.com)

프로젝트 이름: CommunitySite <br/>
개요: 다양한 주제로 커뮤니티를 하는 포럼형 커뮤니티  <br/>
기술 스택: AWS EC2, Spring Boot, JPA, MariaDB <br/>
구현된 기능: <br/>
1. 로그인/로그아웃 <br/>
2. 관리자에 의한 포럼 생성 <br/>
3. 비회원/회원의 권한을 구분한 CRUD <br/>
4. 포럼내 카테고리 생성/삭제 <br/>
5. 게시글 페이징 <br/>
6. 추천/비추천 <br/>

***

## 모델 관계
![CommunitySite_project](https://user-images.githubusercontent.com/105845911/214726618-c8ed6e91-e49a-4fb0-a591-8d1d2936c8ff.jpg)

## 로비
<img width="1512" alt="스크린샷 2023-01-26 오전 9 35 00" src="https://user-images.githubusercontent.com/105845911/214727049-f524150e-ec4f-4e95-b19b-e36de5199118.png">

포럼이름에 M이 붙은건 마이너게시판으로 일반 사용자인 매니저가 관리하는 게시판<br/>

## 로그인
<img width="579" alt="스크린샷 2022-10-23 오전 11 37 00" src="https://user-images.githubusercontent.com/105845911/197371752-9ef0f609-e719-45a6-a530-cfa8a10ed046.png"/>

## 게시판
<img width="1494" alt="스크린샷 2022-10-23 오후 12 06 59" src="https://user-images.githubusercontent.com/105845911/197371758-b15298b2-f403-4ee9-b30b-64f4a6ccc044.png"/>
<img width="752" alt="스크린샷 2022-10-23 오후 12 10 35" src="https://user-images.githubusercontent.com/105845911/197371835-028a6a83-e455-4dbb-af32-18dd99bcdb3f.png"/>
마이너 게시판은 매니저로 로그인 할 시 카테고리 생성 탭이 따로 있음

## 게시글
<img width="1489" alt="스크린샷 2022-10-23 오후 12 31 51" src="https://user-images.githubusercontent.com/105845911/197372125-632bd2f8-cd03-4cd7-96ef-4a8de55cbade.png"/>
<img width="1500" alt="스크린샷 2022-10-23 오후 12 32 27" src="https://user-images.githubusercontent.com/105845911/197372144-f679e6e3-e1c5-4155-a8c7-3a41fc6679c0.png"/>
추천/비추천은 1일 1회만 가능하다<br/>
외부라이브러리 이용(링크: https://github.com/jhalterman/expiringmap)<br/>

## 게시글 수정
<img width="1491" alt="스크린샷 2022-10-23 오후 12 07 32" src="https://user-images.githubusercontent.com/105845911/197371891-34edbfbf-30e8-4ade-bbce-7a33308be4f2.png"/>
<img width="592" alt="스크린샷 2022-10-23 오후 12 07 43" src="https://user-images.githubusercontent.com/105845911/197371897-314f6e44-3542-4c79-b63d-45e22144a8a6.png"/>
비회원 사용자의 글은 누구나 비밀번호만 치면 누구나 수정/삭제 가능<br/>
일반 사용자의 글은 글쓴이, 매니저, 관리자 계정으로만 삭제가능
