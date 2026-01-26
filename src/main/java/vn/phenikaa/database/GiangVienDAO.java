package vn.phenikaa.database;

import java.sql.Connection;
import java.sql.PreparedStatement;

import vn.phenikaa.person.giangvien.GiangVien;

public class GiangVienDAO {

    /**
     * Thêm thông tin giảng viên vào bảng giangvien
     * (chỉ lưu phần riêng của giảng viên)
     */
    public void insert(GiangVien gv) {

        String sql = """
            INSERT INTO giangvien(maNV, soGioGiang, tienMoiGio)
            VALUES (?, ?, ?)
        """;

        try (
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)
        ) {

            ps.setString(1, gv.getMaNV());
            ps.setInt(2, gv.getSoGioGiang());
            ps.setDouble(3, gv.getTienMoiGio());

            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
