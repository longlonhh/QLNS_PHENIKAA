package vn.phenikaa.organization;

public class Truong {

    private int id;
    private String maTruong;
    private String tenTruong;

    public Truong(int id, String maTruong, String tenTruong) {
        this.id = id;
        this.maTruong = maTruong;
        this.tenTruong = tenTruong;
    }

    public int getId() {
        return id;
    }

    public String getMaTruong() {
        return maTruong;
    }

    public String getTenTruong() {
        return tenTruong;
    }

    @Override
    public String toString() {
        return tenTruong;
    }
}
