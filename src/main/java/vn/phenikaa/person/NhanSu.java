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

    public NhanSu(String hoTen, LocalDate ngaySinh, String email) {
        this.hoTen = hoTen;
        this.ngaySinh = ngaySinh;
        this.email = email;
    }

    public abstract double tinhLuong();

    public String getMaNV() { return maNV; }
    public void setMaNV(String maNV) { this.maNV = maNV; }

    public String getHoTen() { return hoTen; }
    public void setHoTen(String hoTen) { this.hoTen = hoTen; }

    public LocalDate getNgaySinh() { return ngaySinh; }
    public void setNgaySinh(LocalDate ngaySinh) { this.ngaySinh = ngaySinh; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public Double getLuongCoBan() { return luongCoBan; }
    public void setLuongCoBan(Double luongCoBan) { this.luongCoBan = luongCoBan; }

    public Truong getTruong() { return truong; }
    public void setTruong(Truong truong) { this.truong = truong; }

    public String getEmailTruong() {
        if (maNV == null) return "";
        return maNV.toLowerCase() + "@phenikaa-uni.edu.vn";
    }

    protected LoaiNhanSu loaiNhanSu;

    public LoaiNhanSu getLoaiNhanSu() {
        return loaiNhanSu;
    }

    public void setLoaiNhanSu(LoaiNhanSu loaiNhanSu) {
        this.loaiNhanSu = loaiNhanSu;
    }

}
