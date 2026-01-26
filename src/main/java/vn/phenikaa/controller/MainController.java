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
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;
import vn.phenikaa.database.NhanSuDAO;
import vn.phenikaa.database.TruongDAO;
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
import vn.phenikaa.person.nhanvien.hanhchinh.ThuKy;
import vn.phenikaa.person.phutro.BaoVe;
import vn.phenikaa.person.phutro.PhuTro;
import vn.phenikaa.person.phutro.TapVu;
import vn.phenikaa.person.phutro.VeSinh;

public class MainController {

    // ================= UI =================
    @FXML private StackPane rootPane;
    @FXML private TableView<NhanSu> table;
    @FXML private TableColumn<NhanSu, String> colMa;
    @FXML private TableColumn<NhanSu, String> colTen;
    @FXML private TableColumn<NhanSu, String> colEmailTruong;
    @FXML private TableColumn<NhanSu, String> colLoai;
    @FXML private TableColumn<NhanSu, Double> colLuong;
    @FXML private TableColumn<NhanSu, Void> colAction;

    @FXML private TextField txtSearch;
    @FXML private ComboBox<Truong> cboTruong;

    @FXML private TabPane tabPane;
    @FXML private Tab tabGV;
    @FXML private Tab tabNV;
    @FXML private Tab tabAll;
    @FXML private Tab tabPT; 

    // ================= DAO =================
    private final NhanSuDAO nhanSuDAO = new NhanSuDAO();
    private final TruongDAO truongDAO = new TruongDAO();

    private NhanSu lastDeleted;

    // ================= INIT =================
    @FXML
    public void initialize() {

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
                        d.getValue().getLoaiNhanSu() != null
                                ? d.getValue().getLoaiNhanSu().name()
                                : ""
                )
        );

        colLuong.setCellValueFactory(d ->
                new SimpleObjectProperty<>(d.getValue().tinhLuong())
        );

        setupActionColumn();
        loadTruong();

        cboTruong.valueProperty().addListener(
                (obs, o, n) -> reloadTable()
        );

        tabPane.getSelectionModel()
                .selectedItemProperty()
                .addListener((obs, o, n) -> reloadTable());
    }

    // ================= LOAD TRƯỜNG =================
    private void loadTruong() {
        cboTruong.setItems(
                FXCollections.observableArrayList(truongDAO.getAll())
        );
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

    // ================= SEARCH =================
    @FXML
private void search() {
    Truong t = cboTruong.getValue();
    if (t == null) return;

    String keyword = txtSearch.getText().trim().toLowerCase();

    // 1. Lấy toàn bộ danh sách từ DB theo trường
    List<NhanSu> ds = nhanSuDAO.getByTruong(t.getId());

    // 2. Lọc theo Tab đang chọn trước
    ds = filterByTab(ds);

    // 3. Lọc theo từ khóa (Mã, Tên, hoặc Tên Loại nhân sự)
    if (!keyword.isEmpty()) {
        ds = ds.stream()
            .filter(ns -> {
                String ma = ns.getMaNV().toLowerCase();
                String ten = ns.getHoTen().toLowerCase();
                // Lấy tên loại (VD: GIANG_VIEN_DAY -> giang vien day)
                String loai = ns.getLoaiNhanSu() != null ? 
                             ns.getLoaiNhanSu().name().toLowerCase().replace("_", " ") : "";

                return ma.contains(keyword) || 
                       ten.contains(keyword) || 
                       loai.contains(keyword);
            })
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
                    reloadTable();
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

    // ================= UNDO =================
     private void showUndoToast() {
    Label msg = new Label("✔ Đã xóa");
    msg.setStyle("-fx-text-fill: white;"); // Đảm bảo chữ màu trắng để nổi bật trên nền tối

    Button undo = new Button("UNDO");
    undo.setStyle("-fx-text-fill: #4CAF50; -fx-font-weight: bold; -fx-background-color: transparent; -fx-cursor: hand;");

    // Xóa new Region() và HBox.setHgrow để các thành phần co lại gần nhau
    HBox toast = new HBox(20, msg, undo); 
    
    toast.setPadding(new Insets(10, 20, 10, 20)); // Padding cân đối
    toast.setAlignment(Pos.CENTER); // Căn giữa nội dung bên trong
    
    // THAY ĐỔI QUAN TRỌNG:
    toast.setMaxWidth(Region.USE_PREF_SIZE); // Tự động co theo độ dài của chữ
    toast.setMaxHeight(40);
    
    toast.setStyle("""
        -fx-background-color: #323232;
        -fx-background-radius: 25;
        """);

    StackPane.setAlignment(toast, Pos.BOTTOM_CENTER);
    StackPane.setMargin(toast, new Insets(0, 0, 40, 0));
    
    // Tránh việc cộng dồn nhiều Toast nếu bấm xóa liên tục
    rootPane.getChildren().removeIf(node -> node instanceof HBox && node.getStyle().contains("#323232"));
    rootPane.getChildren().add(toast);

    undo.setOnAction(e -> {
        if (lastDeleted != null) {
            nhanSuDAO.insert(lastDeleted);
            reloadTable();
            rootPane.getChildren().remove(toast);
        }
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
        new Alert(Alert.AlertType.WARNING, "Vui lòng chọn Trường trước!").show();
        return;
    }

    Dialog<NhanSu> dialog = new Dialog<>();
    dialog.setTitle("Thêm Giảng Viên Mới");
    dialog.setHeaderText("Nhập thông tin giảng viên");

    // Các thành phần giao diện
    TextField txtHoTen = new TextField();
    DatePicker dpNgaySinh = new DatePicker(LocalDate.now());
    TextField txtEmail = new TextField();
    TextField txtLuongCB = new TextField("0");
    TextField txtSoGio = new TextField("0");
    TextField txtTienGio = new TextField("0");
    TextField txtPhuCapNC = new TextField("0");

    // ComboBox chọn loại Giảng viên
    ComboBox<String> cboLoaiGV = new ComboBox<>(FXCollections.observableArrayList(
            "Giảng viên dạy", "Nghiên cứu viên"
    ));
    cboLoaiGV.setValue("Giảng viên dạy");

    // Layout cho Dialog
    GridPane grid = new GridPane();
    grid.setHgap(10); grid.setVgap(10);
    grid.setPadding(new Insets(20, 150, 10, 10));

    grid.add(new Label("Loại:"), 0, 0);      grid.add(cboLoaiGV, 1, 0);
    grid.add(new Label("Họ tên:"), 0, 1);    grid.add(txtHoTen, 1, 1);
    grid.add(new Label("Ngày sinh:"), 0, 2);  grid.add(dpNgaySinh, 1, 2);
    grid.add(new Label("Email:"), 0, 3);     grid.add(txtEmail, 1, 3);
    grid.add(new Label("Lương CB:"), 0, 4);  grid.add(txtLuongCB, 1, 4);
    grid.add(new Label("Số giờ:"), 0, 5);    grid.add(txtSoGio, 1, 5);
    grid.add(new Label("Tiền/giờ:"), 0, 6);  grid.add(txtTienGio, 1, 6);
    
    // Label và Field này chỉ hiện khi chọn Nghiên cứu viên
    Label lblPC = new Label("Phụ cấp NC:");
    grid.add(lblPC, 0, 7);                   grid.add(txtPhuCapNC, 1, 7);

    dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
    dialog.getDialogPane().setContent(grid);

    // Logic xử lý khi nhấn OK
    dialog.setResultConverter(dialogButton -> {
        if (dialogButton == ButtonType.OK) {
            try {
                String hoTen = txtHoTen.getText();
                LocalDate ngaySinh = dpNgaySinh.getValue();
                String email = txtEmail.getText();
                int soGio = Integer.parseInt(txtSoGio.getText());
                double tienGio = Double.parseDouble(txtTienGio.getText());
                double luongCB = Double.parseDouble(txtLuongCB.getText());

                if (cboLoaiGV.getValue().equals("Nghiên cứu viên")) {
                    double phuCap = Double.parseDouble(txtPhuCapNC.getText());
                    NghienCuuVien ncv = new NghienCuuVien(hoTen, ngaySinh, email, soGio, tienGio, phuCap);
                    ncv.setLoaiNhanSu(LoaiNhanSu.NGHIEN_CUU_VIEN);
                    return ncv;
                } else {
                    GiangVienDay gvd = new GiangVienDay(hoTen, ngaySinh, email, soGio, tienGio);
                    gvd.setLoaiNhanSu(LoaiNhanSu.GIANG_VIEN_DAY);
                    return gvd;
                }
            } catch (Exception e) {
                new Alert(Alert.AlertType.ERROR, "Dữ liệu nhập vào không hợp lệ!").show();
            }
        }
        return null;
    });

    dialog.showAndWait().ifPresent(ns -> {
        ns.setTruong(t);
        ns.setLuongCoBan(Double.parseDouble(txtLuongCB.getText()));
        nhanSuDAO.insert(ns);
        reloadTable();
    });
}

    @FXML
public void themNhanVien() {
    Truong t = cboTruong.getValue();
    if (t == null) {
        new Alert(Alert.AlertType.WARNING, "Vui lòng chọn Trường trước!").show();
        return;
    }

    Dialog<NhanSu> dialog = new Dialog<>();
    dialog.setTitle("Thêm Nhân Viên Hành Chính");
    dialog.setHeaderText("Thông tin nhân viên mới");

    // Thành phần giao diện
    TextField txtHoTen = new TextField();
    DatePicker dpNgaySinh = new DatePicker(LocalDate.now());
    TextField txtEmail = new TextField();
    TextField txtLuongCB = new TextField("0");
    TextField txtPhuCap = new TextField("0");

    ComboBox<LoaiNhanSu> cboLoai = new ComboBox<>(FXCollections.observableArrayList(
            LoaiNhanSu.CHUYEN_VIEN, LoaiNhanSu.KE_TOAN, LoaiNhanSu.THU_KY, 
            LoaiNhanSu.IT_SUPPORT, LoaiNhanSu.KY_THUAT_VIEN,
            LoaiNhanSu.GIAM_DOC_TRUONG, LoaiNhanSu.PHO_GIAM_DOC_TRUONG
    ));
    cboLoai.setValue(LoaiNhanSu.CHUYEN_VIEN);

    GridPane grid = new GridPane();
    grid.setHgap(10); grid.setVgap(10);
    grid.setPadding(new Insets(20));

    grid.add(new Label("Chức vụ:"), 0, 0);    grid.add(cboLoai, 1, 0);
    grid.add(new Label("Họ tên:"), 0, 1);     grid.add(txtHoTen, 1, 1);
    grid.add(new Label("Ngày sinh:"), 0, 2);   grid.add(dpNgaySinh, 1, 2);
    grid.add(new Label("Email:"), 0, 3);      grid.add(txtEmail, 1, 3);
    grid.add(new Label("Lương Cứng:"), 0, 4);   grid.add(txtLuongCB, 1, 4);
    grid.add(new Label("Phụ cấp:"), 0, 5);    grid.add(txtPhuCap, 1, 5);

    dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
    dialog.getDialogPane().setContent(grid);

    dialog.setResultConverter(bt -> {
        if (bt == ButtonType.OK) {
            try {
                String name = txtHoTen.getText();
                LocalDate birth = dpNgaySinh.getValue();
                String mail = txtEmail.getText();
                double pc = Double.parseDouble(txtPhuCap.getText());
                LoaiNhanSu selected = cboLoai.getValue();

                // Dùng switch để khởi tạo đúng Class
                NhanVien nv = switch (selected) {
                    case KE_TOAN -> new KeToan(name, birth, mail, pc);
                    case IT_SUPPORT -> new ITSupport(name, birth, mail, pc);
                    case THU_KY -> new ThuKy(name, birth, mail, pc);
                    case GIAM_DOC_TRUONG -> new GiamDocTruong(name, birth, mail, pc);
                    case PHO_GIAM_DOC_TRUONG -> new PhoGiamDocTruong(name, birth, mail, pc);
                    default -> new ChuyenVien(name, birth, mail, pc);
                };
                nv.setLoaiNhanSu(selected);
                return nv;
            } catch (Exception e) { return null; }
        }
        return null;
    });

    dialog.showAndWait().ifPresent(ns -> {
        ns.setTruong(t);
        ns.setLuongCoBan(Double.parseDouble(txtLuongCB.getText()));
        nhanSuDAO.insert(ns);
        reloadTable();
    });
}

@FXML
public void themPhuTro() {
    Truong t = cboTruong.getValue();
    if (t == null) {
        new Alert(Alert.AlertType.WARNING, "Vui lòng chọn Trường trước!").show();
        return;
    }

    Dialog<NhanSu> dialog = new Dialog<>();
    dialog.setTitle("Thêm Nhân Viên Phụ Trợ");

    TextField txtHoTen = new TextField();
    DatePicker dpNgaySinh = new DatePicker(LocalDate.now());
    TextField txtEmail = new TextField();
    TextField txtLuongThang = new TextField("0");

    ComboBox<LoaiNhanSu> cboLoai = new ComboBox<>(FXCollections.observableArrayList(
            LoaiNhanSu.BAO_VE, LoaiNhanSu.TAP_VU, LoaiNhanSu.VE_SINH
    ));
    cboLoai.setValue(LoaiNhanSu.BAO_VE);

    GridPane grid = new GridPane();
    grid.setHgap(10); grid.setVgap(10);
    grid.setPadding(new Insets(20));

    grid.add(new Label("Công việc:"), 0, 0);   grid.add(cboLoai, 1, 0);
    grid.add(new Label("Họ tên:"), 0, 1);      grid.add(txtHoTen, 1, 1);
    grid.add(new Label("Ngày sinh:"), 0, 2);    grid.add(dpNgaySinh, 1, 2);
    grid.add(new Label("Email:"), 0, 3);       grid.add(txtEmail, 1, 3);
    grid.add(new Label("Lương tháng:"), 0, 4); grid.add(txtLuongThang, 1, 4);

    dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
    dialog.getDialogPane().setContent(grid);

    dialog.setResultConverter(bt -> {
        if (bt == ButtonType.OK) {
            try {
                String name = txtHoTen.getText();
                LocalDate birth = dpNgaySinh.getValue();
                String mail = txtEmail.getText();
                double luong = Double.parseDouble(txtLuongThang.getText());
                LoaiNhanSu selected = cboLoai.getValue();

                PhuTro pt = switch (selected) {
                    case TAP_VU -> new TapVu(name, birth, mail, luong);
                    case VE_SINH -> new VeSinh(name, birth, mail, luong);
                    default -> new BaoVe(name, birth, mail, luong);
                };
                pt.setLoaiNhanSu(selected);
                return pt;
            } catch (Exception e) { return null; }
        }
        return null;
    });

    dialog.showAndWait().ifPresent(ns -> {
        ns.setTruong(t);
        ns.setLuongCoBan(0.0); // Nhóm phụ trợ thường dùng lương tháng khoán
        nhanSuDAO.insert(ns);
        reloadTable();
    });
}
    // ================= RELOAD =================
    private void reloadTable() {
    try {
        Truong t = cboTruong.getValue();
        if (t == null) {
            table.getItems().clear();
            return;
        }

        List<NhanSu> ds = nhanSuDAO.getByTruong(t.getId());
        
        // Kiểm tra xem danh sách lấy về có null không
        if (ds == null) {
            System.out.println("Cảnh báo: DAO trả về danh sách NULL");
            return;
        }

        ds = filterByTab(ds);
        table.setItems(FXCollections.observableArrayList(ds));
        table.refresh(); // Buộc table vẽ lại dữ liệu
        
    } catch (Exception e) {
        System.err.println("Lỗi khi tải dữ liệu: " + e.getMessage());
        e.printStackTrace();
        new Alert(Alert.AlertType.ERROR, "Không thể tải dữ liệu: " + e.toString()).show();
    }
}
private List<NhanSu> filterByTab(List<NhanSu> ds) {
    Tab tab = tabPane.getSelectionModel().getSelectedItem();
    if (tab == null) return ds;

    // Sử dụng Text trên Tab để so sánh nếu fx:id không khớp
    String tabText = tab.getText(); 

    if (tabText.contains("Giảng viên") || tab == tabGV) {
        return ds.stream().filter(ns -> ns instanceof GiangVien).toList();
    }

    if (tabText.contains("Nhân viên") || tab == tabNV) {
        return ds.stream().filter(ns -> ns instanceof NhanVien).toList();
    }

    if (tabText.contains("Phụ trợ") || tab == tabPT) {
        return ds.stream().filter(ns -> ns instanceof PhuTro).toList();
    }

    return ds;
}

    @FXML
    public void tatCa() {
        reloadTable();
    }
}
