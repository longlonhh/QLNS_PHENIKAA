package vn.phenikaa.person.giangvien;

import java.time.LocalDate;

public class GiangVienDay extends GiangVien {

    public GiangVienDay(String hoTen, LocalDate ngaySinh, String email,
                        int soGioGiang, double tienMoiGio) {
        super(hoTen, ngaySinh, email, soGioGiang, tienMoiGio);
    }

    @Override
    public double tinhLuong() {
        return (luongCoBan != null ? luongCoBan : 0)
                + soGioGiang * tienMoiGio;
    }
}
