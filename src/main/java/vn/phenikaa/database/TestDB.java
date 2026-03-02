package vn.phenikaa.database;

import java.util.List;
import vn.phenikaa.organization.DonVi;
import vn.phenikaa.person.ChucVu;

public class TestDB {
    public static void main(String[] args) {
        System.out.println("--- TESTING CONNECTION ---");
        if (DBConnection.getConnection() != null) {
            System.out.println("✅ CONNECTION OK");

            System.out.println("\n--- TESTING DON VI DAO ---");
            DonViDAO dvDao = new DonViDAO();

            // Thử thêm một đơn vị mới
            String testMa = "TEST_DV_" + System.currentTimeMillis() % 1000;
            boolean ok = dvDao.insert(testMa, "Đơn vị Test", DonVi.LoaiDonVi.KHOA, null);
            System.out.println("Thêm đơn vị mới: " + (ok ? "✅ OK" : "❌ THẤT BẠI"));

            List<DonVi> dsDV = dvDao.getAll();
            dsDV.forEach(d -> System.out.println("ID: " + d.getId() + " | Tên: " + d.getTenDonVi()));

            System.out.println("\n--- TESTING CHUC VU DAO ---");
            ChucVuDAO cvDao = new ChucVuDAO();
            List<ChucVu> dsCV = cvDao.getAll();
            dsCV.forEach(c -> System.out.println("ID: " + c.getId() + " | Chức vụ: " + c.getTenChucVu()));
        } else {
            System.err.println("❌ CONNECTION FAILED");
        }
    }
}
