# Table-For-You

식당 예약 관리 서비스 서버입니다.

## ✨ Features
### 권한별
 - 관리자
     - 사용자 관리
     - 식당 등록 승인 및 관리
 - 식당 주인
     - 식당 생성
     - 예약자 관리
 - 사용자
     - 식당 예약 (번호표 & 시간)
     - 리뷰 생성
     - 방문한 식당 관리
     - 관심 식당 관리
  
### 공통
 - 회원가입
 - 로그인
 - 로그아웃
 - 알림 (FCM)

## 🚀 Tech Stacks
 - Language: Java
 - Build tool: Gradle
 - Frameworks & Libraries: Spring Boot, Spring Data JPA, Spring Security, Lombok, JJWT, Redis(Redisson), firebase
 - Databases & Caching: MySQL, Redis
 - DevOps & Cloud: GitHub Actions, Docker, AWS

## 🏗️ Infra Architecture
![table-for-you](https://github.com/user-attachments/assets/57756369-611d-4a1e-a282-612ea2e72ebd)


## ERD
<img width="968" alt="table-for-you-erd" src="https://github.com/user-attachments/assets/5c2d8401-b8a0-4b35-9218-1172f8c147ef" />

## 예약 과정
해당 코드 위치 - `src/main/java/com/project/tableforyou/domain/reservationrefactor/**`

### 대기열 처리 ([PR](https://github.com/table-for-you/backend/pull/32))

 - 사용자 대기열 입장 -> 대기열 순번 부여 -> 입장 시, 예약 페이지 접근 -> 예약 진행

|<img width="421" height="311" alt="대기열" src="https://github.com/user-attachments/assets/a03b46bd-c1cc-4274-8a26-28816c8571b7" />|<img width="391" height="322" alt="기존 예약" src="https://github.com/user-attachments/assets/faf23b55-0cba-4274-a00c-50efbb63a605" />|
|---|---|
|대기열 입장 처리|예약 페이지 입장 후|

---

### 예약 생성 Sequence Diagram ([PR](https://github.com/table-for-you/backend/pull/31))

|<img width="571" height="743" alt="queue-예약" src="https://github.com/user-attachments/assets/13cff3ad-5760-46c7-8d18-0b84808e5605" />|<img width="703" height="815" alt="timeslot-예약" src="https://github.com/user-attachments/assets/443b3fe7-eefe-4c39-81a3-138f6d2de4c3" />|
|---|---|
| 번호표(Queue) 예약 생성 | 시간대(TimeSlot) 예약 생성 |

---

### 예약 조회 Sequence Diagram

|<img width="383" height="629" alt="queue-조회1" src="https://github.com/user-attachments/assets/50158f42-1111-47d1-bd00-f1d8f4a57330" />|<img width="862" height="524" alt="timeslot-조회" src="https://github.com/user-attachments/assets/4a0d799c-0526-4786-80bd-595cc6770bff" />|
|---|---|
| 번호표(Queue) 예약 조회 | 시간대(TimeSlot) 예약 조회 |

---

### 예약 취소 Sequence Diagram

|<img width="869" height="395" alt="queue-취소" src="https://github.com/user-attachments/assets/b56a77ab-d8ba-4aff-bf80-c62a17460c95" />|<img width="971" height="349" alt="timeslot-취소" src="https://github.com/user-attachments/assets/8d125424-050a-4d63-895d-bb1eac52753d" />|
|---|---|
| 번호표(Queue) 예약 취소 | 시간대(TimeSlot) 예약 취소 |

---

### 예약 입장 Sequence Diagram

|<img width="795" height="362" alt="queue-입장" src="https://github.com/user-attachments/assets/6cfc0d94-0db0-46c2-a481-efb8b7525f93" />|<img width="959" height="362" alt="timeslot-입장" src="https://github.com/user-attachments/assets/c627488a-fffb-4abb-ac5a-7653242f9dc7" />|
|---|---|
| 번호표(Queue) 예약 입장 | 시간대(TimeSlot) 예약 입장 |
