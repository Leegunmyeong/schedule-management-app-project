# 일정 관리 앱

## 1. 프로젝트 소개
> Spring Boot, JPA를 활용하여 일정을 생성, 조회, 수정, 삭제할 수 있는 REST API프로젝트입니다
>  3 Layer Architecture(Controller, Service, Repository)를 적용하여 계층별 역할을 분리하고,
> 데이터베이스에 일정 정보를 저장하고 관리합니다.

## 2. API 명세서

| 기능           | HTTP Method | URL                     | 상태코드       |
|----------------|-------------|-------------------------|----------------|
| 일정 생성      | POST        | /schedules              | 201 Created    |
| 선택 일정 조회 | GET         | /schedules/{scheduleId} | 200 OK         |
| 전체 일정 조회 | GET         | /schedules              | 200 OK         |
| 일정 수정      | PATCH       | /schedules/{scheduleId} | 200 OK         |
| 일정 삭제      | DELETE      | /schedules/{scheduleId} | 204 No Content |


## 공통 데이터 명세

| 필드명    | JAVA 타입     | JSON 타입         | 설명                                                             |
|-----------|---------------|-------------------|------------------------------------------------------------------|
| id        | Long          | Number            | 일정 고유 식별자                                                 |
| title     | String        | String            | 일정 제목                                                        |
| detail    | String        | String            | 일정 내용                                                        |
| name      | String        | String            | 작성자명                                                         |
| pw        | String        | String            | 일정 생성 시 설정 및 수정, 삭제 시 본인 확인에 사용하는 비밀번호 |
| createdAt | LocalDateTime | String (DateTime) | 일정 최초 작성일                                                 |
| updatedAt | LocalDateTime | String (DateTime) | 일정 마지막 수정일                                               |

- `id`는 일정 생성 시 자동으로 생성됩니다
- `pw`는 일정 생성 요청 시 필수이며, API 응답에는 포함하지 않습니다
- `createdAt`과`updatedAt`은 JPA Auditing으로 자동 관리합니다
- 최초 생성 시 `createdAt`과`updatedAt`은 동일합니다
- 날짜와 시간은 ISO 8601 형식의 문자열로 표현합니다

---

### 2.1 일정 생성

- **HTTP Method:** `POST`
- **URL:** `/schedules`
- **Status:** `201 Created`

#### Request Body 명세

| 필드명 | 필수 여부 | 설명                         |
|--------|-----------|------------------------------|
| title  | 필수      | 일정 제목                    |
| detail | 필수      | 일정 내용                    |
| name   | 필수      | 작성자명                     |
| pw     | 필수      | 최초 생성 시 설정할 비밀번호 |

- 데이터 타입은 공통 데이터 명세 참고

#### Request JSON

```json
{
  "title": "에이타니",
  "detail": "아침에 에이타니 문제 풀기",
  "name": "홍길동",
  "pw": "1234567"
}
```

#### Response Body 명세

| 필드명    | 필수 여부 | 설명               |
|-----------|-----------|--------------------|
| id        | 필수      | 일정 고유 식별자   |
| title     | 필수      | 일정 제목          |
| detail    | 필수      | 일정 내용          |
| name      | 필수      | 작성자명           |
| createdAt | 필수      | 일정 최초 작성일   |
| updatedAt | 필수      | 일정 마지막 수정일 |

#### Response JSON

```json
{
  "id": 1,
  "title": "에이타니",
  "detail": "아침에 에이타니 문제 풀기",
  "name": "홍길동",
  "createdAt": "2026-10-14T09:00:00",
  "updatedAt": "2026-10-14T09:00:00"
}
```
#### 생성 조건

- 일정 ID는 자동 생성
- 작성일/수정일 자동으로 저장
- 최초 생성 시 작성일/수정일 동일
- 비밀번호는 Response에 포함 ❌

---

### 2.2 선택 일정 조회

- **HTTP Method:** `GET`
- **URL:** `/schedules/{scheduleId}`
- **Status:** `200 OK`

**Path Variable**

| 변수이름   | 타입 | 필수여부 | 설명           |
|------------|------|----------|----------------|
| scheduleId | Long | 필수     | 조회할 일정 ID |

- Request Body 없음

#### Response Body 명세

| 필드명    | 필수 여부 | 설명               |
|-----------|-----------|--------------------|
| id        | 필수      | 일정 고유 식별자   |
| title     | 필수      | 일정 제목          |
| detail    | 필수      | 일정 내용          |
| name      | 필수      | 작성자명           |
| createdAt | 필수      | 일정 최초 작성일   |
| updatedAt | 필수      | 일정 마지막 수정일 |

#### Response JSON

```json
{
  "id": 1,
  "title": "에이타니",
  "detail": "아침에 에이타니 문제 풀기",
  "name": "홍길동",
  "createdAt": "2026-10-14T09:00:00",
  "updatedAt": "2026-10-14T09:00:00"
}
```

#### 조회 조건

- 일정 ID로 특정 일정을 조회
- 비밀번호는 응답에 포함 ❌

---

### 2.3 전체 일정 조회

- **HTTP Method:** `GET`
- **URL:** `/schedules`
- **Status:** `200 OK`

**Query Parameter**

| 이름 | 타입   | 필수여부 | 설명                   |
|------|--------|----------|------------------------|
| name | String | 선택     | 조회할 일정의 작성자명 |

- 전체 조회: `/schedules`
- 작성자별 조회: `/schedules?name=홍길동`

- Request Body 없음

#### Response Body 명세

| 필드명       | 필수 여부 | 설명               |
|--------------|-----------|--------------------|
| scheduleList | 필수      | 조회된 일정 목록   |
| id           | 필수      | 일정 고유 식별자   |
| title        | 필수      | 일정 제목          |
| detail       | 필수      | 일정 내용          |
| name         | 필수      | 작성자명           |
| createdAt    | 필수      | 일정 최초 작성일   |
| updatedAt    | 필수      | 일정 마지막 수정일 |

#### Response JSON

```json
{
  "scheduleList": [
    {
      "id": 2,
      "title": "코드카타",
      "detail": "프로그래머스 문제 풀어보기",
      "name": "홍길동",
      "createdAt": "2026-10-14T15:00:00",
      "updatedAt": "2026-10-14T17:00:00"
    },
    {
      "id": 1,
      "title": "에이타니",
      "detail": "아침에 에이타니 문제 풀기",
      "name": "홍길동",
      "createdAt": "2026-10-14T09:00:00",
      "updatedAt": "2026-10-14T09:00:00"
    }
  ]
}

```

#### 조회 조건

- 작성자명이 없으면 모든 일정을 조회
- 작성자명이 있으면 해당 작성자의 일정만 조회
- 수정일 기준 내림차순으로 정렬(가장 최근에 수정한게 맨 위로)
- 비밀번호는 응답에 포함 ❌
- 조회 결과가 없으면 빈 배열 `"scheduleList": []`을 반환

---

### 2.4 일정 수정

- **HTTP Method:** `PATCH`
- **URL:** `/schedules/{scheduleId}`
- **Status:** `200 OK`

**Path Variable**

| 변수이름   | 타입 | 필수여부 | 설명           |
|------------|------|----------|----------------|
| scheduleId | Long | 필수     | 수정할 일정 ID |

#### Request Body 명세

| 필드명 | 필수 여부 | 설명                             |
|--------|-----------|----------------------------------|
| title  | 필수      | 일정 제목                        |
| name   | 필수      | 작성자명                         |
| pw     | 필수      | 최소 생성 했을때 등록된 비밀번호 |


**Request JSON**

```json
{
  "title": "Spring 과제하기",
  "name": "도라에몽",
  "pw": "1234567"
}
```

#### Response Body 명세

| 필드명    | 필수 여부 | 설명               |
|-----------|-----------|--------------------|
| id        | 필수      | 일정 고유 식별자   |
| title     | 필수      | 일정 제목          |
| detail    | 필수      | 일정 내용          |
| name      | 필수      | 작성자명           |
| createdAt | 필수      | 일정 최초 작성일   |
| updatedAt | 필수      | 일정 마지막 수정일 |

#### Response Body

```json
{
  "id": 1,
  "title": "Spring 과제하기",
  "detail": "아침에 에이타니 문제 풀기",
  "name": "도라에몽",
  "createdAt": "2026-10-14T09:00:00",
  "updatedAt": "2026-10-14T10:00:00"
}
```

#### 수정 조건

- 일정 제목과 작성자명만 수정 가능
- 비밀번호가 일치해야 수정 가능
- 작성일은 변경 ❌
- 수정일은 수정 완료 시점으로 변경
- 비밀번호는 응답에 포함 ❌

---

### 2.5 일정 삭제

- **HTTP Method:** `PATCH`
- **URL:** `/schedules/{scheduleId}`
- **Status:** `200 OK`

**Path Variable**

| 변수이름   | 타입 | 필수여부 | 설명           |
|------------|------|----------|----------------|
| scheduleId | Long | 필수     | 삭제할 일정 ID |


#### Request Body 명세

| 필드명 | 필수 여부 | 설명                               |
|--------|-----------|------------------------------------|
| pw     | 필수      | 비밀번호 일치 여부 확인용 비밀번호 |


**Request JSON**
```json
{
  "pw": "1234567"
}
```

#### Response Body 명세

| 필드명  | 필수 여부 | 설명                                        |
|---------|-----------|---------------------------------------------|
| message | 필수      | 삭제 성공했을 때의 사용자에게 전달할 메세지 |


#### Response Body
```json
{
  "message": "일정이 삭제 되었습니다!"
}
```

#### 삭제 조건

- 일정 ID로 삭제할 일정 선택
- 비밀번호가 일치해야 삭제 가능

---

## 3. ERD
![일정 관리 앱 ERD](./images/일정%20관리%20앱%20ERD.png)
