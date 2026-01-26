package vn.phenikaa.database;

import java.sql.Connection;
import java.sql.PreparedStatement;

import vn.phenikaa.person.nhanvien.NhanVien;

public class NhanVienDAO {

    /**
     * Thêm thông tin nhân viên vào bảng nhanvien
     * (chỉ lưu phần riêng của nhân viên)
     */
    public void insert(NhanVien nv) {

        String sql = """
            INSERT INTO nhanvien(maNV, phuCap)
            VALUES (?, ?)
        """;

        try (
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)
        ) {

            ps.setString(1, nv.getMaNV());
            ps.setDouble(2, nv.getPhuCap());

            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
