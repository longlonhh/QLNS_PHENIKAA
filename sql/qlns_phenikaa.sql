CREATE DATABASE IF NOT EXISTS qlns_phenikaa
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;

USE qlns_phenikaa;

CREATE TABLE nhansu (
    maNV VARCHAR(10) PRIMARY KEY,
    hoTen VARCHAR(100) NOT NULL,
    ngaySinh DATE,
    email VARCHAR(100),
    luongCoBan DOUBLE DEFAULT 0,
    loai ENUM('GV','NV') NOT NULL
);

CREATE TABLE giangvien (
    maNV VARCHAR(10) PRIMARY KEY,
    soGioGiang INT DEFAULT 0,
    tienMoiGio DOUBLE DEFAULT 0,
    CONSTRAINT fk_gv_ns
        FOREIGN KEY (maNV)
        REFERENCES nhansu(maNV)
        ON DELETE CASCADE
);

CREATE TABLE nhanvien (
    maNV VARCHAR(10) PRIMARY KEY,
    phuCap DOUBLE DEFAULT 0,
    CONSTRAINT fk_nv_ns
        FOREIGN KEY (maNV)
        REFERENCES nhansu(maNV)
        ON DELETE CASCADE
);
