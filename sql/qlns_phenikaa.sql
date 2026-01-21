CREATE DATABASE IF NOT EXISTS qlns_phenikaa
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;

USE qlns_phenikaa;

-- ===============================
-- BẢNG TRƯỜNG (THÊM TỪ APP)
-- ===============================
CREATE TABLE truong (
    id INT AUTO_INCREMENT PRIMARY KEY,
    maTruong VARCHAR(20) UNIQUE NOT NULL,
    tenTruong VARCHAR(100) NOT NULL
);

-- ===============================
-- BẢNG NHÂN SỰ
-- ===============================
CREATE TABLE nhansu (
    maNV VARCHAR(10) PRIMARY KEY,
    hoTen VARCHAR(100) NOT NULL,
    ngaySinh DATE,
    email VARCHAR(100),
    luongCoBan DOUBLE DEFAULT 0,
    loai ENUM('GV','NV') NOT NULL,

    truong_id INT NOT NULL,

    CONSTRAINT fk_ns_truong
        FOREIGN KEY (truong_id)
        REFERENCES truong(id)
        ON DELETE RESTRICT
);

-- ===============================
-- BẢNG GIẢNG VIÊN
-- ===============================
CREATE TABLE giangvien (
    maNV VARCHAR(10) PRIMARY KEY,
    soGioGiang INT DEFAULT 0,
    tienMoiGio DOUBLE DEFAULT 0,

    CONSTRAINT fk_gv_ns
        FOREIGN KEY (maNV)
        REFERENCES nhansu(maNV)
        ON DELETE CASCADE
);

-- ===============================
-- BẢNG NHÂN VIÊN
-- ===============================
CREATE TABLE nhanvien (
    maNV VARCHAR(10) PRIMARY KEY,
    phuCap DOUBLE DEFAULT 0,

    CONSTRAINT fk_nv_ns
        FOREIGN KEY (maNV)
        REFERENCES nhansu(maNV)
        ON DELETE CASCADE
);
