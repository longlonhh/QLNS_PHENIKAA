CREATE DATABASE IF NOT EXISTS qlns_phenikaa
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;

USE qlns_phenikaa;

-- Xóa bảng cũ nếu tồn tại (theo thứ tự ngược lại của khóa ngoại)
DROP TABLE IF EXISTS nguoidung;
DROP TABLE IF EXISTS phutro;
DROP TABLE IF EXISTS nhanvien;
DROP TABLE IF EXISTS giangvien;
DROP TABLE IF EXISTS nhansu;
DROP TABLE IF EXISTS donvi;
DROP TABLE IF EXISTS chucvu;

-- =============================================================
-- 1. BẢNG CHỨC VỤ (Cấp bậc quản lý trong Đại học)
-- =============================================================
CREATE TABLE chucvu (
    id INT AUTO_INCREMENT PRIMARY KEY,
    tenChucVu VARCHAR(100) NOT NULL UNIQUE,
    heSoPhuCap DOUBLE DEFAULT 0 -- Hệ số tính trên lương cơ bản
);

INSERT INTO chucvu (tenChucVu, heSoPhuCap) VALUES 
('Hiệu trưởng', 2.0),
('Phó Hiệu trưởng', 1.5),
('Trưởng phòng', 1.0),
('Phó Trưởng phòng', 0.8),
('Trưởng khoa', 1.0),
('Phó Trưởng khoa', 0.8),
('Giám đốc Trường', 1.2),
('Phó Giám đốc Trường', 1.0),
('Nhân viên/Giảng viên', 0);

-- =============================================================
-- 2. BẢNG ĐƠN VỊ (Cấu trúc phân cấp của Đại học)
-- =============================================================
CREATE TABLE donvi (
    id INT AUTO_INCREMENT PRIMARY KEY,
    maDonVi VARCHAR(20) UNIQUE NOT NULL,
    tenDonVi VARCHAR(100) NOT NULL,
    loaiDonVi ENUM('DAI_HOC', 'PHONG_BAN', 'TRUONG_THANH_VIEN', 'VIEN_NGHIEN_CUU', 'KHOA', 'BO_MON') NOT NULL,
    parent_id INT,
    CONSTRAINT fk_donvi_parent FOREIGN KEY (parent_id) REFERENCES donvi(id) ON DELETE SET NULL
);

-- Dữ liệu mẫu khởi tạo cây tổ chức (Bỏ trống để người dùng thêm qua UI)
-- INSERT INTO donvi (maDonVi, tenDonVi, loaiDonVi, parent_id) VALUES ('PHK', 'Đại học Phenikaa', 'DAI_HOC', NULL);

-- =============================================================
-- 3. BẢNG NHÂN SỰ (Bảng cha chứa thông tin chung)
-- =============================================================
CREATE TABLE nhansu (
    maNV VARCHAR(10) PRIMARY KEY,
    hoTen VARCHAR(100) NOT NULL,
    ngaySinh DATE,
    email VARCHAR(100),
    luongCoBan DOUBLE DEFAULT 0,
    loaiNhanSu VARCHAR(50) NOT NULL, 
    loai ENUM('GV','NV', 'PT') NOT NULL, 
    donvi_id INT NOT NULL,
    chucVu_id INT NOT NULL,

    CONSTRAINT fk_ns_donvi
        FOREIGN KEY (donvi_id)
        REFERENCES donvi(id)
        ON DELETE RESTRICT,
    
    CONSTRAINT fk_ns_chucvu
        FOREIGN KEY (chucVu_id)
        REFERENCES chucvu(id)
        ON DELETE RESTRICT
);

-- =============================================================
-- 4. BẢNG GIẢNG VIÊN (Dùng cho cả Giảng viên dạy và Nghiên cứu viên)
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
-- 5. BẢNG NHÂN VIÊN HÀNH CHÍNH (Kế toán, IT, Giám đốc trường...)
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
-- 6. BẢNG PHỤ TRỢ (Bảo vệ, Tạp vụ, Vệ sinh)
-- =============================================================
CREATE TABLE phutro (
    maNV VARCHAR(10) PRIMARY KEY,
    luongThang DOUBLE DEFAULT 0, -- Nhóm này thường nhận lương khoán

    CONSTRAINT fk_pt_ns
        FOREIGN KEY (maNV)
        REFERENCES nhansu(maNV)
        ON DELETE CASCADE
);

-- =============================================================
-- 7. BẢNG NGƯỜI DÙNG (Quản lý tài khoản đăng nhập)
-- =============================================================
CREATE TABLE nguoidung (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL, -- Lưu mã hóa SHA-256
    role ENUM('ADMIN', 'STAFF') NOT NULL
);

-- Chèn tài khoản mặc định (Mật khẩu: admin123 và staff123)
INSERT IGNORE INTO nguoidung (username, password, role) VALUES 
('admin', SHA2('admin123', 256), 'ADMIN'),
('staff', SHA2('staff123', 256), 'STAFF');