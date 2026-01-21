package vn.phenikaa.person;

import java.time.LocalDate;

public class GiangVien extends NhanSu {

    private int soGioGiang;
    private double tienMoiGio;

    // ===== CONSTRUCTOR =====
    public GiangVien(
            String hoTen,
            LocalDate ngaySinh,
            String email,
            int soGioGiang,
            double tienMoiGio
    ) {
        super(hoTen, ngaySinh, email); // KHỚP constructor NhanSu
        this.soGioGiang = soGioGiang;
        this.tienMoiGio = tienMoiGio;
    }

    // ===== GET =====
    public int getSoGioGiang() {
        return soGioGiang;
    }

    public double getTienMoiGio() {
        return tienMoiGio;
    }

    // ===== SET =====
    public void setSoGioGiang(int soGioGiang) {
        this.soGioGiang = soGioGiang;
    }

    public void setTienMoiGio(double tienMoiGio) {
        this.tienMoiGio = tienMoiGio;
    }

    // ===== TÍNH LƯƠNG =====
    @Override
    public double tinhLuong() {
        double base = (luongCoBan != null) ? luongCoBan : 0;
        return base + soGioGiang * tienMoiGio;
    }
}
