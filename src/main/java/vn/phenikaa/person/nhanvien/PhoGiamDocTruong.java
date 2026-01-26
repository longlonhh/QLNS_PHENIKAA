package vn.phenikaa.person.nhanvien;

import java.time.LocalDate;

public class PhoGiamDocTruong extends NhanVien {

    public PhoGiamDocTruong(String hoTen, LocalDate ngaySinh,
                            String email, Double phuCap) {
        super(hoTen, ngaySinh, email, phuCap);
    }
}
