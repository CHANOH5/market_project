# 📖 프로젝트 소개

**Market(마켓)**은 Java와 Spring Boot 기반으로 구축된 백엔드 API 서버입니다.  
온라인 스토어의 핵심 기능인 **상품 관리, 장바구니, 주문 및 결제** 로직을 안정적이고 확장 가능하게 구현하는 것을 목표로 합니다.

<br>

# ✨ 주요 기능

## 기능 설명

- 🛍️ **상품 조회**: 카테고리, 상품명, 가격 조건으로 검색 및 페이징 처리된 목록 조회
- 🛒 **장바구니**: 사용자별 상품 담기, 수량 관리, 총액 계산 및 품절 여부 확인
- 💳 **주문/결제**: 장바구니 기반 주문 생성, 재고 관리, 외부 결제 API 연동 및 결제 이력 관리 (ing..)

<br>

# 🚀 API Endpoints

## 유저

| Method | URL               | 설명                  |
| ------ | ----------------- | --------------------- |
| GET    | `/api/users`      | 모든 사용자 조회      |
| POST   | `/api/users`      | 신규 사용자 생성      |
| PUT    | `/api/users/{id}` | 특정 사용자 정보 수정 |
| DELETE | `/api/users/{id}` | 특정 사용자 탈퇴 처리 |

## 상품

| Method | URL                         | 설명                           |
| ------ | --------------------------- | ------------------------------ |
| GET    | `/api/products`             | 조건별 상품 목록 조회 (페이징) |
| GET    | `/api/products/{productId}` | 특정 상품 상세 조회            |

## 장바구니

| Method | URL                            | 설명                        |
| ------ | ------------------------------ | --------------------------- |
| POST   | `/api/cart/items`              | 장바구니에 상품 추가        |
| GET    | `/api/cart`                    | 현재 사용자의 장바구니 조회 |
| PATCH  | `/api/cart/items/{cartItemId}` | 장바구니 상품 수량 변경     |
| DELETE | `/api/cart/items/{cartItemId}` | 장바구니 특정 상품 삭제     |
| DELETE | `/api/cart/items`              | 장바구니 전체 비우기        |

## 주문/결제

| Method | URL                            | 설명                           |
| ------ | ------------------------------ | ------------------------------ |
| POST   | `/api/orders`                  | 장바구니 상품으로 주문 생성    |
| GET    | `/api/orders/{orderId}`        | 특정 주문 내역 조회            |
| POST   | `/api/orders/{orderId}/cancel` | 주문 취소                      |
| POST   | `/api/payments/complete`       | 결제 완료 처리 (외부 API 콜백) |

<br>

# 🔧 시작하기

## 사전 요구사항

- Java 17 +
- Spring Boot 3.x +
- MySQL 8.0 +

## 실행 방법

# 1. 프로젝트 클론

git clone https://github.com/CHANOH5/market_project.git

# 2. Gradle 빌드

./gradlew build

# 3. 애플리케이션 실행

java -jar build/libs/olla-market-0.0.1-SNAPSHOT.jar

<br>

# 향후 개선 과제

1. 주문/결제 고도화
2. AOP 기반 로깅/트랜잭션 관리: 공통 관심사(로깅, 보안, 성능 측정) 분리
3. 예외 처리 중앙화
4. 테스트 코드 강화: JUnit5 + MockMvc를 활용한 단위/통합 테스트
5. 비회원 장바구니 기능: 세션 또는 쿠키를 활용한 비회원 장바구니 기능 추가
6. 쿠폰 및 프로모션: 총액 기반 할인, 특정 상품 할인 등 프로모션 기능 확장
7. 성능 최적화: 캐시 도입 및 DB 인덱스 최적화 및 쿼리 성능 개선에 대한 고민
