package vn.phenikaa.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import vn.phenikaa.person.NguoiDung;
import vn.phenikaa.person.VaiTro;

public class NguoiDungDAO {
    public NguoiDung xacThuc(String username, String password) {
        String sql = "SELECT * FROM nguoidung WHERE username = ? AND password = SHA2(?, 256)";
        try (Connection c = DBConnection.getConnection();
                PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new NguoiDung(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("password"),
                        VaiTro.valueOf(rs.getString("role")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean insert(String username, String password, VaiTro role) {
        String sql = "INSERT INTO nguoidung (username, password, role) VALUES (?, SHA2(?, 256), ?)";
        try (Connection c = DBConnection.getConnection();
                PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, password);
            ps.setString(3, role.name());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean doiMatKhau(int id, String oldPass, String newPass) {
        String sql = "UPDATE nguoidung SET password = SHA2(?, 256) WHERE id = ? AND password = SHA2(?, 256)";
        try (Connection c = DBConnection.getConnection();
                PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, newPass);
            ps.setInt(2, id);
            ps.setString(3, oldPass);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
