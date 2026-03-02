package vn.phenikaa.main;

import vn.phenikaa.person.NguoiDung;

public class SessionManager {
    private static NguoiDung currentUser;

    public static void login(NguoiDung user) {
        currentUser = user;
    }

    public static void logout() {
        currentUser = null;
    }

    public static NguoiDung getCurrentUser() {
        return currentUser;
    }

    public static boolean isAdmin() {
        return currentUser != null && currentUser.getRole() == vn.phenikaa.person.VaiTro.ADMIN;
    }
}
