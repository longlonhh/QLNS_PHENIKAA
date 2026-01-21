package vn.phenikaa.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import vn.phenikaa.organization.Truong;

public class TruongDAO {

    public void insert(String ma, String ten) {

        String sql = """
            INSERT INTO truong(maTruong, tenTruong)
            VALUES (?, ?)
        """;

        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, ma);
            ps.setString(2, ten);
            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ArrayList<Truong> getAll() {

        ArrayList<Truong> list = new ArrayList<>();

        String sql = "SELECT * FROM truong";

        try (Connection c = DBConnection.getConnection();
             ResultSet rs = c.prepareStatement(sql).executeQuery()) {

            while (rs.next()) {
                list.add(new Truong(
                    rs.getInt("id"),
                    rs.getString("maTruong"),
                    rs.getString("tenTruong")
                ));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }
}
