package vn.phenikaa.person;

public class NguoiDung {
    private int id;
    private String username;
    private String password;
    private VaiTro role;

    public NguoiDung(int id, String username, String password, VaiTro role) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.role = role;
    }

    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public VaiTro getRole() {
        return role;
    }
}
