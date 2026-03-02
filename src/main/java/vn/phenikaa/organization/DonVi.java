package vn.phenikaa.organization;

import java.util.ArrayList;
import java.util.List;

public class DonVi {
    public enum LoaiDonVi {
        DAI_HOC, PHONG_BAN, TRUONG_THANH_VIEN, VIEN_NGHIEN_CUU, KHOA, BO_MON
    }

    private int id;
    private String maDonVi;
    private String tenDonVi;
    private LoaiDonVi loaiDonVi;
    private DonVi parent;
    private List<DonVi> children = new ArrayList<>();

    public DonVi(int id, String maDonVi, String tenDonVi, LoaiDonVi loaiDonVi) {
        this.id = id;
        this.maDonVi = maDonVi;
        this.tenDonVi = tenDonVi;
        this.loaiDonVi = loaiDonVi;
    }

    public int getId() {
        return id;
    }

    public String getMaDonVi() {
        return maDonVi;
    }

    public String getTenDonVi() {
        return tenDonVi;
    }

    public LoaiDonVi getLoaiDonVi() {
        return loaiDonVi;
    }

    public DonVi getParent() {
        return parent;
    }

    public void setParent(DonVi parent) {
        this.parent = parent;
    }

    public List<DonVi> getChildren() {
        return children;
    }

    @Override
    public String toString() {
        return tenDonVi;
    }
}
