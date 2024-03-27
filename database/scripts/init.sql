CREATE DATABASE IF NOT EXISTS tableforyou;

USE tableforyou;

-- User 테이블 생성
CREATE TABLE User (
    Id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    nickname VARCHAR(255) NOT NULL,
    username VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    age VARCHAR(255),
    provider VARCHAR(255),
    providerId VARCHAR(255),
    role VARCHAR(255),
    created_time DATETIME NOT NULL,
    modified_time DATETIME NOT NULL
);

-- Restaurant 테이블 생성
CREATE TABLE Restaurant (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    usedSeats INT NOT NULL,
    totalSeats INT NOT NULL,
    likeCount INT NOT NULL,
    rating DOUBLE NOT NULL,
    rating_num INT NOT NULL,
    time VARCHAR(255) NOT NULL,
    name VARCHAR(255) NOT NULL,
    location VARCHAR(255) NOT NULL,
    tel VARCHAR(255) NOT NULL,
    description VARCHAR(255),
    user_id BIGINT,
    created_time DATETIME NOT NULL,
    modified_time DATETIME NOT NULL,
    FOREIGN KEY (user_id) REFERENCES User(Id)
);

-- Menu 테이블 생성
CREATE TABLE Menu (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    price VARCHAR(255) NOT NULL,
    restaurant_id BIGINT,
    created_time DATETIME NOT NULL,
    modified_time DATETIME NOT NULL,
    FOREIGN KEY (restaurant_id) REFERENCES Restaurant(id)
);
