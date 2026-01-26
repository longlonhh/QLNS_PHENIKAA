package vn.phenikaa.person.nhanvien;

import java.time.LocalDate;

import vn.phenikaa.person.NhanSu;

public abstract class NhanVien extends NhanSu {

    protected Double phuCap;

    public NhanVien(String hoTen, LocalDate ngaySinh, String email, Double phuCap) {
        super(hoTen, ngaySinh, email);
        this.phuCap = phuCap;
    }

    public Double getPhuCap() { return phuCap; }
    public void setPhuCap(Double phuCap) { this.phuCap = phuCap; }

    @Override
    public double tinhLuong() {
        return (luongCoBan != null ? luongCoBan : 0)
                + (phuCap != null ? phuCap : 0);
    }
}
