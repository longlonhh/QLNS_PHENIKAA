package vn.phenikaa.person;

import java.time.LocalDate;

import vn.phenikaa.organization.DonVi;

public abstract class NhanSu {

    protected String maNV;
    protected String hoTen;
    protected LocalDate ngaySinh;
    protected String email;
    protected Double luongCoBan;
    protected DonVi donVi;
    protected ChucVu chucVu;

    public NhanSu(String hoTen, LocalDate ngaySinh, String email) {
        this.hoTen = hoTen;
        this.ngaySinh = ngaySinh;
        this.email = email;
    }

    public abstract double tinhLuong();

    public String getMaNV() {
        return maNV;
    }

    public void setMaNV(String maNV) {
        this.maNV = maNV;
    }

    public String getHoTen() {
        return hoTen;
    }

    public void setHoTen(String hoTen) {
        this.hoTen = hoTen;
    }

    public LocalDate getNgaySinh() {
        return ngaySinh;
    }

    public void setNgaySinh(LocalDate ngaySinh) {
        this.ngaySinh = ngaySinh;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Double getLuongCoBan() {
        return luongCoBan;
    }

    public void setLuongCoBan(Double luongCoBan) {
        this.luongCoBan = luongCoBan;
    }

    public DonVi getDonVi() {
        return donVi;
    }

    public void setDonVi(DonVi donVi) {
        this.donVi = donVi;
    }

    public ChucVu getChucVu() {
        return chucVu;
    }

    public void setChucVu(ChucVu chucVu) {
        this.chucVu = chucVu;
    }

    /**
     * Tính tổng lương nhận được.
     * Lương = Lương cơ bản + Phụ cấp chức vụ (Hệ số * Lương cơ bản) + Phụ cấp đặc
     * thù của class con
     */
    public double tinhTongLuong() {
        double phuCapChucVu = (chucVu != null) ? (chucVu.getHeSoPhuCap() * (luongCoBan != null ? luongCoBan : 0)) : 0;
        return tinhLuong() + phuCapChucVu;
    }

    public String getEmailTruong() {
        if (maNV == null)
            return "";
        return maNV.toLowerCase() + "@st.phenikaa-uni.edu.vn";
    }

    protected LoaiNhanSu loaiNhanSu;

    public LoaiNhanSu getLoaiNhanSu() {
        return loaiNhanSu;
    }

    public void setLoaiNhanSu(LoaiNhanSu loaiNhanSu) {
        this.loaiNhanSu = loaiNhanSu;
    }

}
