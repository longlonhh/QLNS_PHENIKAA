package vn.phenikaa.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import vn.phenikaa.person.ChucVu;

public class ChucVuDAO {
    public List<ChucVu> getAll() {
        List<ChucVu> list = new ArrayList<>();
        String sql = "SELECT * FROM chucvu";
        try (Connection c = DBConnection.getConnection();
                PreparedStatement ps = c.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new ChucVu(
                        rs.getInt("id"),
                        rs.getString("tenChucVu"),
                        rs.getDouble("heSoPhuCap")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}
