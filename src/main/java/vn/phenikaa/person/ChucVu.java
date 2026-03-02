package vn.phenikaa.person;

public class ChucVu {
    private int id;
    private String tenChucVu;
    private double heSoPhuCap;

    public ChucVu(int id, String tenChucVu, double heSoPhuCap) {
        this.id = id;
        this.tenChucVu = tenChucVu;
        this.heSoPhuCap = heSoPhuCap;
    }

    public int getId() {
        return id;
    }

    public String getTenChucVu() {
        return tenChucVu;
    }

    public double getHeSoPhuCap() {
        return heSoPhuCap;
    }

    @Override
    public String toString() {
        return tenChucVu;
    }
}
