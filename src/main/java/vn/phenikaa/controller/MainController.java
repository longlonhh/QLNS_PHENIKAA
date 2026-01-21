package vn.phenikaa.controller;

import java.time.LocalDate;
import java.util.List;

import javafx.animation.PauseTransition;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;
import vn.phenikaa.database.NhanSuDAO;
import vn.phenikaa.database.TruongDAO;
import vn.phenikaa.organization.Truong;
import vn.phenikaa.person.GiangVien;
import vn.phenikaa.person.NhanSu;
import vn.phenikaa.person.NhanVien;

public class MainController {

    // ================= UI =================
    @FXML private StackPane rootPane;
    @FXML private TableView<NhanSu> table;
    @FXML private TableColumn<NhanSu, String> colMa;
    @FXML private TableColumn<NhanSu, String> colTen;
    @FXML private TableColumn<NhanSu, String> colLoai;
    @FXML private TableColumn<NhanSu, Double> colLuong;
    @FXML private TableColumn<NhanSu, Void> colAction;
    @FXML private TextField txtSearch;
    @FXML private ComboBox<Truong> cboTruong;
    @FXML private TabPane tabPane;
    @FXML private Tab tabGV;
    @FXML private Tab tabNV;
    @FXML private Tab tabAll;


    // ================= DAO =================
    private final NhanSuDAO nhanSuDAO = new NhanSuDAO();
    private final TruongDAO truongDAO = new TruongDAO();

    private NhanSu lastDeleted;

    // ================= INIT =================
    @FXML
public void initialize() {

    // ================= TABLE COLUMNS =================
    colMa.setCellValueFactory(d ->
            new SimpleStringProperty(d.getValue().getMaNV())
    );

    colTen.setCellValueFactory(d ->
            new SimpleStringProperty(d.getValue().getHoTen())
    );

    colEmailTruong.setCellValueFactory(d ->
            new SimpleStringProperty(d.getValue().getEmailTruong())
    );

    colLoai.setCellValueFactory(d ->
            new SimpleStringProperty(
                    d.getValue() instanceof GiangVien
                            ? "Giảng viên"
                            : "Nhân viên"
            )
    );

    colLuong.setCellValueFactory(d ->
            new SimpleObjectProperty<>(d.getValue().tinhLuong())
    );

    // ================= DISABLE MENU KHI CHƯA CHỌN TRƯỜNG =================
    cboTruong.valueProperty().addListener((obs, oldV, newV) -> {
        boolean disable = (newV == null);

        rootPane.lookupAll(".btn-menu").forEach(n -> {
            if (n instanceof Button b && !b.getText().contains("Trường")) {
                b.setDisable(disable);
            }
        });

        // khi đổi trường → load lại bảng
        reloadTable();
    });

    // ================= TAB CHANGE =================
    tabPane.getSelectionModel()
            .selectedItemProperty()
            .addListener((obs, oldTab, newTab) -> reloadTable());

    // ================= ACTION COLUMN =================
    setupActionColumn();

    // ================= LOAD TRƯỜNG =================
    loadTruong();
}


    // ================= TRƯỜNG =================
    private void loadTruong() {
        cboTruong.setItems(
            FXCollections.observableArrayList(truongDAO.getAll())
        );

        cboTruong.getSelectionModel().selectedItemProperty()
            .addListener((obs, o, n) -> loadNhanSuTheoTruong());
    }

    private void loadNhanSuTheoTruong() {
    reloadTable();
}


    // ================= SEARCH =================
    @FXML
private void search() {

    Truong t = cboTruong.getValue();
    if (t == null) return;

    String keyword = txtSearch.getText().trim().toLowerCase();

    List<NhanSu> ds = nhanSuDAO.getByTruong(t.getId());

    // 🔥 LỌC THEO TAB TRƯỚC
    ds = filterByTab(ds);

    // 🔥 SAU ĐÓ MỚI TÌM
    if (!keyword.isEmpty()) {
        ds = ds.stream()
                .filter(ns ->
                        ns.getMaNV().toLowerCase().contains(keyword) ||
                        ns.getHoTen().toLowerCase().contains(keyword)
                )
                .toList();
    }

    table.setItems(FXCollections.observableArrayList(ds));
}


    // ================= ACTION COLUMN =================
    private void setupActionColumn() {

        colAction.setCellFactory(col -> new TableCell<>() {

            private final Button btnSua = new Button("✏");
            private final Button btnXoa = new Button("❌");
            private final HBox box = new HBox(6, btnSua, btnXoa);

            {
                btnSua.setOnAction(e -> {
                    NhanSu ns = getTableRow().getItem();
                    if (ns != null) suaNhanSu(ns);
                });

                btnXoa.setOnAction(e -> {
                    NhanSu ns = getTableRow().getItem();
                    if (ns == null) return;

                    if (new Alert(
                        Alert.AlertType.CONFIRMATION,
                        "Xóa " + ns.getMaNV() + " ?",
                        ButtonType.OK, ButtonType.CANCEL
                    ).showAndWait().orElse(ButtonType.CANCEL)
                        != ButtonType.OK) return;

                    nhanSuDAO.delete(ns.getMaNV());
                    lastDeleted = ns;
                    loadNhanSuTheoTruong();
                    showUndoToast();
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : box);
            }
        });
    }

    // ================= UNDO TOAST =================
    private void showUndoToast() {

        Label msg = new Label("✔ Đã xóa");
        Button undo = new Button("UNDO");

        HBox toast = new HBox(10, msg, new Region(), undo);
        HBox.setHgrow(toast.getChildren().get(1), Priority.ALWAYS);

        toast.setPadding(new Insets(10));
        toast.setAlignment(Pos.CENTER_LEFT);
        toast.setStyle("""
            -fx-background-color: #323232;
            -fx-background-radius: 20;
        """);

        StackPane.setAlignment(toast, Pos.BOTTOM_CENTER);
        StackPane.setMargin(toast, new Insets(0, 0, 40, 0));
        rootPane.getChildren().add(toast);

        undo.setOnAction(e -> {
            nhanSuDAO.insert(lastDeleted);
            loadNhanSuTheoTruong();
            rootPane.getChildren().remove(toast);
        });

        PauseTransition pt = new PauseTransition(Duration.seconds(3));
        pt.setOnFinished(e -> rootPane.getChildren().remove(toast));
        pt.play();
    }

    // ================= CRUD =================
    private void suaNhanSu(NhanSu ns) {

    Dialog<ButtonType> dialog = new Dialog<>();
    dialog.setTitle("Sửa nhân sự");

    // ===== FIELDS CHUNG =====
    TextField txtTen = new TextField(ns.getHoTen());
    DatePicker dpNgaySinh = new DatePicker(ns.getNgaySinh());
    TextField txtLuongCoBan = new TextField(
            String.valueOf(ns.getLuongCoBan())
    );

    TextField txtEmail = new TextField(ns.getEmail());
    txtEmail.setDisable(true); // ❌ KHÔNG CHO SỬA

    ComboBox<Truong> cbo = new ComboBox<>(
            FXCollections.observableArrayList(truongDAO.getAll())
    );
    cbo.setValue(ns.getTruong());

    // ===== RIÊNG =====
    TextField txtSoGio = new TextField();
    TextField txtTienMoiGio = new TextField();
    TextField txtPhuCap = new TextField();

    if (ns instanceof GiangVien gv) {
        txtSoGio.setText(String.valueOf(gv.getSoGioGiang()));
        txtTienMoiGio.setText(String.valueOf(gv.getTienMoiGio()));
    }

    if (ns instanceof NhanVien nv) {
        txtPhuCap.setText(String.valueOf(nv.getPhuCap()));
    }

    // ===== LAYOUT =====
    GridPane grid = new GridPane();
    grid.setHgap(10);
    grid.setVgap(10);

    int r = 0;
    grid.add(new Label("Họ tên"), 0, r);
    grid.add(txtTen, 1, r++);

    grid.add(new Label("Ngày sinh"), 0, r);
    grid.add(dpNgaySinh, 1, r++);

    grid.add(new Label("Email"), 0, r);
    grid.add(txtEmail, 1, r++);

    grid.add(new Label("Lương cơ bản"), 0, r);
    grid.add(txtLuongCoBan, 1, r++);

    grid.add(new Label("Trường"), 0, r);
    grid.add(cbo, 1, r++);

    if (ns instanceof GiangVien) {
        grid.add(new Label("Số giờ giảng"), 0, r);
        grid.add(txtSoGio, 1, r++);

        grid.add(new Label("Tiền mỗi giờ"), 0, r);
        grid.add(txtTienMoiGio, 1, r++);
    }

    if (ns instanceof NhanVien) {
        grid.add(new Label("Phụ cấp"), 0, r);
        grid.add(txtPhuCap, 1, r++);
    }

    dialog.getDialogPane().setContent(grid);
    dialog.getDialogPane().getButtonTypes()
            .addAll(ButtonType.OK, ButtonType.CANCEL);

    // ===== SAVE =====
    dialog.showAndWait().ifPresent(bt -> {
        if (bt != ButtonType.OK) return;

        ns.setHoTen(txtTen.getText().trim());
        ns.setNgaySinh(dpNgaySinh.getValue());
        ns.setLuongCoBan(Double.parseDouble(txtLuongCoBan.getText()));
        ns.setTruong(cbo.getValue());

        if (ns instanceof GiangVien gv) {
            gv.setSoGioGiang(
                    Integer.parseInt(txtSoGio.getText())
            );
            gv.setTienMoiGio(
                    Double.parseDouble(txtTienMoiGio.getText())
            );
        }

        if (ns instanceof NhanVien nv) {
            nv.setPhuCap(
                    Double.parseDouble(txtPhuCap.getText())
            );
        }

        nhanSuDAO.update(ns);
        table.refresh();
    });
}


    // ================= ADD =================
    @FXML
    public void themGiangVien() {

        Truong t = cboTruong.getValue();
        if (t == null) {
            new Alert(Alert.AlertType.WARNING, "Chọn Trường trước!").show();
            return;
        }

        GiangVien gv = new GiangVien(
            "Giảng viên mới",
            LocalDate.now(),
            "",
            0,
            0.0
        );

        gv.setLuongCoBan(0.0);
        gv.setTruong(t);

        nhanSuDAO.insert(gv);
        loadNhanSuTheoTruong();
    }

    @FXML
    public void themNhanVien() {

        Truong t = cboTruong.getValue();
        if (t == null) {
            new Alert(Alert.AlertType.WARNING, "Chọn Trường trước!").show();
            return;
        }

        NhanVien nv = new NhanVien(
            "Nhân viên mới",
            LocalDate.now(),
            "",
            0.0
        );

        nv.setLuongCoBan(0.0);
        nv.setTruong(t);

        nhanSuDAO.insert(nv);
        loadNhanSuTheoTruong();
    }



    // ================= THÊM TRƯỜNG =================
    @FXML
    public void themTruong() {

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Thêm Trường");

        TextField txtMa = new TextField();
        TextField txtTen = new TextField();

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        grid.add(new Label("Mã Trường:"), 0, 0);
        grid.add(txtMa, 1, 0);
        grid.add(new Label("Tên Trường:"), 0, 1);
        grid.add(txtTen, 1, 1);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes()
            .addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.showAndWait().ifPresent(bt -> {
            if (bt == ButtonType.OK &&
                !txtMa.getText().isBlank() &&
                !txtTen.getText().isBlank()) {

                truongDAO.insert(
                    txtMa.getText().trim(),
                    txtTen.getText().trim()
                );
                loadTruong();
            }
        });
    }

    private void reloadTable() {

    Truong t = cboTruong.getValue();
    if (t == null) {
        table.getItems().clear();
        return;
    }

    // 1️⃣ Lấy toàn bộ nhân sự theo Trường
    List<NhanSu> ds = nhanSuDAO.getByTruong(t.getId());

    // 2️⃣ Lọc theo TAB
    Tab tab = tabPane.getSelectionModel().getSelectedItem();

    if (tab == tabGV) {
        ds = ds.stream()
                .filter(ns -> ns instanceof GiangVien)
                .toList();
    } else if (tab == tabNV) {
        ds = ds.stream()
                .filter(ns -> ns instanceof NhanVien)
                .toList();
    }
    // tabAll → giữ nguyên

    // 3️⃣ Đổ dữ liệu ra bảng
    table.setItems(FXCollections.observableArrayList(ds));
}



@FXML
public void tatCa() {
    reloadTable();
}

@FXML private TableColumn<NhanSu, String> colEmailTruong;


private List<NhanSu> filterByTab(List<NhanSu> ds) {

    Tab tab = tabPane.getSelectionModel().getSelectedItem();

    if (tab == tabGV) {
        return ds.stream()
                .filter(ns -> ns instanceof GiangVien)
                .toList();
    }

    if (tab == tabNV) {
        return ds.stream()
                .filter(ns -> ns instanceof NhanVien)
                .toList();
    }

    // tabAll
    return ds;
}

}
