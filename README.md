# 📖 프로젝트 소개

마켓(Market)은 Java와 Spring Boot를 기반으로 구축된 백엔드 API 서버입니다.
이 시스템은 온라인 스토어의 핵심 기능인 상품 관리, 장바구니, 주문 및 결제 로직을 안정적이고 확장 가능하게 구현하는 것을 목표로 합니다.

<br>

# ✨ 주요 기능

## 기능 설명

🛍️ 상품 카테고리, 상품명, 가격 등 다양한 조건으로 상품을 검색하고 페이징된 목록을 조회합니다.
🛒 장바구니 사용자별로 상품을 담고, 수량을 관리하며, 총액을 계산하는 기능을 제공합니다.
💳 주문/결제 장바구니 상품을 기반으로 주문을 생성하고, 재고를 관리하며, 외부 API를 통해 결제를 처리합니다.

<br>

# 🚀 API Endpoints

## 기능 Method URL 설명

### 유저
GET /api/user 신규 사용자 생성
POST /apai/user 모든 사용자 조회
PUT /api/user/{id} 특정 사용자 정보 수정
DELETE /api/user/{id} 특정 사용자 탈퇴 처리
### 상품
GET /api/products 조건별 상품 목록 조회 (페이징)
GET /api/products/{productId} 특정 상품 상세 정보 조회
### 장바구니
POST /api/cart/items 장바구니에 상품 추가
GET /api/cart 현재 사용자의 장바구니 조회
PATCH /api/cart/items/{cartItemId} 장바구니 상품 수량 변경
DELETE /api/cart/items/{cartItemId} 장바구니 특정 상품 삭제
DELETE /api/cart/items 장바구니 모든 상품 비우기
### 주문/결제
POST /api/orders 장바구니 상품으로 주문 생성
GET /api/orders/{orderId} 특정 주문 내역 조회
POST /api/orders/{orderId}/cancel 주문 취소
POST /api/payments/complete 결제 완료 처리 (외부 API 콜백)

<br>

# 🔧 시작하기

## 사전 요구사항

Java 17+

Gradle 8.x

MySQL

# 실행 방법

Bash

## 1. 프로젝트 클론

git clone https://github.com/your-username/olla-market.git
cd olla-market

## 2. Gradle 빌드

./gradlew build

## 3. 애플리케이션 실행

java -jar build/libs/olla-market-0.0.1-SNAPSHOT.jar
<br>

# 🔮 향후 개선 과제

1. 주문 및 결제 기능 추가

2. 비회원 장바구니 기능: 세션 또는 쿠키를 활용한 비회원 장바구니 기능 추가

3. 쿠폰 및 프로모션: 총액 기반 할인, 특정 상품 할인 등 프로모션 기능 확장

4. 인증/인가: Spring Security와 JWT를 도입하여 API 보안 강화
