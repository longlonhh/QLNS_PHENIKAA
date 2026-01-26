package vn.phenikaa.person.giangvien;

import java.time.LocalDate;

public class NghienCuuVien extends GiangVien {

    private double phuCapNghienCuu;

    public NghienCuuVien(String hoTen, LocalDate ngaySinh, String email,
                         int soGioGiang, double tienMoiGio,
                         double phuCapNghienCuu) {
        super(hoTen, ngaySinh, email, soGioGiang, tienMoiGio);
        this.phuCapNghienCuu = phuCapNghienCuu;
    }

    public double getPhuCapNghienCuu() {
        return phuCapNghienCuu;
    }

    @Override
    public double tinhLuong() {
        return (luongCoBan != null ? luongCoBan : 0)
                + soGioGiang * tienMoiGio
                + phuCapNghienCuu;
    }
}
