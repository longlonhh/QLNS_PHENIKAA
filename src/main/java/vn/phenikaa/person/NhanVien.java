package vn.phenikaa.person;

import java.time.LocalDate;

public class NhanVien extends NhanSu {

    private double phuCap;

    private static final double LUONG_CO_BAN = 8_000_000.0;

    public NhanVien(String hoTen, LocalDate ngaySinh,
                    String soDienThoai, String queQuan,
                    double phuCap) {

        super(hoTen, ngaySinh, soDienThoai, queQuan, LUONG_CO_BAN);
        this.phuCap = phuCap;
    }

    public double getPhuCap() {
        return phuCap;
    }

    public void setPhuCap(double phuCap) {
        this.phuCap = phuCap;
    }

    @Override
    public Double tinhLuong() {
        return luongCoBan + phuCap;
    }
}
