CREATE DATABASE IF NOT EXISTS market_dev DEFAULT CHARACTER SET utf8mb4 DEFAULT COLLATE utf8mb4_0900_ai_ci;
USE market_dev;

CREATE TABLE users(
                      id  bigint  auto_increment comment '사용자ID' primary key,
                      username varchar(255) not null comment '사용자 이름',
                      email varchar(255) not null comment '이메일',
                      status int not null default 1 comment '상태(0-탈퇴/1-가입)',
                      auth int not null default 1 comment '권한(0-admin/1-일반사용자)',
                      created_by varchar(255) not null comment '등록자',
                      created_at timestamp default CURRENT_TIMESTAMP not null comment '등록일시',
                      updated_by varchar(255) not null comment '수정자',
                      updated_at timestamp default CURRENT_TIMESTAMP not null comment '수정일시',
                      constraint email unique (email)
);

CREATE TABLE categories(
                           id   bigint auto_increment comment '카테고리ID' primary key,
                           name varchar(255) not null comment '카테고리 이름',
                           constraint name unique (name)
);

CREATE TABLE products(
                         id  bigint auto_increment comment '상품ID' primary key ,
                         category_id bigint not null comment '카테고리',
                         name varchar(255) not null comment '상품이름',
                         description text not null comment '상품설명',
                         price decimal(10, 2) not null comment '상품가격',
                         stock int not null comment '상품 재고 수량',
                         status varchar(255) not null comment '상품상태 - FOR_SALE, SOLD_OUT, DELETED',
                         version bigint default 0 not null comment '버전',
                         created_by varchar(255) not null comment '등록자',
                         created_at timestamp default CURRENT_TIMESTAMP not null comment '등록일시',
                         updated_by varchar(255) not null comment '수정자',
                         updated_at timestamp default CURRENT_TIMESTAMP not null comment '수정일시',

                         constraint fk_products_category foreign key (category_id) references categories(id),
                         check ( stock >= 0 )
);

CREATE TABLE cart(
                     id  bigint  auto_increment  primary key,
                     user_id bigint  not null comment '사용자ID',
                     created_by varchar(255) not null comment '등록자',
                     created_at timestamp default CURRENT_TIMESTAMP not null comment '등록일시',
                     updated_by varchar(255) not null comment '수정자',
                     updated_at timestamp default CURRENT_TIMESTAMP not null comment '수정일시',

    -- 유저는 장바구니 1개
                     constraint uq_cart_user unique (user_id),
    -- 조회용 index
                     index idx_cart_user_id (user_id),

                     constraint fk_cart_user foreign key (user_id) references users (id) on update cascade on delete cascade
);

CREATE TABLE cart_items(
                           id  bigint  auto_increment  primary key,
                           cart_id bigint  not null comment '장바구니ID',
                           product_id bigint   not null comment '상품ID',
                           quantity int    not null comment '수량',
                           created_by varchar(255) not null comment '등록자',
                           created_at timestamp default CURRENT_TIMESTAMP not null comment '등록일시',
                           updated_by varchar(255) not null comment '수정자',
                           updated_at timestamp default CURRENT_TIMESTAMP not null comment '수정일시',

    -- 한 장바구니에 같은 상품은 1개만
                           constraint uq_cart_product unique (cart_id, product_id),

    -- FK
                           constraint fk_cart_itmes_cart foreign key (cart_id) references cart (id) on update cascade on delete cascade,
                           constraint fk_cart_itmes_product foreign key (product_id) references products (id) on update cascade on delete cascade
);

CREATE TABLE orders
(
    id           bigint auto_increment  comment '주문ID'  primary key,
    user_id      bigint not null comment '사용자ID',
    status       varchar(255)   not null comment 'CANCELLED(주문취소)/CREATED(주문생성)/PAID(결제완료)/PAYMENT _FAILED(주문실패)/PAYMENT_PENDING(결제 진행 중)',
    total_amount decimal(15, 2) not null comment '결제총액',
    created_by varchar(255) not null comment '등록자',
    created_at timestamp default CURRENT_TIMESTAMP not null comment '등록일시',
    updated_by varchar(255) not null comment '수정자',
    updated_at timestamp default CURRENT_TIMESTAMP not null comment '수정일시',

    constraint fk_orders_user foreign key (user_id) references users (id)
);

CREATE TABLE order_items
(
    id           bigint auto_increment  comment '주문상품ID'    primary key,
    order_id     bigint not null comment '주문ID',
    product_id   bigint not null comment '상품ID',
    product_name varchar(200)   not null comment '상품이름',
    quantity     int    not null comment '수량',
    unit_price   decimal(12, 2) not null comment '상품가격',
    created_by varchar(255) not null comment '등록자',
    created_at timestamp default CURRENT_TIMESTAMP not null comment '등록일시',
    updated_by varchar(255) not null comment '수정자',
    updated_at timestamp default CURRENT_TIMESTAMP not null comment '수정일시',
    -- FK
    constraint fk_order_item_product foreign key (product_id) references products (id),
    constraint fk_order_item_order foreign key (order_id) references orders (id)
);

INSERT INTO categories (name) VALUES ('도서'), ('생활용품'), ('식품'), ('의류'), ('전자제품');

INSERT INTO products
(category_id, name, description, price, stock, status, version, created_by, updated_by)
VALUES
    (1, '무선 블루투스 이어폰', 'AAC 지원, 최대 24시간 재생', 59000.00, 10, 'FOR_SALE', 0, 'system', 'system'),
    (1, '게이밍 마우스', 'PMW3360 센서, 6버튼, RGB', 39000.00, 15, 'FOR_SALE', 0, 'system', 'system'),
    (2, '남성용 반팔 티셔츠', '코튼 100%, 베이직 핏', 19000.00, 20, 'FOR_SALE', 0, 'system', 'system'),
    (2, '여성용 원피스', '여름용 원단, A라인', 49000.00, 0,  'FOR_SALE', 0, 'system', 'system'),
    (3, '유기농 아메리카노', '콜드브루 원액 1L', 8900.00, 29, 'FOR_SALE', 1, 'system', 'system'),
    (3, '그래놀라 시리얼', '견과류 믹스, 500g', 12500.00, 24, 'FOR_SALE', 1, 'system', 'system'),
    (4, '디퓨저 세트', '라벤더/시트러스 2종', 24000.00, 10, 'FOR_SALE', 0, 'system', 'system'),
    (4, 'LED 스탠드', '3단 밝기, 눈부심 방지', 29000.00, 0,  'FOR_SALE', 0, 'system', 'system'),
    (5, '자바의 정석', 'Java 기초부터 심화까지', 35000.00, 5,  'FOR_SALE', 0, 'system', 'system');




