package vn.phenikaa.person;

import java.time.LocalDate;

import vn.phenikaa.organization.Truong;

public abstract class NhanSu {

    protected String maNV;
    protected String hoTen;
    protected LocalDate ngaySinh;
    protected String email;
    protected Double luongCoBan;
    protected Truong truong;

    // ===== CONSTRUCTOR =====
    public NhanSu(String hoTen, LocalDate ngaySinh, String email) {
        this.hoTen = hoTen;
        this.ngaySinh = ngaySinh;
        this.email = email;
    }

    // ===== ABSTRACT =====
    public abstract double tinhLuong();

    // ===== GET / SET =====
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

    public Truong getTruong() {
        return truong;
    }

    public void setTruong(Truong truong) {
        this.truong = truong;
    }

    public String getEmailTruong() {
    if (maNV == null || maNV.isBlank()) return "";
    return maNV.toLowerCase() + "@st.phenikaa-uni.edu.vn";
}


}
