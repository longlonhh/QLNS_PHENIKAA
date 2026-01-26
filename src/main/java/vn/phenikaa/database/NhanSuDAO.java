package vn.phenikaa.database;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;

import vn.phenikaa.organization.Truong;
import vn.phenikaa.person.LoaiNhanSu;
import vn.phenikaa.person.NhanSu;
import vn.phenikaa.person.giangvien.GiangVien;
import vn.phenikaa.person.giangvien.GiangVienDay;
import vn.phenikaa.person.giangvien.NghienCuuVien;
import vn.phenikaa.person.nhanvien.GiamDocTruong;
import vn.phenikaa.person.nhanvien.NhanVien;
import vn.phenikaa.person.nhanvien.PhoGiamDocTruong;
import vn.phenikaa.person.nhanvien.hanhchinh.ChuyenVien;
import vn.phenikaa.person.nhanvien.hanhchinh.ITSupport;
import vn.phenikaa.person.nhanvien.hanhchinh.KeToan;
import vn.phenikaa.person.nhanvien.hanhchinh.KyThuatVien;
import vn.phenikaa.person.nhanvien.hanhchinh.ThuKy;
import vn.phenikaa.person.phutro.BaoVe;
import vn.phenikaa.person.phutro.PhuTro;
import vn.phenikaa.person.phutro.TapVu;
import vn.phenikaa.person.phutro.VeSinh;



public class NhanSuDAO {

    // ================== TẠO MÃ NV ==================
    private String taoMaNV(Connection c, String loai) throws SQLException {
        String sql = "SELECT MAX(CAST(SUBSTRING(maNV,3) AS UNSIGNED)) FROM nhansu WHERE loai = ?";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, loai);
            ResultSet rs = ps.executeQuery();
            int max = rs.next() ? rs.getInt(1) : 0;
            return loai + String.format("%03d", max + 1);
        }
    }

    // ================== INSERT ==================
    public void insert(NhanSu ns) {
        if (ns.getTruong() == null) throw new IllegalStateException("Nhân sự chưa gán Trường!");

        // Phân loại nhãn mã (GV001, NV001, PT001)
        String loaiLabel = (ns instanceof GiangVien) ? "GV" : (ns instanceof NhanVien ? "NV" : "PT");

        try (Connection c = DBConnection.getConnection()) {
            c.setAutoCommit(false);
            try {
                String maNV = taoMaNV(c, loaiLabel);
                ns.setMaNV(maNV);

                // 1. Insert bảng nhansu chung
                String sqlNS = "INSERT INTO nhansu (maNV, hoTen, ngaySinh, email, luongCoBan, loaiNhanSu, loai, truong_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
                try (PreparedStatement ps = c.prepareStatement(sqlNS)) {
                    ps.setString(1, maNV);
                    ps.setString(2, ns.getHoTen());
                    ps.setDate(3, Date.valueOf(ns.getNgaySinh()));
                    ps.setString(4, ns.getEmail());
                    ps.setDouble(5, ns.getLuongCoBan() != null ? ns.getLuongCoBan() : 0);
                    ps.setString(6, ns.getLoaiNhanSu().name());
                    ps.setString(7, loaiLabel);
                    ps.setInt(8, ns.getTruong().getId());
                    ps.executeUpdate();
                }

                // 2. Insert bảng con tương ứng
                if (ns instanceof GiangVien gv) {
                    try (PreparedStatement ps = c.prepareStatement("INSERT INTO giangvien (maNV, soGioGiang, tienMoiGio, phuCapNghienCuu) VALUES (?, ?, ?, ?)")) {
                        ps.setString(1, maNV);
                        ps.setInt(2, gv.getSoGioGiang());
                        ps.setDouble(3, gv.getTienMoiGio());
                        double pcnc = (gv instanceof NghienCuuVien ncv) ? ncv.getPhuCapNghienCuu() : 0;
                        ps.setDouble(4, pcnc);
                        ps.executeUpdate();
                    }
                } else if (ns instanceof NhanVien nv) {
                    try (PreparedStatement ps = c.prepareStatement("INSERT INTO nhanvien (maNV, phuCap) VALUES (?, ?)")) {
                        ps.setString(1, maNV);
                        ps.setDouble(2, nv.getPhuCap() != null ? nv.getPhuCap() : 0);
                        ps.executeUpdate();
                    }
                } else if (ns instanceof PhuTro pt) {
                    try (PreparedStatement ps = c.prepareStatement("INSERT INTO phutro (maNV, luongThang) VALUES (?, ?)")) {
                        ps.setString(1, maNV);
                        ps.setDouble(2, pt.tinhLuong()); // tinhLuong của PhuTro trả về luongThang
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
            SELECT n.*, t.maTruong, t.tenTruong, gv.soGioGiang, gv.tienMoiGio, gv.phuCapNghienCuu, nv.phuCap, pt.luongThang
            FROM nhansu n
            JOIN truong t ON n.truong_id = t.id
            LEFT JOIN giangvien gv ON n.maNV = gv.maNV
            LEFT JOIN nhanvien nv ON n.maNV = nv.maNV
            LEFT JOIN phutro pt ON n.maNV = pt.maNV
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
             PreparedStatement ps = c.prepareStatement("DELETE FROM nhansu WHERE maNV=?")) {
            ps.setString(1, maNV);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // ================== UPDATE ==================
    public void update(NhanSu ns) {
        try (Connection c = DBConnection.getConnection()) {
            c.setAutoCommit(false);
            try {
                String sqlNS = "UPDATE nhansu SET hoTen=?, ngaySinh=?, email=?, luongCoBan=?, truong_id=? WHERE maNV=?";
                try (PreparedStatement ps = c.prepareStatement(sqlNS)) {
                    ps.setString(1, ns.getHoTen());
                    ps.setDate(2, Date.valueOf(ns.getNgaySinh()));
                    ps.setString(3, ns.getEmail());
                    ps.setDouble(4, ns.getLuongCoBan() != null ? ns.getLuongCoBan() : 0);
                    ps.setInt(5, ns.getTruong().getId());
                    ps.setString(6, ns.getMaNV());
                    ps.executeUpdate();
                }

                if (ns instanceof GiangVien gv) {
                    try (PreparedStatement ps = c.prepareStatement("UPDATE giangvien SET soGioGiang=?, tienMoiGio=?, phuCapNghienCuu=? WHERE maNV=?")) {
                        ps.setInt(1, gv.getSoGioGiang());
                        ps.setDouble(2, gv.getTienMoiGio());
                        double pcnc = (gv instanceof NghienCuuVien ncv) ? ncv.getPhuCapNghienCuu() : 0;
                        ps.setDouble(3, pcnc);
                        ps.setString(4, gv.getMaNV());
                        ps.executeUpdate();
                    }
                } else if (ns instanceof NhanVien nv) {
                    try (PreparedStatement ps = c.prepareStatement("UPDATE nhanvien SET phuCap=? WHERE maNV=?")) {
                        ps.setDouble(1, nv.getPhuCap() != null ? nv.getPhuCap() : 0);
                        ps.setString(2, nv.getMaNV());
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

   // ================== MAP DATA ==================
    private NhanSu map(ResultSet rs) throws SQLException {
        String maNV = rs.getString("maNV");
    String hoTen = rs.getString("hoTen");
    
    // Kiểm tra NULL cho ngày sinh để tránh crash
    java.sql.Date sqlDate = rs.getDate("ngaySinh");
    LocalDate ngaySinh = (sqlDate != null) ? sqlDate.toLocalDate() : LocalDate.now();
    
    String email = rs.getString("email");
    double luongCoBan = rs.getDouble("luongCoBan");
    
    // Đảm bảo loaiNhanSu không NULL trước khi gọi valueOf
    String loaiStr = rs.getString("loaiNhanSu");
    LoaiNhanSu loaiEnum = (loaiStr != null) ? LoaiNhanSu.valueOf(loaiStr) : LoaiNhanSu.CHUYEN_VIEN;

        double phuCap = rs.getDouble("phuCap");
        int soGio = rs.getInt("soGioGiang");
        double tienGio = rs.getDouble("tienMoiGio");
        double phuCapNC = rs.getDouble("phuCapNghienCuu");
        double luongThangPT = rs.getDouble("luongThang");

        NhanSu ns = switch (loaiEnum) {
            case GIANG_VIEN_DAY -> new GiangVienDay(hoTen, ngaySinh, email, soGio, tienGio);
            case NGHIEN_CUU_VIEN -> new NghienCuuVien(hoTen, ngaySinh, email, soGio, tienGio, phuCapNC);
            case KE_TOAN -> new KeToan(hoTen, ngaySinh, email, phuCap);
            case CHUYEN_VIEN -> new ChuyenVien(hoTen, ngaySinh, email, phuCap);
            case THU_KY -> new ThuKy(hoTen, ngaySinh, email, phuCap);
            case KY_THUAT_VIEN -> new KyThuatVien(hoTen, ngaySinh, email, phuCap);
            case IT_SUPPORT -> new ITSupport(hoTen, ngaySinh, email, phuCap);
            case GIAM_DOC_TRUONG -> new GiamDocTruong(hoTen, ngaySinh, email, phuCap);
            case PHO_GIAM_DOC_TRUONG -> new PhoGiamDocTruong(hoTen, ngaySinh, email, phuCap);
            case BAO_VE -> new BaoVe(hoTen, ngaySinh, email, luongThangPT);
            case TAP_VU -> new TapVu(hoTen, ngaySinh, email, luongThangPT);
            case VE_SINH -> new VeSinh(hoTen, ngaySinh, email, luongThangPT);
        };

        ns.setMaNV(maNV);
        ns.setLuongCoBan(luongCoBan);
        ns.setLoaiNhanSu(loaiEnum);
        ns.setTruong(new Truong(rs.getInt("truong_id"), rs.getString("maTruong"), rs.getString("tenTruong")));
        
        return ns;
    }

}