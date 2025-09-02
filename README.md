물론이죠. README 파일을 더 보기 좋고 전문적으로 꾸며 드릴게요.

배지(Badges), 깔끔한 표, 인용구 등을 활용해서 가독성을 높이고 세련된 느낌을 주도록 수정했습니다. 아래 전체 코드를 복사해서 README.md 파일에 붙여넣으시면 됩니다.

<br>

<p align="center">
<img src="https://i.imgur.com/g2360bZ.png" alt="Olla Market Logo" width="150">
<h1 align="center">🛒 Olla Market Project</h1>
<p align="center">
Java & Spring Boot 기반 이커머스 플랫폼 백엔드 시스템
<br>
<a href="https://github.com/your-username/olla-market"><strong>Explore the docs »</strong></a>
<br>
<br>
<a href="https://github.com/your-username/olla-market/issues">Report Bug</a>
·
<a href="https://github.com/your-username/olla-market/issues">Request Feature</a>
</p>
</p>

<p align="center">
<img src="https://img.shields.io/badge/Java-17-007396?style=for-the-badge&logo=java" alt="Java 17"/>
<img src="https://img.shields.io/badge/Spring_Boot-3.x-6DB33F?style=for-the-badge&logo=spring" alt="Spring Boot"/>
<img src="https://img.shields.io/badge/JPA%2FHibernate-FFFFFF?style=for-the-badge&logo=hibernate" alt="JPA/Hibernate"/>
<img src="https://img.shields.io/badge/QueryDSL-4A9FC3?style=for-the-badge" alt="QueryDSL"/>
<img src="https://img.shields.io/badge/MySQL-4479A1?style=for-the-badge&logo=mysql" alt="MySQL"/>
<img src="https://img.shields.io/badge/Gradle-8.x-02303A?style=for-the-badge&logo=gradle" alt="Gradle"/>
</p>

📖 프로젝트 소개
**올라 마켓(Olla Market)**은 Java와 Spring Boot를 기반으로 구축된 이커머스 플랫폼의 백엔드 API 서버입니다. 이 시스템은 온라인 스토어의 핵심 기능인 상품 관리, 장바구니, 주문 및 결제 로직을 안정적이고 확장 가능하게 구현하는 것을 목표로 합니다.

<br>

✨ 주요 기능
기능 설명
🛍️ 상품 카테고리, 상품명, 가격 등 다양한 조건으로 상품을 검색하고 페이징된 목록을 조회합니다.
🛒 장바구니 사용자별로 상품을 담고, 수량을 관리하며, 총액을 계산하는 기능을 제공합니다.
💳 주문/결제 장바구니 상품을 기반으로 주문을 생성하고, 재고를 관리하며, 외부 API를 통해 결제를 처리합니다.

Sheets로 내보내기
<br>

🚀 API Endpoints
기능 Method URL 설명
상품 GET /api/products 조건별 상품 목록 조회 (페이징)
GET /api/products/{productId} 특정 상품 상세 정보 조회
장바구니 POST /api/cart/items 장바구니에 상품 추가
GET /api/cart 현재 사용자의 장바구니 조회
PATCH /api/cart/items/{cartItemId} 장바구니 상품 수량 변경
DELETE /api/cart/items/{cartItemId} 장바구니 특정 상품 삭제
DELETE /api/cart/items 장바구니 모든 상품 비우기
주문/결제 POST /api/orders 장바구니 상품으로 주문 생성
GET /api/orders/{orderId} 특정 주문 내역 조회
POST /api/orders/{orderId}/cancel 주문 취소
POST /api/payments/complete 결제 완료 처리 (외부 API 콜백)

Sheets로 내보내기
<br>

⚙️ 기술적 결정 및 주요 고민 사항
프로젝트를 진행하며 마주한 기술적 문제들과 이를 해결하기 위한 결정 사항을 정리합니다.

1. 성능 최적화: N+1 문제 해결
   고민: 상품 목록 조회 시, 각 상품의 카테고리 정보까지 함께 가져와야 했습니다. 이 과정에서 페이징과 맞물려 각 상품마다 카테고리를 조회하는 N+1 문제가 발생할 가능성을 인지했습니다.

해결 방안:
N+1 문제는 DB에 과도한 부하를 주어 성능을 저하 시키는 주된 원인입니다. 이 문제를 해결하기 위해 다음과 같은 접근법을 고려하고 적용했습니다.

Fetch Join: JPQL에서 JOIN FETCH를 사용하여 연관된 엔티티(예: Category)를 한 번의 쿼리로 함께 조회합니다.

@EntityGraph: Repository 메서드에 @EntityGraph 어노테이션을 사용하여 Fetch Join과 유사한 효과를 냅니다.

DTO 프로젝션 (QueryDSL 활용): 복잡한 검색 조건과 동적 쿼리가 필요한 경우, QueryDSL을 사용하여 처음부터 필요한 데이터만 DTO로 조회합니다.

결정: 간단한 연관관계 조회는 Fetch Join을 우선적으로 사용하고, 복잡한 동적 검색이 필요한 상품 조회 API에서는 QueryDSL과 DTO 프로젝션을 채택하여 N+1 문제를 원천적으로 방지하고 성능을 최적화했습니다.

2. 데이터 모델링: 품절 상태 관리
   고민: 장바구니나 상품 목록 조회 시, 사용자는 상품의 품절 여부를 즉시 알 수 있어야 합니다. 이를 위해 별도의 status 컬럼을 둘지, stock 수량으로 실시간 계산할지 결정이 필요했습니다.

해결 방안:

A) status 컬럼으로 관리: Product 테이블에 status (FOR_SALE, SOLD_OUT) 컬럼을 추가.

장점: 읽기 성능이 빠름.

단점: 데이터 정합성이 깨질 위험 존재. (예: 재고는 있으나 상태가 SOLD_OUT)

B) stock 수량으로 동적 계산: stock > 0 여부를 실시간으로 계산.

장점: 데이터가 항상 정확함.

단점: 조회 시마다 미세한 계산 비용 발생.

결정: 데이터의 정확성을 최우선으로 고려하여 동적 계산 방식을 기본으로 채택했습니다. 다만 상품 단종/판매 중단 케이스를 위해 status 컬럼(SELLING, DISCONTINUED)을 혼합하여, status가 SELLING이면서 stock > 0일 때만 '구매 가능' 상태로 판단합니다.

3. 동시성 제어: 재고 관리
   고민: 재고가 1개 남은 상품을 여러 사용자가 동시에 주문하는 경우, 재고 이상의 주문이 발생하는 Race Condition이 발생할 수 있습니다. 이는 시스템의 신뢰도에 치명적입니다.

해결 방안:
재고 차감 로직에는 동시성 제어가 필수적입니다.

Pessimistic Lock (비관적 락): SELECT ... FOR UPDATE 구문으로 DB 레코드에 직접 락을 거는 방식. 정합성은 확실히 보장하지만 동시성이 떨어질 수 있습니다.

Optimistic Lock (낙관적 락): 엔티티에 @Version 컬럼을 두어 수정 시점의 데이터 버전을 확인하는 방식. 락으로 인한 성능 저하가 없어 동시 요청이 많은 이커머스 환경에 더 적합합니다.

결정: 주문 및 결제와 같이 충돌이 예상되는 재고 차감 로직에서는 **Optimistic Lock (낙관적 락)**을 적용했습니다. 주문 처리 시 @Version을 확인하여 충돌이 감지되면 사용자에게 재고 변동을 알리고 재시도를 유도합니다.

4. 주문 관리: 주문 상태 및 취소 정책
   고민: 주문은 생성부터 배송 완료까지 여러 상태를 거칩니다. 각 상태의 정의와 상태 전이가 명확해야 하며, 사용자가 주문을 취소할 수 있는 시점에 대한 정책이 필요했습니다.

해결 방안:
주문 상태를 Enum (PENDING_PAYMENT, PAID, PREPARING_SHIPMENT, SHIPPED, DELIVERED, CANCELLED)으로 정의하여 명시적으로 관리합니다.

결정: 주문 취소는 "결제 완료" 상태(PAID)까지만 사용자가 직접 취소할 수 있도록 정책을 정했습니다. "배송 준비중"(PREPARING_SHIPMENT) 상태부터는 고객센터를 통해서만 취소 절차를 밟도록 제한합니다.

<br>

🔧 시작하기
사전 요구사항
Java 17+

Gradle 8.x

MySQL

실행 방법
Bash

# 1. 프로젝트 클론

git clone https://github.com/your-username/olla-market.git
cd olla-market

# 2. Gradle 빌드

./gradlew build

# 3. 애플리케이션 실행

java -jar build/libs/olla-market-0.0.1-SNAPSHOT.jar
<br>

🔮 향후 개선 과제
비회원 장바구니 기능: 세션 또는 쿠키를 활용한 비회원 장바구니 기능 추가

쿠폰 및 프로모션: 총액 기반 할인, 특정 상품 할인 등 프로모션 기능 확장

인증/인가: Spring Security와 JWT를 도입하여 API 보안 강화
