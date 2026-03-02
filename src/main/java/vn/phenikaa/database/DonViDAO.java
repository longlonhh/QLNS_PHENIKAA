package vn.phenikaa.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import vn.phenikaa.organization.DonVi;

public class DonViDAO {
    public List<DonVi> getAll() {
        List<DonVi> list = new ArrayList<>();
        String sql = "SELECT * FROM donvi";
        try (Connection c = DBConnection.getConnection();
                PreparedStatement ps = c.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new DonVi(
                        rs.getInt("id"),
                        rs.getString("maDonVi"),
                        rs.getString("tenDonVi"),
                        DonVi.LoaiDonVi.valueOf(rs.getString("loaiDonVi"))));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean insert(String ma, String ten, DonVi.LoaiDonVi loai, Integer parentId) {
        String sql = "INSERT INTO donvi(maDonVi, tenDonVi, loaiDonVi, parent_id) VALUES (?, ?, ?, ?)";
        try (Connection c = DBConnection.getConnection();
                PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, ma);
            ps.setString(2, ten);
            ps.setString(3, loai.name());
            if (parentId != null)
                ps.setInt(4, parentId);
            else
                ps.setNull(4, java.sql.Types.INTEGER);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
