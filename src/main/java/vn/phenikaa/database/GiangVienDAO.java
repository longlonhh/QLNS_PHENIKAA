package vn.phenikaa.database;

import java.sql.Connection;
import java.sql.PreparedStatement;

import vn.phenikaa.person.GiangVien;

public class GiangVienDAO {

    public void insert(GiangVien gv) {
        String sql = """
            INSERT INTO giangvien(maNV, soGioGiang, tienMoiGio)
            VALUES (?, ?, ?)
        """;

        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, gv.getMaNV());
            ps.setInt(2, gv.getSoGioGiang());
            ps.setDouble(3, gv.getTienMoiGio());

            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}