package vn.phenikaa.person;

import java.time.LocalDate;

public class NhanVien extends NhanSu {

    private Double phuCap;

    // ===== CONSTRUCTOR (KHỚP MainController) =====
    public NhanVien(
            String hoTen,
            LocalDate ngaySinh,
            String email,
            Double phuCap
    ) {
        super(hoTen, ngaySinh, email);
        this.phuCap = phuCap;
    }

    // ===== TÍNH LƯƠNG =====
    @Override
    public double tinhLuong() {
        if (luongCoBan == null) return 0;
        return luongCoBan + (phuCap != null ? phuCap : 0);
    }

    // ===== GET / SET =====
    public Double getPhuCap() {
        return phuCap;
    }

    public void setPhuCap(Double phuCap) {
        this.phuCap = phuCap;
    }
}
