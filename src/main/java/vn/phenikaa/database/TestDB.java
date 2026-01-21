package vn.phenikaa.database;

public class TestDB {

    public static void main(String[] args) {
        DBConnection.getConnection();
        System.out.println("✅ CONNECT OK");
    }
}
