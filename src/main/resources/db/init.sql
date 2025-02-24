-- 데이터베이스 생성
CREATE DATABASE IF NOT EXISTS frankit DEFAULT CHARACTER SET utf8;

-- 사용자 생성 (존재하지 않을 경우)
CREATE USER IF NOT EXISTS 'frankit'@'%' IDENTIFIED BY 'frankit2025';

-- 권한 부여
GRANT ALL PRIVILEGES ON frankit.* TO 'frankit'@'%';

-- 변경 사항 적용
FLUSH PRIVILEGES;

-- frankit 데이터베이스 사용
USE frankit;