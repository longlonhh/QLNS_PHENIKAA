package vn.phenikaa.database;

import java.sql.Connection;
import java.sql.PreparedStatement;

import vn.phenikaa.person.NhanVien;

public class NhanVienDAO {

    public void insert(NhanVien nv) {
        String sql = """
            INSERT INTO nhanvien(maNV, phuCap)
            VALUES (?, ?)
        """;

        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, nv.getMaNV());
            ps.setDouble(2, nv.getPhuCap());

            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}