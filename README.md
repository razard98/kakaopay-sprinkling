# Sprinkling REST API Document

뿌리기, 받기, 조회 기능을 수행하는 REST API를 제공합니다.


## Install

    mvn install

## Run the app

    mvn spring-boot:run

## Run the tests

    mvn test

# Specification
* JAVA8
* SpringBoot 2.3.3.RELEASE
* Junit5
* jacoco
* JPA
* H2 1.4.2

# DB Schema
* 1:N

![db-diagram](https://user-images.githubusercontent.com/33849726/95014558-a80ef600-0682-11eb-9065-84c068b1c632.png)

# REST API

요청한 사용자의 식별값은 숫자 행태 이며 `X-USER-ID`라는 HTTP Header로 전달합니다.
요청한 사용자가 속한 대화방의 식별값은 문자 행태이며 `X-ROOM-ID`라는 HTTP Header로 전달합니다.

모든 결과 데이터는 `application/json` 으로 제공됩니다.

## 뿌리기 API
 
 * 뿌릴 금액, 뿌릴 인원을 요청값으로 받습니다. 
 * 뿌리기 요청건에 대한 고유 token을 발급하고 응답값으로 내려줍니다. 
 * 뿌릴 금액을 인원수에 맞게 분배하여 저장합니다. 
 * token은 3자리 문자열로 구성되며 예측이 불가능 합니다. 
 
### Request

`POST /api/v1/sprinkling`

    curl --location --request POST 'http://127.0.0.1:8080/api/v1/sprinkling' \
    --header 'X-ROOM-ID: 1' \
    --header 'X-USER-ID: 1' \
    --header 'Accept: application/json' \
    --header 'Content-Type: application/json' \
    --data-raw '{"count" : 5, "amount": 100}'

### Response

    HTTP/1.1 201 Created
    Location: /tokens/sUv
    Content-Type: application/json
    Transfer-Encoding: chunked
    Date: Sun, 04 Oct 2020 08:28:06 GMT
    Keep-Alive: timeout=60
    Connection: keep-alive

    {"token":"sUv"}

* **Error Response:**

  * **Code:** 400 Bad Request <br />
    **Content:**
     
        {
            "timestamp": "2020-10-04T18:20:50.22",
            "status": 400,
            "error": "Bad Request",
            "message": "받을 사용자 수가 뿌릴 금액 보다 클 수는 없습니다.",
            "path": "/api/v1/sprinkling"
        }

## 받기 API

* 뿌리기 시 발급된 token을 요청값으로 받습니다.
* token에 해당하는 뿌리기 건 중 아직 누구에게도 할당되지 않은 분배건 하나를 API를 호출한 사용자에게 할당하고, 
그 금액을 응답값으로 내려줍니다.
* 뿌리기 당 한 사용자는 한번만 받을 수 있습니다.
* 자신이 뿌리기한 건은 자신이 받을 수 없습니다.
* 뿌린기가 호출된 대화방과 동일한 대화방에 속한 사용자만이 받을 수 있습니다. 
* 뿌린 건은 10분간만 유효합니다. 뿌린지 10분이 지난 요청에 대해서는 받기 실패 응답이 내려갑니다. 
### Request

`PUT /api/v1/sprinkling/tokens/sUv`

    curl --location --request PUT 'http://127.0.0.1:8080/api/v1/sprinkling/tokens/sUv' \
    --header 'X-ROOM-ID: 1' \
    --header 'X-USER-ID: 6'

### Response

    HTTP/1.1 201 Created
    Content-Type: application/json
    Transfer-Encoding: chunked
    Date: Sun, 04 Oct 2020 08:34:57 GMT
    Keep-Alive: timeout=60
    Connection: keep-alive

    {
        "amount": 32.00
    }
* **Error Response:**

  * **Code:** 403 Forbidden <br />
    **Content:**
     
        {
           "timestamp": "2020-10-04T17:51:37.861",
           "status": 403,
           "error": "Forbidden",
           "message": "뿌리기 당 한번만 받을 수 있습니다.",
           "path": "/api/v1/sprinkling/tokens/KLI"
         }
       
        {
          "timestamp": "2020-10-04T17:51:37.861",
          "status": 403,
          "error": "Forbidden",
          "message": "뿌린 건은 10분간만 유효합니다.",
          "path": "/api/v1/sprinkling/tokens/KLI"
        }
        
        {
          "timestamp": "2020-10-04T17:51:37.861",
          "status": 403,
          "error": "Forbidden",
          "message": "뿌리기가 호출된 대화방과 동일한 대화방에 속한 사용자만 받을 수 있습니다.",
          "path": "/api/v1/sprinkling/tokens/KLI"
        }
        
        {
          "timestamp": "2020-10-04T17:51:37.861",
          "status": 403,
          "error": "Forbidden",
          "message": "자신이 뿌리기한 건은 자신이 받을 수 없습니다.",
          "path": "/api/v1/sprinkling/tokens/KLI"
        }
    
        {
            "timestamp": "2020-10-04T18:16:28.817",
            "status": 403,
            "error": "Forbidden",
            "message": "뿌리기가 모두 완료 되어, 받기에 실패하였습니다.",
            "path": "/api/v1/sprinkling/tokens/fON"
        }
        
        
## 조회 API
* 뿌리기 시 발급된 token을 요청값으로 받습니다.
* token에 해당하는 뿌리기 건의 현재 상태를 응답값으로 내려줍니다. 현재 상태는 다음의 정보를 포함합니다. 
  `뿌린 시각, 뿌린 금액, 받기 완료된 금액, 받기 완료된 정보 [받은 금액, 받은 사용자 아이디] `리스트) 
* 뿌린 사람 자신만 조회를 할 수 있습니다. 
* 다른사람의 뿌리기건이나 유효하지 않은 token에 대해서는 조회 실패 응답이 내려가야 합니다. 
* 뿌린 건에 대한 조회는 7일 동안 할 수 있습니다
### Request

`GET /api/v1/sprinkling/tokens/sUv`

    curl --location --request GET 'http://127.0.0.1:8080/api/v1/sprinkling/tokens/sUv' \
    --header 'X-ROOM-ID: 1' \
    --header 'X-USER-ID: 1'

### Response

    HTTP/1.1 200 OK
    Content-Type: application/json
    Transfer-Encoding: chunked
    Date: Sun, 04 Oct 2020 08:40:18 GMT
    Keep-Alive: timeout=60
    Connection: keep-alive

    {
        "amount": 100.00,
        "createdAt": "2020-10-04T17:46:23.312",
        "assigns": [
            {
                "userId": 6,
                "amount": 8.00,
                "assignedAt": "2020-10-04T17:46:36.927"
            },
            {
                "userId": 2,
                "amount": 4.00,
                "assignedAt": "2020-10-04T17:46:52.278"
            }
        ]
    }
* **Error Response:**

  * **Code:** 403 Forbidden <br />
    **Content:**
     
        {
           "timestamp": "2020-10-04T17:51:37.861",
           "status": 403,
           "error": "Forbidden",
           "message": "뿌린 사람 자신만 조회를 할 수 있습니다.",
           "path": "/api/v1/sprinkling/tokens/KLI"
         }
       
        {
          "timestamp": "2020-10-04T17:51:37.861",
          "status": 403,
          "error": "Forbidden",
          "message": "뿌린 건에 대한 조회는 7일 동안 할 수 있습니다.",
          "path": "/api/v1/sprinkling/tokens/KLI"
        }
       
       