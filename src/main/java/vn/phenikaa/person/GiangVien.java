package vn.phenikaa.person;

import java.time.LocalDate;

public class GiangVien extends NhanSu {

    private int soGioGiang;
    private Double tienMoiGio;

    private static final Double LUONG_CO_BAN = 15_000_000.0;

    public GiangVien(String hoTen, LocalDate ngaySinh,
                     String soDienThoai, String queQuan,
                     int soGioGiang, Double tienMoiGio) {

        super(hoTen, ngaySinh, soDienThoai, queQuan, LUONG_CO_BAN);
        this.soGioGiang = soGioGiang;
        this.tienMoiGio = tienMoiGio;
    }

    public int getSoGioGiang() {
        return soGioGiang;
    }

    public void setSoGioGiang(int soGioGiang) {
        this.soGioGiang = soGioGiang;
    }

    public Double getTienMoiGio() {
        return tienMoiGio;
    }

    public void setTienMoiGio(Double tienMoiGio) {
        this.tienMoiGio = tienMoiGio;
    }

    @Override
    public Double tinhLuong() {
        return luongCoBan + soGioGiang * tienMoiGio;
    }
}
