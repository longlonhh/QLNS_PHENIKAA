package vn.phenikaa.person;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public abstract class NhanSu {

    protected String maNV;
    protected String hoTen;
    protected LocalDate ngaySinh;
    protected String soDienThoai;
    protected String queQuan;
    protected String email;
    protected Double luongCoBan;

    public static final DateTimeFormatter FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public NhanSu(String hoTen, LocalDate ngaySinh,
                  String soDienThoai, String queQuan,
                  Double luongCoBan) {

        this.hoTen = hoTen;
        this.ngaySinh = ngaySinh;
        this.soDienThoai = soDienThoai;
        this.queQuan = queQuan;
        this.luongCoBan = luongCoBan;
    }

    // ===== GET =====
    public String getMaNV() { return maNV; }
    public String getHoTen() { return hoTen; }
    public LocalDate getNgaySinh() { return ngaySinh; }
    public String getSoDienThoai() { return soDienThoai; }
    public String getQueQuan() { return queQuan; }
    public String getEmail() { return email; }
    public Double getLuongCoBan() { return luongCoBan; }

    // ===== SET =====
    public void setMaNV(String maNV) {
        this.maNV = maNV;
        this.email = maNV + "@st.phenikaa-uni.edu.vn";
    }

    public void setHoTen(String hoTen) {
        this.hoTen = hoTen;
    }

    public void setNgaySinh(LocalDate ngaySinh) {
        this.ngaySinh = ngaySinh;
    }

    // 🔥🔥🔥 CÁI QUAN TRỌNG NHẤT (BẠN THIẾU)
    public void setLuongCoBan(Double luongCoBan) {
        this.luongCoBan = luongCoBan;
    }

    // ===== ABSTRACT =====
    public abstract Double tinhLuong();

    // ===== DISPLAY =====
    public void hienThi() {
        System.out.println("Ma NV       : " + maNV);
        System.out.println("Ho ten      : " + hoTen);
        System.out.println("Ngay sinh   : " + ngaySinh.format(FORMAT));
        System.out.println("So DT       : " + soDienThoai);
        System.out.println("Que quan    : " + queQuan);
        System.out.println("Email       : " + email);
        System.out.println("Luong co ban: " + luongCoBan);
    }
}
