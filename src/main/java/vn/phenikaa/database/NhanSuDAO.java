package vn.phenikaa.database;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;

import vn.phenikaa.organization.Truong;
import vn.phenikaa.person.GiangVien;
import vn.phenikaa.person.NhanSu;
import vn.phenikaa.person.NhanVien;

public class NhanSuDAO {

    // ================== TẠO MÃ NV ==================
    private String taoMaNV(Connection c, String loai) throws SQLException {

        String sql = """
            SELECT MAX(CAST(SUBSTRING(maNV,3) AS UNSIGNED))
            FROM nhansu
            WHERE loai = ?
        """;

        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, loai);
            ResultSet rs = ps.executeQuery();
            int max = rs.next() ? rs.getInt(1) : 0;
            return loai + String.format("%03d", max + 1);
        }
    }

    // ================== INSERT ==================
    public void insert(NhanSu ns) {

        if (ns.getTruong() == null) {
            throw new IllegalStateException("Nhân sự chưa gán Trường!");
        }

        String loai = (ns instanceof GiangVien) ? "GV" : "NV";

        try (Connection c = DBConnection.getConnection()) {

            c.setAutoCommit(false);

            try {
                String maNV = taoMaNV(c, loai);
                ns.setMaNV(maNV);

                // ===== insert nhansu =====
                try (PreparedStatement ps = c.prepareStatement("""
                    INSERT INTO nhansu
                    (maNV, hoTen, ngaySinh, email, luongCoBan, loai, truong_id)
                    VALUES (?, ?, ?, ?, ?, ?, ?)
                """)) {

                    ps.setString(1, maNV);
                    ps.setString(2, ns.getHoTen());
                    ps.setDate(3, Date.valueOf(ns.getNgaySinh()));
                    ps.setString(4, ns.getEmail());
                    ps.setDouble(5, ns.getLuongCoBan());
                    ps.setString(6, loai);
                    ps.setInt(7, ns.getTruong().getId());
                    ps.executeUpdate();
                }

                // ===== insert bảng con =====
                if (ns instanceof GiangVien gv) {
                    try (PreparedStatement ps = c.prepareStatement("""
                        INSERT INTO giangvien(maNV, soGioGiang, tienMoiGio)
                        VALUES (?, ?, ?)
                    """)) {
                        ps.setString(1, maNV);
                        ps.setInt(2, gv.getSoGioGiang());
                        ps.setDouble(3, gv.getTienMoiGio());
                        ps.executeUpdate();
                    }
                } else {
                    NhanVien nv = (NhanVien) ns;
                    try (PreparedStatement ps = c.prepareStatement("""
                        INSERT INTO nhanvien(maNV, phuCap)
                        VALUES (?, ?)
                    """)) {
                        ps.setString(1, maNV);
                        ps.setDouble(2, nv.getPhuCap());
                        ps.executeUpdate();
                    }
                }

                c.commit();

            } catch (Exception e) {
                c.rollback();
                throw e;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ================== GET ALL ==================
    public ArrayList<NhanSu> getByTruong(int truongId) {

    ArrayList<NhanSu> list = new ArrayList<>();

    String sql = """
        SELECT n.*,
               t.id AS truongId,
               t.maTruong,
               t.tenTruong,
               gv.soGioGiang,
               gv.tienMoiGio,
               nv.phuCap
        FROM nhansu n
        JOIN truong t ON n.truong_id = t.id
        LEFT JOIN giangvien gv ON n.maNV = gv.maNV
        LEFT JOIN nhanvien nv ON n.maNV = nv.maNV
        WHERE t.id = ?
    """;

    try (Connection c = DBConnection.getConnection();
         PreparedStatement ps = c.prepareStatement(sql)) {

        ps.setInt(1, truongId);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            list.add(map(rs));
        }

    } catch (Exception e) {
        e.printStackTrace();
    }

    return list;
}


    // ================== DELETE ==================
    public boolean delete(String maNV) {

        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps =
                 c.prepareStatement("DELETE FROM nhansu WHERE maNV=?")) {

            ps.setString(1, maNV);
            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // ================== UPDATE ==================
    public void update(NhanSu ns) {

        if (ns.getTruong() == null) {
            throw new IllegalStateException("Nhân sự chưa gán Trường!");
        }

        try (Connection c = DBConnection.getConnection()) {

            try (PreparedStatement ps = c.prepareStatement("""
                UPDATE nhansu
                SET hoTen=?, ngaySinh=?, email=?, luongCoBan=?, truong_id=?
                WHERE maNV=?
            """)) {

                ps.setString(1, ns.getHoTen());
                ps.setDate(2, Date.valueOf(ns.getNgaySinh()));
                ps.setString(3, ns.getEmail());
                ps.setDouble(4, ns.getLuongCoBan());
                ps.setInt(5, ns.getTruong().getId());
                ps.setString(6, ns.getMaNV());
                ps.executeUpdate();
            }

            if (ns instanceof GiangVien gv) {
                try (PreparedStatement ps = c.prepareStatement("""
                    UPDATE giangvien
                    SET soGioGiang=?, tienMoiGio=?
                    WHERE maNV=?
                """)) {
                    ps.setInt(1, gv.getSoGioGiang());
                    ps.setDouble(2, gv.getTienMoiGio());
                    ps.setString(3, gv.getMaNV());
                    ps.executeUpdate();
                }
            }

            if (ns instanceof NhanVien nv) {
                try (PreparedStatement ps = c.prepareStatement("""
                    UPDATE nhanvien
                    SET phuCap=?
                    WHERE maNV=?
                """)) {
                    ps.setDouble(1, nv.getPhuCap());
                    ps.setString(2, nv.getMaNV());
                    ps.executeUpdate();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ================== SEARCH ==================
    public ArrayList<NhanSu> search(String key) {

        ArrayList<NhanSu> list = new ArrayList<>();

        String sql = """
            SELECT n.*, 
                   t.id AS truongId,
                   t.maTruong,
                   t.tenTruong,
                   gv.soGioGiang,
                   gv.tienMoiGio,
                   nv.phuCap
            FROM nhansu n
            JOIN truong t ON n.truong_id = t.id
            LEFT JOIN giangvien gv ON n.maNV = gv.maNV
            LEFT JOIN nhanvien nv ON n.maNV = nv.maNV
            WHERE n.maNV LIKE ? OR n.hoTen LIKE ?
        """;

        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            String k = "%" + key + "%";
            ps.setString(1, k);
            ps.setString(2, k);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(map(rs));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

   // ================== MAP ==================
private NhanSu map(ResultSet rs) throws SQLException {

    String ma = rs.getString("maNV");
    String ten = rs.getString("hoTen");
    String email = rs.getString("email");
    double luong = rs.getDouble("luongCoBan");

    LocalDate ngay = rs.getDate("ngaySinh") != null
            ? rs.getDate("ngaySinh").toLocalDate()
            : LocalDate.now();

    Truong t = new Truong(
            rs.getInt("truongId"),
            rs.getString("maTruong"),
            rs.getString("tenTruong")
    );

    // ===== GIẢNG VIÊN =====
    if ("GV".equals(rs.getString("loai"))) {

        GiangVien gv = new GiangVien(
                ten,
                ngay,
                email,
                rs.getInt("soGioGiang"),
                rs.getDouble("tienMoiGio")
        );

        gv.setMaNV(ma);
        gv.setLuongCoBan(luong);
        gv.setTruong(t);

        return gv;
    }

    // ===== NHÂN VIÊN =====
    NhanVien nv = new NhanVien(
            ten,
            ngay,
            email,
            rs.getDouble("phuCap")
    );

    nv.setMaNV(ma);
    nv.setLuongCoBan(luong);
    nv.setTruong(t);

    return nv;
    }
}