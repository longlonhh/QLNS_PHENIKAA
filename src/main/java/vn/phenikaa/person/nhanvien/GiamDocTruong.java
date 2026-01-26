package vn.phenikaa.person.nhanvien;

import java.time.LocalDate;

public class GiamDocTruong extends NhanVien {

    public GiamDocTruong(String hoTen, LocalDate ngaySinh,
                         String email, Double phuCap) {
        super(hoTen, ngaySinh, email, phuCap);
    }
}
