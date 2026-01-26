package vn.phenikaa.person.giangvien;

import java.time.LocalDate;

import vn.phenikaa.person.NhanSu;

public abstract class GiangVien extends NhanSu {

    protected int soGioGiang;
    protected double tienMoiGio;

    public GiangVien(String hoTen, LocalDate ngaySinh, String email,
                     int soGioGiang, double tienMoiGio) {
        super(hoTen, ngaySinh, email);
        this.soGioGiang = soGioGiang;
        this.tienMoiGio = tienMoiGio;
    }

    public int getSoGioGiang() { return soGioGiang; }
    public void setSoGioGiang(int soGioGiang) { this.soGioGiang = soGioGiang; }

    public double getTienMoiGio() { return tienMoiGio; }
    public void setTienMoiGio(double tienMoiGio) { this.tienMoiGio = tienMoiGio; }
}
