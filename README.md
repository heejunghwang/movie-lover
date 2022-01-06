# movie-lover

Spring Boot 기반 검색용 REST API 입니다. REST API 로 영화 데이터를 JSON 파일로 부터 크롤링 후 엘라스틱서치에 색인 작업 및 영화 추천 검색 API 를 제공합니다.

## prerequisite (준비사항)

- 자바 버전 : openjdk version "12.0.2" (최소사양 java 11)
- 엘라스틱서치 버전 : 7.16.0
- Gradle 7.3.1
- Docker

## 실행방법

1. 엘라스틱 서치 실행 & nori 분석기 플러그인 설치

아래 커맨드를 이용하면 docker-compose를 이용해서 elasticsearch를 설치할 수 있습니다. 이 커멘드는 elasticsearch 뿐 아니라 nori 분석기 플러그인도 함께 설치합니다.

```sh
cd docker-compose
docker-compose up --build
```

2. 서버 실행

아래 커멘드를 실행하면 서버를 실행할 수 있습니다.

``` sh
./gradlew build
./gradlew bootRun
```

## API 사용방법

1. 색인

```
curl --location --request POST 'localhost:8080/movies-recommend'
```

2. 검색

쿼리 파라미터 뒤에 질의하고자하는 텍스트를 넣으면 됩니다. 단, q가 없을 시 랜덤하게 영화를 추천하고 있습니다.

```
curl --location --request GET 'localhost:8080/movies-recommend?q=나'
```

이렇게 실행 시키면, 아래와 같이 검색 결과를 확인할 수 있습니다.

```
[
    {
        "movieCode": "20179083",
        "movieName": "나피디",
        "movieNameEnglish": "",
        "productYear": 2016,
        "openDate": null,
        "typeName": "단편",
        "productStateName": "기타",
        "nations": [
            "한국"
        ],
        "genres": [
            "드라마"
        ],
        "representativeNationName": "한국",
        "representativeGenreName": "드라마"
    },
    {
        "movieCode": "20176710",
        "movieName": "율리안나",
        "movieNameEnglish": "Juliana",
        "productYear": 2017,
        "openDate": null,
        "typeName": "단편",
        "productStateName": "기타",
        "nations": [
            "한국"
        ],
        "genres": [
            "기타"
        ],
        "representativeNationName": "한국",
        "representativeGenreName": "기타"
    },
    {
        "movieCode": "20166550",
        "movieName": "너나 나나",
        "movieNameEnglish": "I'M THE SAME AS YOU",
        "productYear": 2016,
        "openDate": null,
        "typeName": "단편",
        "productStateName": "기타",
        "nations": [
            "프랑스"
        ],
        "genres": [
            "기타"
        ],
        "representativeNationName": "프랑스",
        "representativeGenreName": "기타"
    },
    {
        "movieCode": "20179482",
        "movieName": "내 친구 그리고 나",
        "movieNameEnglish": "",
        "productYear": 2016,
        "openDate": null,
        "typeName": "단편",
        "productStateName": "기타",
        "nations": [
            "한국"
        ],
        "genres": [
            "드라마"
        ],
        "representativeNationName": "한국",
        "representativeGenreName": "드라마"
    },
    {
        "movieCode": "20168751",
        "movieName": "나의 딸, 나의 누나",
        "movieNameEnglish": "Les Cowboys",
        "productYear": 2015,
        "openDate": 20170323,
        "typeName": "장편",
        "productStateName": "개봉",
        "nations": [
            "프랑스"
        ],
        "genres": [
            "드라마"
        ],
        "representativeNationName": "프랑스",
        "representativeGenreName": "드라마"
    }
]
```

## Swagger

REST API를 효율적으로 테스트하기 위해서 swagger ui를 제공하고 있습니다. 서버 실행 후, 인터넷 브라우저를 열고 아래 url로 접속하면 스웨거 확인이 가능합니다.

```url
http://localhost:8080/swagger-ui/index.html
```

## Postman collection

Postman으로 테스트할 경우 postman 폴더 하위에서 postman collection을 다운로드 후 postman에서 import 후 사용하실 수 있습니다.

## 색인 프로세스

스프링 배치를 활용해서 구성했으며, 'curl --location --request POST 'localhost:8080/movies-recommend' API 를 호출하면 아래 작업이 수행됩니다.

1. 인덱스 생성 및 인덱스 설정
   - 인덱스를 설정해야, 형태소 분석이 가능하므로, 인덱스를 설정했습니다.
   - nori : 한국어 형태소 분석기를 위해서 사용
     예) 살아있는 검색 시 -> 살아, 살았 등등 (예제 보완 필요)
   - ngram
   - edge ngram

2. 영화 데이터 JSON 파일에서 부터 크롤링 해온 후, 엘라스틱서치에 전체 색인
   1. chunk : 영화 데이터를 N건씩 item reader에서 읽고 writer 하는 작업을 수행합니다.
   2. item reader : 영화 데이터를 json으로 부터 조회하는 역할을 합니다.
   3. item writer : item reader로 부터 조회된 데이터를 엘라스틱서치에 색인합니다. (bulk API로 색인)

3. elasticsearch alias 지정

## Appendix A : 영화 추천 인덱스 수동 설정 (참고)

영화 추천 데이터의 인덱스를 설정하기 위해서 크롤러를 실행시키지 않고, curl 로 인덱스를 설정하고 싶을 땐 아래 curl을 실행하면 인덱스 설정이 가능합니다.

```sh
curl --location --request PUT 'localhost:9200/movies_20220105022129' \
--header 'Content-Type: application/json' \
--data-raw '{
   "aliases":{
      "movies-recommend":{
         
      }
   },
   "mappings":{
      "properties":{
         "genres":{
            "type":"keyword"
         },
         "movieCode":{
            "type":"keyword"
         },
         "movieName":{
            "type":"text",
            "fields":{
               "keyword":{
                  "type":"keyword",
                  "ignore_above":256
               },
               "ngram":{
                  "type":"text",
                  "analyzer":"ngram_analyzer"
               }
            },
            "analyzer":"korean_analyzer"
         },
         "movieNameEnglish":{
            "type":"text",
            "fields":{
               "keyword":{
                  "type":"keyword",
                  "ignore_above":256
               },
               "ngram":{
                  "type":"text",
                  "analyzer":"ngram_analyzer"
               }
            },
            "analyzer":"standard"
         },
         "nations":{
            "type":"keyword"
         },
         "openDate":{
            "type":"long",
            "null_value":0
         },
         "productStateName":{
            "type":"keyword"
         },
         "productYear":{
            "type":"long",
            "null_value":0
         },
         "representativeGenreName":{
            "type":"text",
            "fields":{
               "keyword":{
                  "type":"keyword",
                  "ignore_above":256
               }
            },
            "analyzer":"ngram_analyzer"
         },
         "representativeNationName":{
            "type":"text",
            "fields":{
               "keyword":{
                  "type":"keyword",
                  "ignore_above":256
               }
            },
            "analyzer":"ngram_analyzer"
         },
         "typeName":{
            "type":"keyword"
         },
         "viewCount":{
            "type":"long",
            "null_value":0
         }
      }
   },
   "settings":{
      "analysis":{
         "analyzer":{
            "edge_ngram_analyzer":{
               "type":"custom",
               "tokenizer":"edge_ngram_tokenizer"
            },
            "ngram_analyzer":{
               "filter":[
                  "lowercase",
                  "trim"
               ],
               "type":"custom",
               "tokenizer":"ngram_tokenizer"
            },
            "korean_analyzer":{
               "type":"custom",
               "tokenizer":"korean_analyzer"
            }
         },
         "tokenizer":{
            "edge_ngram_tokenizer":{
               "token_chars":[
                  "digit",
                  "letter"
               ],
               "min_gram":"1",
               "type":"edge_ngram",
               "max_gram":"10"
            },
            "korean_analyzer":{
               "type":"nori_tokenizer"
            },
            "ngram_tokenizer":{
               "token_chars":[
                  "digit",
                  "letter"
               ],
               "min_gram":"1",
               "type":"ngram",
               "max_gram":"2"
            }
         }
      }
   }
}'
```

위 curl을 실행 시키면, 아래와 같은 결과를 확인할 수 있습니다.

```json
{"acknowledged":true,"shards_acknowledged":true,"index":"movies_20220105022129"}
```


## Appendix B : 영화 추천 데이터 curl로 색인

영화 추천 데이터를 만들기 위해서 API를 호출하지 않고, curl 로 인덱스를 설정하고 싶을 땐 아래 curl을 실행하면 인덱스 설정이 가능합니다. 단, 위의 인덱스 설정을 먼저 설정해야 분석기를 통해 텍스트 분석이 가능하므노 인덱스 설정을 먼저 하셔야합니다.

* (참고) bulk API curl example
*
```sh
// bulk insert 실행
curl -X POST "localhost:9200/_bulk?pretty" -H 'Content-Type: application/json' -d'
{ "index" : { "_index" : "movies_20220105022129", "_id" : "626a5b43-d015-4413-91bd-cde2515ff7b4" } }
{"movieCode":"20165141","movieName":"키즈모노가타리 I : 철혈편","movieNameEnglish":"Kizu Monogatari Tekketsuhen","productYear":2016,"openDate":20160630,"typeName":"장편","productStateName":"개봉","nations":["일본"],"genres":["애니메이션","드라마","판타지","액션"],"representativeNationName":"일본","representativeGenreName":"애니메이션","viewCount":1149751213}
{ "index" : { "_index" : "movies_20220105022129", "_id" : "30abe3dd-5912-4e05-8eab-447f06937bba" } }
{"movieCode":"20165783","movieName":"친구","movieNameEnglish":"Ryoudonarinomiboujin kawakamiyu","productYear":2010,"openDate":20160630,"typeName":"장편","productStateName":"개봉","nations":["일본"],"genres":["멜로","로맨스","드라마"],"representativeNationName":"일본","representativeGenreName":"멜로/로맨스","viewCount":2103234214}
{ "index" : { "_index" : "movies_20220105022129", "_id" : "8ea7fd26-5b41-4617-b2ce-67e5ceaabce1" } }
{"movieCode":"20162241","movieName":"마일드 앤 러블리","movieNameEnglish":"Thou Wast Mild and Lovely","productYear":2014,"openDate":20160630,"typeName":"장편","productStateName":"개봉","nations":["미국"],"genres":["멜로","로맨스","스릴러"],"representativeNationName":"미국","representativeGenreName":"멜로/로맨스","viewCount":1227823754}
{ "index" : { "_index" : "movies_20220105022129", "_id" : "cd12f962-a4f9-45eb-9b79-451da7219882" } }
{"movieCode":"20155390","movieName":"양치기들","movieNameEnglish":"The Boys Who Cried Wolf","productYear":2015,"openDate":20160602,"typeName":"장편","productStateName":"개봉","nations":["한국"],"genres":["드라마","범죄","스릴러"],"representativeNationName":"한국","representativeGenreName":"드라마","viewCount":1019133755}
{ "index" : { "_index" : "movies_20220105022129", "_id" : "05e8b43f-582a-4765-b849-dd1a3149d18c" } }
{"movieCode": "19498009","movieName": "나는 전쟁 신부","movieNameEnglish": "I Was a Male War Bride","productYear": 1949,"openDate": null,"typeName": "","productStateName": "기타","nations": null,"genres": null,"representativeNationName": "","representativeGenreName": ""}
'
```

데이터 색인 후에는 검색 가능합니다.



## Appendix C : 실행 전략

실행시마다 전체 색인하도록 설정했습니다. 장애가 없이, 재색인 중에도 검색을 할 수 있도록 무중단 배포를 위해 블루/그린 배포처럼 하고자, 인덱스에 alias를 지정해서 무중단 배포가 가능하도록 했습니다.

## Appendix D : 테스트 실행

테스트는 로컬 엘라스틱서치가 실행된 상태에서 실행해야합니다. 테스트 실행 방법은 아래와 같습니다.

```
./gradlew test
```