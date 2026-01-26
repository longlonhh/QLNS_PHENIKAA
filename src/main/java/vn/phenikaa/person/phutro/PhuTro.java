package vn.phenikaa.person.phutro;

import java.time.LocalDate;

import vn.phenikaa.person.NhanSu;

public abstract class PhuTro extends NhanSu {

    protected Double luongThang;

    public PhuTro(String hoTen, LocalDate ngaySinh,
                  String email, Double luongThang) {
        super(hoTen, ngaySinh, email);
        this.luongThang = luongThang;
    }

    @Override
    public double tinhLuong() {
        return luongThang != null ? luongThang : 0;
    }
}
