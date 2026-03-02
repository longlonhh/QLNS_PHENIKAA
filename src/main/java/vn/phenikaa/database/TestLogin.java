package vn.phenikaa.database;

import vn.phenikaa.person.NguoiDung;
import vn.phenikaa.person.VaiTro;

public class TestLogin {
    public static void main(String[] args) {
        NguoiDungDAO dao = new NguoiDungDAO();

        System.out.println("--- Testing Login ---");

        // Test Admin
        NguoiDung admin = dao.xacThuc("admin", "admin123");
        if (admin != null && admin.getRole() == VaiTro.ADMIN) {
            System.out.println("✅ Admin Authentication OK");
        } else {
            System.out.println("❌ Admin Authentication FAILED");
        }

        // Test Staff
        NguoiDung staff = dao.xacThuc("staff", "staff123");
        if (staff != null && staff.getRole() == VaiTro.STAFF) {
            System.out.println("✅ Staff Authentication OK");
        } else {
            System.out.println("❌ Staff Authentication FAILED");
        }

        // Test Wrong Password
        NguoiDung wrong = dao.xacThuc("admin", "wrongpass");
        if (wrong == null) {
            System.out.println("✅ Wrong Password Handled OK");
        } else {
            System.out.println("❌ Wrong Password FAILED");
        }
    }
}
