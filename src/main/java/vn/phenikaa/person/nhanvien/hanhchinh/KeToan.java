package vn.phenikaa.person.nhanvien.hanhchinh;

import java.time.LocalDate;

import vn.phenikaa.person.nhanvien.NhanVien;

public class KeToan extends NhanVien {

    public KeToan(String hoTen, LocalDate ngaySinh, String email, Double phuCap) {
        super(hoTen, ngaySinh, email, phuCap);
    }
}
