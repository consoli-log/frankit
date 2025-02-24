-- 사용자 테이블
create table if not exists users
(
    user_seq   bigint auto_increment
    primary key,
    email      varchar(100)                       not null,
    password   varchar(255)                       not null,
    created_at datetime default CURRENT_TIMESTAMP not null,
    updated_at datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP,
    constraint email
    unique (email)
    );

-- 상품 테이블
create table if not exists products
(
    product_seq  bigint auto_increment
    primary key,
    name         varchar(255)                         not null,
    description  text                                 not null,
    price        decimal(10, 2)                       not null,
    shipping_fee decimal(10, 2)                       not null,
    is_active    tinyint(1) default 1                 not null,
    created_at   datetime   default CURRENT_TIMESTAMP not null,
    updated_at   datetime   default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP
    );

-- 옵션 테이블
create table if not exists product_options
(
    option_seq   bigint auto_increment
    primary key,
    product_seq  bigint                               not null,
    option_name  varchar(255)                         not null,
    option_type  enum ('INPUT', 'SELECT')             not null,
    option_price decimal(10, 2)                       null,
    is_active    tinyint(1) default 1                 not null,
    created_at   datetime   default CURRENT_TIMESTAMP not null,
    updated_at   datetime   default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP,
    constraint product_options_ibfk_1
    foreign key (product_seq) references products (product_seq)
                                                               on delete cascade
    );

create index product_seq
    on product_options (product_seq);

-- 옵션 상세 테이블 (선택형 옵션 값 저장)
create table if not exists option_details
(
    detail_seq   bigint auto_increment
    primary key,
    option_seq   bigint                               not null,
    detail_name  varchar(255)                         not null,
    detail_price decimal(10, 2)                       not null,
    is_active    tinyint(1) default 1                 not null,
    created_at   datetime   default CURRENT_TIMESTAMP not null,
    updated_at   datetime   default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP,
    constraint option_details_ibfk_1
    foreign key (option_seq) references product_options (option_seq)
                                                               on delete cascade
    );

create index option_seq
    on option_details (option_seq);

