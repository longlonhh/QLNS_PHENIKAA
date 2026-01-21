package vn.phenikaa.database;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;

import vn.phenikaa.person.GiangVien;
import vn.phenikaa.person.NhanSu;
import vn.phenikaa.person.NhanVien;

public class NhanSuDAO {

    // ===== TẠO MÃ =====
    private String taoMaNV(Connection c, String loai) throws SQLException {

        String sql = """
            SELECT MAX(CAST(SUBSTRING(maNV,3) AS UNSIGNED))
            FROM nhansu WHERE loai=?
        """;

        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, loai);
            ResultSet rs = ps.executeQuery();
            int max = rs.next() ? rs.getInt(1) : 0;
            return loai + String.format("%03d", max + 1);
        }
    }

    // ===== INSERT =====
    public void insert(NhanSu ns) {

        String loai = (ns instanceof GiangVien) ? "GV" : "NV";

        try (Connection c = DBConnection.getConnection()) {

            c.setAutoCommit(false);

            String ma = taoMaNV(c, loai);
            ns.setMaNV(ma);

            PreparedStatement ps = c.prepareStatement("""
                INSERT INTO nhansu(maNV, hoTen, ngaySinh, email, luongCoBan, loai)
                VALUES (?, ?, ?, ?, ?, ?)
            """);

            ps.setString(1, ma);
            ps.setString(2, ns.getHoTen());
            ps.setDate(3, Date.valueOf(ns.getNgaySinh()));
            ps.setString(4, ns.getEmail());
            ps.setDouble(5, ns.getLuongCoBan());
            ps.setString(6, loai);
            ps.executeUpdate();

            if (ns instanceof GiangVien gv) {
                ps = c.prepareStatement("""
                    INSERT INTO giangvien(maNV, soGioGiang, tienMoiGio)
                    VALUES (?, ?, ?)
                """);
                ps.setString(1, ma);
                ps.setInt(2, gv.getSoGioGiang());
                ps.setDouble(3, gv.getTienMoiGio());
                ps.executeUpdate();
            } else {
                NhanVien nv = (NhanVien) ns;
                ps = c.prepareStatement("""
                    INSERT INTO nhanvien(maNV, phuCap)
                    VALUES (?, ?)
                """);
                ps.setString(1, ma);
                ps.setDouble(2, nv.getPhuCap());
                ps.executeUpdate();
            }

            c.commit();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ===== GET ALL =====
    public ArrayList<NhanSu> getAll() {

        ArrayList<NhanSu> list = new ArrayList<>();

        String sql = """
            SELECT n.*, gv.soGioGiang, gv.tienMoiGio, nv.phuCap
            FROM nhansu n
            LEFT JOIN giangvien gv ON n.maNV = gv.maNV
            LEFT JOIN nhanvien nv ON n.maNV = nv.maNV
        """;

        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(map(rs));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    // ===== DELETE (XÓA THẬT) =====
    public boolean delete(String maNV) {

        try (Connection c = DBConnection.getConnection()) {

            c.setAutoCommit(false);

            // ❗ XÓA BẢNG CON TRƯỚC
            PreparedStatement ps = c.prepareStatement(
                "DELETE FROM giangvien WHERE maNV=?"
            );
            ps.setString(1, maNV);
            ps.executeUpdate();

            ps = c.prepareStatement(
                "DELETE FROM nhanvien WHERE maNV=?"
            );
            ps.setString(1, maNV);
            ps.executeUpdate();

            // ❗ SAU ĐÓ MỚI XÓA NHANSU
            ps = c.prepareStatement(
                "DELETE FROM nhansu WHERE maNV=?"
            );
            ps.setString(1, maNV);
            int rows = ps.executeUpdate();

            c.commit();

            return rows > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // ===== UPDATE (SỬA ĐẦY ĐỦ) =====
    public void update(NhanSu ns) {

        try (Connection c = DBConnection.getConnection()) {

            // ❗ UPDATE NHANSU (CÓ LƯƠNG)
            PreparedStatement ps = c.prepareStatement("""
                UPDATE nhansu
                SET hoTen=?, ngaySinh=?, luongCoBan=?
                WHERE maNV=?
            """);

            ps.setString(1, ns.getHoTen());
            ps.setDate(2, Date.valueOf(ns.getNgaySinh()));
            ps.setDouble(3, ns.getLuongCoBan());
            ps.setString(4, ns.getMaNV());
            ps.executeUpdate();

            // ❗ UPDATE GIẢNG VIÊN
            if (ns instanceof GiangVien gv) {
                ps = c.prepareStatement("""
                    UPDATE giangvien
                    SET soGioGiang=?, tienMoiGio=?
                    WHERE maNV=?
                """);
                ps.setInt(1, gv.getSoGioGiang());
                ps.setDouble(2, gv.getTienMoiGio());
                ps.setString(3, gv.getMaNV());
                ps.executeUpdate();
            }

            // ❗ UPDATE NHÂN VIÊN
            if (ns instanceof NhanVien nv) {
                ps = c.prepareStatement("""
                    UPDATE nhanvien
                    SET phuCap=?
                    WHERE maNV=?
                """);
                ps.setDouble(1, nv.getPhuCap());
                ps.setString(2, nv.getMaNV());
                ps.executeUpdate();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ===== SEARCH =====
    public ArrayList<NhanSu> search(String key) {

        ArrayList<NhanSu> list = new ArrayList<>();

        String sql = """
            SELECT n.*, gv.soGioGiang, gv.tienMoiGio, nv.phuCap
            FROM nhansu n
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
            while (rs.next()) list.add(map(rs));

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    // ===== MAP =====
    private NhanSu map(ResultSet rs) throws SQLException {

        String ma = rs.getString("maNV");
        String ten = rs.getString("hoTen");
        double luong = rs.getDouble("luongCoBan");

        Date d = rs.getDate("ngaySinh");
        LocalDate ngay = (d != null) ? d.toLocalDate() : LocalDate.now();

        String loai = rs.getString("loai");

        if ("GV".equals(loai)) {
            GiangVien gv = new GiangVien(
                ten,
                ngay,
                "",
                "",
                rs.getInt("soGioGiang"),
                rs.getDouble("tienMoiGio")
            );
            gv.setMaNV(ma);
            gv.setLuongCoBan(luong);
            return gv;
        }

        NhanVien nv = new NhanVien(
            ten,
            ngay,
            "",
            "",
            rs.getDouble("phuCap")
        );
        nv.setMaNV(ma);
        nv.setLuongCoBan(luong);
        return nv;
    }
}
