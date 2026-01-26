CREATE DATABASE IF NOT EXISTS qlns_phenikaa
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;

USE qlns_phenikaa;

-- =============================================================
-- 1. BẢNG TRƯỜNG (Cấp cao nhất trong cấu trúc Phenikaa)
-- =============================================================
CREATE TABLE truong (
    id INT AUTO_INCREMENT PRIMARY KEY,
    maTruong VARCHAR(20) UNIQUE NOT NULL,
    tenTruong VARCHAR(100) NOT NULL
);

-- =============================================================
-- 2. BẢNG NHÂN SỰ (Bảng cha chứa thông tin chung)
-- =============================================================
CREATE TABLE nhansu (
    maNV VARCHAR(10) PRIMARY KEY,
    hoTen VARCHAR(100) NOT NULL,
    ngaySinh DATE,
    email VARCHAR(100),
    luongCoBan DOUBLE DEFAULT 0,
    -- Phân loại chi tiết để Java map đúng đối tượng con
    loaiNhanSu VARCHAR(50) NOT NULL, 
    -- 'GV' hoặc 'NV' để xử lý logic tổng quát
    loai ENUM('GV','NV', 'PT') NOT NULL, 
    truong_id INT NOT NULL,

    CONSTRAINT fk_ns_truong
        FOREIGN KEY (truong_id)
        REFERENCES truong(id)
        ON DELETE RESTRICT
);

-- =============================================================
-- 3. BẢNG GIẢNG VIÊN (Dùng cho cả Giảng viên dạy và Nghiên cứu viên)
-- =============================================================
CREATE TABLE giangvien (
    maNV VARCHAR(10) PRIMARY KEY,
    soGioGiang INT DEFAULT 0,
    tienMoiGio DOUBLE DEFAULT 0,
    phuCapNghienCuu DOUBLE DEFAULT 0, -- Dành riêng cho Nghiên cứu viên

    CONSTRAINT fk_gv_ns
        FOREIGN KEY (maNV)
        REFERENCES nhansu(maNV)
        ON DELETE CASCADE
);

-- =============================================================
-- 4. BẢNG NHÂN VIÊN HÀNH CHÍNH (Kế toán, IT, Giám đốc trường...)
-- =============================================================
CREATE TABLE nhanvien (
    maNV VARCHAR(10) PRIMARY KEY,
    phuCap DOUBLE DEFAULT 0, -- Phụ cấp trách nhiệm hoặc chức vụ

    CONSTRAINT fk_nv_ns
        FOREIGN KEY (maNV)
        REFERENCES nhansu(maNV)
        ON DELETE CASCADE
);

-- =============================================================
-- 5. BẢNG PHỤ TRỢ (Bảo vệ, Tạp vụ, Vệ sinh)
-- =============================================================
CREATE TABLE phutro (
    maNV VARCHAR(10) PRIMARY KEY,
    luongThang DOUBLE DEFAULT 0, -- Nhóm này thường nhận lương khoán

    CONSTRAINT fk_pt_ns
        FOREIGN KEY (maNV)
        REFERENCES nhansu(maNV)
        ON DELETE CASCADE
);