package vn.phenikaa.organization;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Scanner;

import vn.phenikaa.database.NhanSuDAO;
import vn.phenikaa.person.NhanSu;

public class Truong {

    private String tenTruong;
    private final NhanSuDAO dao = new NhanSuDAO();

    public Truong(String tenTruong) {
        this.tenTruong = tenTruong;
    }

    // ===== THEM =====
    public void themNhanSu(NhanSu ns) {
        dao.insert(ns);
    }

    // ===== HIEN THI DANH SACH =====
    public void hienThiDanhSach() {

        ArrayList<NhanSu> ds = dao.getAll();

        if (ds.isEmpty()) {
            System.out.println("Danh sách rỗng!");
            return;
        }

        System.out.println("===== DANH SÁCH NHÂN SỰ - " + tenTruong + " =====");

        for (NhanSu ns : ds) {
            ns.hienThi();
            System.out.println("--------------------------");
        }
    }

    // ===== XOA =====
    public void xoaNhanSu(String maNV) {

        boolean ok = dao.delete(maNV);

        if (ok) {
            System.out.println("✅ Đã xoá: " + maNV);
        } else {
            System.out.println("❌ Không tìm thấy nhân sự!");
        }
    }

    // ===== SUA =====
    public void suaNhanSu(String maNV, Scanner sc) {

        NhanSu ns = dao.search(maNV).stream()
                .filter(x -> x.getMaNV().equalsIgnoreCase(maNV))
                .findFirst()
                .orElse(null);

        if (ns == null) {
            System.out.println("❌ Không tìm thấy nhân sự!");
            return;
        }

        System.out.print("Nhập họ tên mới: ");
        String hoTen = sc.nextLine();

        System.out.print("Nhập ngày sinh (yyyy-MM-dd): ");
        LocalDate ngaySinh = LocalDate.parse(sc.nextLine());

        ns.setHoTen(hoTen);
        ns.setNgaySinh(ngaySinh);

        dao.update(ns);
        System.out.println("✅ Đã cập nhật nhân sự: " + maNV);
    }

    // ===== TIM KIEM =====
    public void hienThiKetQuaTimKiem(String keyword) {

        ArrayList<NhanSu> ds = dao.search(keyword);

        if (ds.isEmpty()) {
            System.out.println("Không tìm thấy kết quả!");
            return;
        }

        System.out.println("===== KẾT QUẢ TÌM KIẾM =====");

        for (NhanSu ns : ds) {
            ns.hienThi();
            System.out.println("--------------------------");
        }
    }
}