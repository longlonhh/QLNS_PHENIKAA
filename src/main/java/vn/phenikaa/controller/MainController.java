package vn.phenikaa.controller;

import java.time.LocalDate;
import java.util.List;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.animation.PauseTransition;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import vn.phenikaa.organization.DonVi;
import vn.phenikaa.person.ChucVu;
import vn.phenikaa.database.DonViDAO;
import vn.phenikaa.database.ChucVuDAO;
import javafx.scene.control.PasswordField;
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
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;
import vn.phenikaa.database.NhanSuDAO;
import vn.phenikaa.main.SessionManager;
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
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.cell.PropertyValueFactory;

public class MainController {

    // ================= UI =================
    @FXML
    private StackPane rootPane;
    @FXML
    private ComboBox<DonVi> cboDonVi;
    @FXML
    private TabPane tabPane;
    @FXML
    private TableView<NhanSu> tblMain;
    @FXML
    private TableColumn<NhanSu, String> colMaNV, colHoTen, colNgaySinh, colEmail;
    @FXML
    private TableColumn<NhanSu, Double> colLuongCB;
    @FXML
    private TableColumn<NhanSu, String> colDonVi;
    @FXML
    private TableColumn<NhanSu, String> colChucVu;
    @FXML
    private TableColumn<NhanSu, Double> colTongLuong;
    @FXML
    private TableColumn<NhanSu, Void> colAction;

    @FXML
    private TextField txtSearch;

    @FXML
    private Tab tabAll;
    @FXML
    private Tab tabGV;
    @FXML
    private Tab tabNV;
    @FXML
    private Tab tabPT;
    @FXML
    private Button btnThemDonVi;
    @FXML
    private Button btnThemGV;
    @FXML
    private Button btnThemNV;
    @FXML
    private Button btnThemPT;
    @FXML
    private Button btnThemNguoiDung;

    // ================= DAO =================
    private final NhanSuDAO nhanSuDAO = new NhanSuDAO();
    private final DonViDAO donViDAO = new DonViDAO();
    private final ChucVuDAO chucVuDAO = new ChucVuDAO();
    private final vn.phenikaa.database.NguoiDungDAO nguoiDungDAO = new vn.phenikaa.database.NguoiDungDAO();

    private NhanSu lastDeleted;

    // ================= INIT =================
    @FXML
    public void initialize() {

        colMaNV.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getMaNV()));
        colHoTen.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getHoTen()));
        colNgaySinh.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getNgaySinh().toString()));
        colEmail.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getEmail()));
        colLuongCB.setCellValueFactory(new PropertyValueFactory<>("luongCoBan"));
        colDonVi.setCellValueFactory(
                cellData -> new SimpleStringProperty(cellData.getValue().getDonVi().getTenDonVi()));

        colChucVu.setCellValueFactory(
                cellData -> new SimpleStringProperty(cellData.getValue().getChucVu().getTenChucVu()));

        colTongLuong.setCellValueFactory(new PropertyValueFactory<>("luongCoBan"));
        colTongLuong.setCellFactory(tc -> new TableCell<NhanSu, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                    setText(null);
                } else {
                    setText(String.format("%,.0f", getTableRow().getItem().tinhTongLuong()));
                }
            }
        });

        setupActionColumn();
        loadDonVi();

        cboDonVi.valueProperty().addListener(
                (obs, o, n) -> reloadTable());

        tabPane.getSelectionModel()
                .selectedItemProperty()
                .addListener((obs, o, n) -> reloadTable());

        applyPermissions();
    }

    private void applyPermissions() {
        boolean isAdmin = SessionManager.isAdmin();
        btnThemDonVi.setVisible(isAdmin);
        btnThemGV.setVisible(isAdmin);
        btnThemNV.setVisible(isAdmin);
        btnThemPT.setVisible(isAdmin);
        btnThemNguoiDung.setVisible(isAdmin);
        // ColAction (Sửa/Xóa) will be handled in setupActionColumn
    }

    // ================= LOAD ĐƠN VỊ =================
    private void loadDonVi() {
        cboDonVi.setItems(
                FXCollections.observableArrayList(donViDAO.getAll()));
    }

    // ================= SEARCH =================
    @FXML
    private void search() {
        DonVi dv = cboDonVi.getValue();
        if (dv == null)
            return;

        String keyword = txtSearch.getText().trim().toLowerCase();

        // 1. Lấy toàn bộ danh sách từ DB theo đơn vị
        List<NhanSu> ds = nhanSuDAO.getByDonVi(dv.getId());

        // 2. Lọc theo Tab đang chọn trước
        ds = filterByTab(ds);

        // 3. Lọc theo từ khóa (Mã, Tên, hoặc Tên Loại nhân sự)
        if (!keyword.isEmpty()) {
            ds = ds.stream()
                    .filter(ns -> {
                        String ma = ns.getMaNV().toLowerCase();
                        String ten = ns.getHoTen().toLowerCase();
                        // Lấy tên loại (VD: GIANG_VIEN_DAY -> giang vien day)
                        String loai = ns.getLoaiNhanSu() != null
                                ? ns.getLoaiNhanSu().name().toLowerCase().replace("_", " ")
                                : "";

                        return ma.contains(keyword) ||
                                ten.contains(keyword) ||
                                loai.contains(keyword);
                    })
                    .toList();
        }

        tblMain.setItems(FXCollections.observableArrayList(ds));
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
                    if (ns != null)
                        suaNhanSu(ns);
                });

                btnXoa.setOnAction(e -> {
                    NhanSu ns = getTableRow().getItem();
                    if (ns == null)
                        return;

                    if (new Alert(
                            Alert.AlertType.CONFIRMATION,
                            "Xóa " + ns.getMaNV() + " ?",
                            ButtonType.OK, ButtonType.CANCEL).showAndWait().orElse(ButtonType.CANCEL) != ButtonType.OK)
                        return;

                    nhanSuDAO.delete(ns.getMaNV());
                    lastDeleted = ns;
                    reloadTable();
                    showUndoToast();
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || !SessionManager.isAdmin()) {
                    setGraphic(null);
                } else {
                    setGraphic(box);
                }
            }
        });
    }

    // ================= UNDO =================
    private void showUndoToast() {
        Label msg = new Label("✔ Đã xóa");
        msg.setStyle("-fx-text-fill: white;"); // Đảm bảo chữ màu trắng để nổi bật trên nền tối

        Button undo = new Button("UNDO");
        undo.setStyle(
                "-fx-text-fill: #4CAF50; -fx-font-weight: bold; -fx-background-color: transparent; -fx-cursor: hand;");

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
        TextField txtMa = new TextField(ns.getMaNV());
        txtMa.setEditable(false);
        TextField txtTen = new TextField(ns.getHoTen());
        DatePicker dpNgaySinh = new DatePicker(ns.getNgaySinh());
        TextField txtEmail = new TextField(ns.getEmail());
        TextField txtLuong = new TextField(String.valueOf(ns.getLuongCoBan()));

        // 1. Load data ComboBox
        ObservableList<DonVi> listDonVi = FXCollections.observableArrayList(donViDAO.getAll());
        ComboBox<DonVi> cboDV = new ComboBox<>(listDonVi);
        cboDV.setValue(ns.getDonVi());

        ComboBox<ChucVu> cboCV = new ComboBox<>(FXCollections.observableArrayList(chucVuDAO.getAll()));
        cboCV.setValue(ns.getChucVu());

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
        grid.add(new Label("Mã NV"), 0, r);
        grid.add(txtMa, 1, r++);

        grid.add(new Label("Họ tên"), 0, r);
        grid.add(txtTen, 1, r++);

        grid.add(new Label("Ngày sinh"), 0, r);
        grid.add(dpNgaySinh, 1, r++);

        grid.add(new Label("Email"), 0, r);
        grid.add(txtEmail, 1, r++);

        grid.add(new Label("Lương cơ bản"), 0, r);
        grid.add(txtLuong, 1, r++);

        grid.add(new Label("Đơn vị"), 0, r);
        grid.add(cboDV, 1, r++);

        grid.add(new Label("Chức vụ"), 0, r);
        grid.add(cboCV, 1, r++);

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
            if (bt != ButtonType.OK)
                return;

            ns.setHoTen(txtTen.getText().trim());
            ns.setNgaySinh(dpNgaySinh.getValue());
            ns.setLuongCoBan(Double.parseDouble(txtLuong.getText()));
            ns.setDonVi(cboDV.getValue());
            ns.setChucVu(cboCV.getValue());

            if (ns instanceof GiangVien gv) {
                gv.setSoGioGiang(
                        Integer.parseInt(txtSoGio.getText()));
                gv.setTienMoiGio(
                        Double.parseDouble(txtTienMoiGio.getText()));
            }

            if (ns instanceof NhanVien nv) {
                nv.setPhuCap(
                        Double.parseDouble(txtPhuCap.getText()));
            }

            nhanSuDAO.update(ns);
            tblMain.refresh();
        });
    }

    // ================= ADD =================
    @FXML
    public void themGiangVien() {
        DonVi dv = cboDonVi.getValue();
        if (dv == null) {
            new Alert(Alert.AlertType.WARNING, "Vui lòng chọn Đơn vị trước!").show();
            return;
        }

        Dialog<NhanSu> dialog = new Dialog<>();
        dialog.setTitle("Thêm Giảng Viên Mới");
        dialog.setHeaderText("Nhập thông tin giảng viên");

        // Các thành phần giao diện
        TextField txtTen = new TextField();
        DatePicker dpNgaySinh = new DatePicker(LocalDate.now());
        TextField txtLuong = new TextField("0");
        ComboBox<DonVi> cboDV = new ComboBox<>(cboDonVi.getItems());
        cboDV.setValue(cboDonVi.getValue());
        ComboBox<ChucVu> cboCV = new ComboBox<>(FXCollections.observableArrayList(chucVuDAO.getAll()));

        TextField txtGio = new TextField("0");
        TextField txtTien = new TextField("0");
        TextField txtPhuCapNC = new TextField("0"); // For NghienCuuVien

        // ComboBox chọn loại Giảng viên
        ComboBox<String> cboLoaiGV = new ComboBox<>(FXCollections.observableArrayList(
                "Giảng viên dạy", "Nghiên cứu viên"));
        cboLoaiGV.setValue("Giảng viên dạy");

        // Layout cho Dialog
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        grid.add(new Label("Loại:"), 0, 0);
        grid.add(cboLoaiGV, 1, 0);
        grid.add(new Label("Họ tên:"), 0, 1);
        grid.add(txtTen, 1, 1);
        grid.add(new Label("Ngày sinh:"), 0, 2);
        grid.add(dpNgaySinh, 1, 2);
        grid.add(new Label("Lương CB:"), 0, 3);
        grid.add(txtLuong, 1, 3);
        grid.add(new Label("Đơn vị:"), 0, 4);
        grid.add(cboDV, 1, 4);
        grid.add(new Label("Chức vụ:"), 0, 5);
        grid.add(cboCV, 1, 5);
        grid.add(new Label("Số giờ:"), 0, 6);
        grid.add(txtGio, 1, 6);
        grid.add(new Label("Tiền/giờ:"), 0, 7);
        grid.add(txtTien, 1, 7);

        // Label và Field này chỉ hiện khi chọn Nghiên cứu viên
        Label lblPC = new Label("Phụ cấp NC:");
        grid.add(lblPC, 0, 8);
        grid.add(txtPhuCapNC, 1, 8);
        lblPC.setVisible(false);
        txtPhuCapNC.setVisible(false);

        cboLoaiGV.valueProperty().addListener((obs, oldVal, newVal) -> {
            boolean isNghienCuuVien = "Nghiên cứu viên".equals(newVal);
            lblPC.setVisible(isNghienCuuVien);
            txtPhuCapNC.setVisible(isNghienCuuVien);
        });

        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                try {
                    String hoTen = txtTen.getText().trim();
                    if (hoTen.isEmpty()) {
                        new Alert(Alert.AlertType.ERROR, "Họ tên không được để trống!").show();
                        return null;
                    }
                    LocalDate ngaySinh = dpNgaySinh.getValue();
                    double luongCB = Double.parseDouble(txtLuong.getText());
                    DonVi selectedDonVi = cboDV.getValue();
                    ChucVu selectedChucVu = cboCV.getValue();

                    if (selectedDonVi == null || selectedChucVu == null) {
                        new Alert(Alert.AlertType.ERROR, "Vui lòng chọn Đơn vị và Chức vụ!").show();
                        return null;
                    }

                    int soGio = Integer.parseInt(txtGio.getText());
                    double tienGio = Double.parseDouble(txtTien.getText());

                    if (cboLoaiGV.getValue().equals("Nghiên cứu viên")) {
                        double phuCap = Double.parseDouble(txtPhuCapNC.getText());
                        NghienCuuVien ncv = new NghienCuuVien(hoTen, ngaySinh, "", soGio, tienGio, phuCap);
                        ncv.setLoaiNhanSu(LoaiNhanSu.NGHIEN_CUU_VIEN);
                        ncv.setLuongCoBan(luongCB);
                        ncv.setDonVi(selectedDonVi);
                        ncv.setChucVu(selectedChucVu);
                        return ncv;
                    } else {
                        GiangVienDay gvd = new GiangVienDay(hoTen, ngaySinh, "", soGio, tienGio);
                        gvd.setLoaiNhanSu(LoaiNhanSu.GIANG_VIEN_DAY);
                        gvd.setLuongCoBan(luongCB);
                        gvd.setDonVi(selectedDonVi);
                        gvd.setChucVu(selectedChucVu);
                        return gvd;
                    }
                } catch (Exception e) {
                    new Alert(Alert.AlertType.ERROR, "Dữ liệu nhập vào không hợp lệ!").show();
                }
            }
            return null;
        });

        dialog.showAndWait().ifPresent(ns -> {
            nhanSuDAO.insert(ns);
            reloadTable();
        });
    }

    @FXML
    public void themNhanVien() {
        DonVi dv = cboDonVi.getValue();
        if (dv == null) {
            new Alert(Alert.AlertType.WARNING, "Vui lòng chọn Đơn vị trước!").show();
            return;
        }

        Dialog<NhanSu> dialog = new Dialog<>();
        dialog.setTitle("Thêm Nhân Viên Hành Chính");
        dialog.setHeaderText("Thông tin nhân viên mới");

        // Thành phần giao diện
        TextField txtHoTen = new TextField();
        DatePicker dpNgaySinh = new DatePicker(LocalDate.now());
        TextField txtLuongCB = new TextField("0");
        TextField txtPhuCap = new TextField("0");

        ComboBox<LoaiNhanSu> cboLoai = new ComboBox<>(FXCollections.observableArrayList(
                LoaiNhanSu.CHUYEN_VIEN, LoaiNhanSu.KE_TOAN, LoaiNhanSu.THU_KY,
                LoaiNhanSu.IT_SUPPORT, LoaiNhanSu.KY_THUAT_VIEN,
                LoaiNhanSu.GIAM_DOC_TRUONG, LoaiNhanSu.PHO_GIAM_DOC_TRUONG));
        cboLoai.setValue(LoaiNhanSu.CHUYEN_VIEN);

        ComboBox<DonVi> cboDV = new ComboBox<>(cboDonVi.getItems());
        ComboBox<ChucVu> cboCV = new ComboBox<>(FXCollections.observableArrayList(chucVuDAO.getAll()));

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        grid.add(new Label("Chức vụ:"), 0, 0);
        grid.add(cboLoai, 1, 0);
        grid.add(new Label("Họ tên:"), 0, 1);
        grid.add(txtHoTen, 1, 1);
        grid.add(new Label("Ngày sinh:"), 0, 2);
        grid.add(dpNgaySinh, 1, 2);
        grid.add(new Label("Lương Cứng:"), 0, 3);
        grid.add(txtLuongCB, 1, 3);
        grid.add(new Label("Phụ cấp:"), 0, 4);
        grid.add(txtPhuCap, 1, 4);
        grid.add(new Label("Đơn vị:"), 0, 5);
        grid.add(cboDV, 1, 5);
        grid.add(new Label("Chức vụ:"), 0, 6);
        grid.add(cboCV, 1, 6);

        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(bt -> {
            if (bt == ButtonType.OK) {
                try {
                    String name = txtHoTen.getText().trim();
                    if (name.isEmpty()) {
                        new Alert(Alert.AlertType.ERROR, "Họ tên không được để trống!").show();
                        return null;
                    }
                    LocalDate birth = dpNgaySinh.getValue();
                    double luongCB = Double.parseDouble(txtLuongCB.getText());
                    double pc = Double.parseDouble(txtPhuCap.getText());
                    LoaiNhanSu selectedLoai = cboLoai.getValue();
                    DonVi selectedDonVi = cboDV.getValue();
                    ChucVu selectedChucVu = cboCV.getValue();

                    if (selectedDonVi == null || selectedChucVu == null) {
                        new Alert(Alert.AlertType.ERROR, "Vui lòng chọn Đơn vị và Chức vụ!").show();
                        return null;
                    }

                    // Dùng switch để khởi tạo đúng Class
                    NhanVien nv = switch (selectedLoai) {
                        case KE_TOAN -> new KeToan(name, birth, "", pc);
                        case IT_SUPPORT -> new ITSupport(name, birth, "", pc);
                        case THU_KY -> new ThuKy(name, birth, "", pc);
                        case GIAM_DOC_TRUONG -> new GiamDocTruong(name, birth, "", pc);
                        case PHO_GIAM_DOC_TRUONG -> new PhoGiamDocTruong(name, birth, "", pc);
                        default -> new ChuyenVien(name, birth, "", pc);
                    };
                    nv.setLoaiNhanSu(selectedLoai);
                    nv.setLuongCoBan(luongCB);
                    nv.setDonVi(selectedDonVi);
                    nv.setChucVu(selectedChucVu);
                    return nv;
                } catch (Exception e) {
                    new Alert(Alert.AlertType.ERROR, "Dữ liệu nhập vào không hợp lệ!").show();
                    return null;
                }
            }
            return null;
        });

        dialog.showAndWait().ifPresent(ns -> {
            nhanSuDAO.insert(ns);
            reloadTable();
        });
    }

    @FXML
    public void themPhuTro() {
        DonVi dv = cboDonVi.getValue();
        if (dv == null) {
            new Alert(Alert.AlertType.WARNING, "Vui lòng chọn Đơn vị trước!").show();
            return;
        }

        Dialog<NhanSu> dialog = new Dialog<>();
        dialog.setTitle("Thêm Nhân Viên Phụ Trợ");

        TextField txtHoTen = new TextField();
        DatePicker dpNgaySinh = new DatePicker(LocalDate.now());
        TextField txtLuongThang = new TextField("0");

        ComboBox<LoaiNhanSu> cboLoai = new ComboBox<>(FXCollections.observableArrayList(
                LoaiNhanSu.BAO_VE, LoaiNhanSu.TAP_VU, LoaiNhanSu.VE_SINH));
        cboLoai.setValue(LoaiNhanSu.BAO_VE);

        ComboBox<DonVi> cboDV = new ComboBox<>(cboDonVi.getItems());
        ComboBox<ChucVu> cboCV = new ComboBox<>(FXCollections.observableArrayList(chucVuDAO.getAll()));

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        grid.add(new Label("Công việc:"), 0, 0);
        grid.add(cboLoai, 1, 0);
        grid.add(new Label("Họ tên:"), 0, 1);
        grid.add(txtHoTen, 1, 1);
        grid.add(new Label("Ngày sinh:"), 0, 2);
        grid.add(dpNgaySinh, 1, 2);
        grid.add(new Label("Lương tháng:"), 0, 3);
        grid.add(txtLuongThang, 1, 3);
        grid.add(new Label("Đơn vị:"), 0, 4);
        grid.add(cboDV, 1, 4);
        grid.add(new Label("Chức vụ:"), 0, 5);
        grid.add(cboCV, 1, 5);

        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(bt -> {
            if (bt == ButtonType.OK) {
                try {
                    String name = txtHoTen.getText().trim();
                    if (name.isEmpty()) {
                        new Alert(Alert.AlertType.ERROR, "Họ tên không được để trống!").show();
                        return null;
                    }
                    LocalDate birth = dpNgaySinh.getValue();
                    double luong = Double.parseDouble(txtLuongThang.getText());
                    LoaiNhanSu selectedLoai = cboLoai.getValue();
                    DonVi selectedDonVi = cboDV.getValue();
                    ChucVu selectedChucVu = cboCV.getValue();

                    if (selectedDonVi == null || selectedChucVu == null) {
                        new Alert(Alert.AlertType.ERROR, "Vui lòng chọn Đơn vị và Chức vụ!").show();
                        return null;
                    }

                    PhuTro pt = switch (selectedLoai) {
                        case TAP_VU -> new TapVu(name, birth, "", luong);
                        case VE_SINH -> new VeSinh(name, birth, "", luong);
                        default -> new BaoVe(name, birth, "", luong);
                    };
                    pt.setLoaiNhanSu(selectedLoai);
                    pt.setDonVi(selectedDonVi);
                    pt.setChucVu(selectedChucVu);
                    pt.setLuongCoBan(luong); // Ensure salary is set correctly
                    return pt;
                } catch (Exception e) {
                    new Alert(Alert.AlertType.ERROR, "Dữ liệu nhập vào không hợp lệ!").show();
                    return null;
                }
            }
            return null;
        });

        dialog.showAndWait().ifPresent(ns -> {
            nhanSuDAO.insert(ns);
            reloadTable();
        });
    }

    @FXML
    public void themDonVi() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Thêm Đơn Vị");
        dialog.setHeaderText("Nhập thông tin đơn vị mới");

        TextField txtMa = new TextField();
        TextField txtTen = new TextField();
        ComboBox<DonVi.LoaiDonVi> cboLoai = new ComboBox<>(FXCollections.observableArrayList(DonVi.LoaiDonVi.values()));
        cboLoai.setValue(DonVi.LoaiDonVi.KHOA);

        ComboBox<DonVi> cboParent = new ComboBox<>(cboDonVi.getItems());
        cboParent.setPromptText("-- Chọn Đơn vị cha (nếu có) --");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        grid.add(new Label("Mã Đơn vị:"), 0, 0);
        grid.add(txtMa, 1, 0);
        grid.add(new Label("Tên Đơn vị:"), 0, 1);
        grid.add(txtTen, 1, 1);
        grid.add(new Label("Loại:"), 0, 2);
        grid.add(cboLoai, 1, 2);
        grid.add(new Label("Đơn vị cha:"), 0, 3);
        grid.add(cboParent, 1, 3);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.showAndWait().ifPresent(bt -> {
            if (bt == ButtonType.OK) {
                String ma = txtMa.getText().trim();
                String ten = txtTen.getText().trim();
                DonVi.LoaiDonVi loai = cboLoai.getValue();
                DonVi parent = cboParent.getValue();
                Integer parentId = (parent != null) ? parent.getId() : null;

                if (ma.isEmpty() || ten.isEmpty()) {
                    new Alert(Alert.AlertType.ERROR, "Mã và Tên không được để trống!").show();
                    return;
                }

                boolean ok = donViDAO.insert(ma, ten, loai, parentId);
                if (ok) {
                    new Alert(Alert.AlertType.INFORMATION, "Thành công!").show();
                    loadDonVi(); // Refresh ComboBox
                } else {
                    new Alert(Alert.AlertType.ERROR, "Lỗi khi lưu vào CSDL (có thể Mã đã tồn tại).").show();
                }
            }
        });
    }

    @FXML
    public void locTheoDonVi() {
        reloadTable();
    }

    // ================= RELOAD =================
    private void reloadTable() {
        try {
            DonVi dv = cboDonVi.getValue();
            if (dv == null) {
                tblMain.getItems().clear();
                return;
            }

            List<NhanSu> ds = nhanSuDAO.getByDonVi(dv.getId());

            if (ds == null) {
                System.out.println("Cảnh báo: DAO trả về danh sách NULL");
                return;
            }

            ds = filterByTab(ds);
            tblMain.setItems(FXCollections.observableArrayList(ds));
            tblMain.refresh();

        } catch (Exception e) {
            System.err.println("Lỗi khi tải dữ liệu: " + e.getMessage());
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Không thể tải dữ liệu: " + e.toString()).show();
        }
    }

    private List<NhanSu> filterByTab(List<NhanSu> ds) {
        Tab tab = tabPane.getSelectionModel().getSelectedItem();
        if (tab == null)
            return ds;

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

    @FXML
    private void handleLogout() {
        SessionManager.logout();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/vn/phenikaa/ui/LoginView.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) rootPane.getScene().getWindow();
            Scene scene = new Scene(root, 500, 400);
            scene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());
            stage.setScene(scene);
            stage.centerOnScreen();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ================= NGƯỜI DÙNG =================
    @FXML
    public void themNguoiDung() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Thêm Người Dùng");

        TextField txtUser = new TextField();
        PasswordField txtPass = new PasswordField();
        ComboBox<vn.phenikaa.person.VaiTro> cboVaiTro = new ComboBox<>(
                FXCollections.observableArrayList(vn.phenikaa.person.VaiTro.values()));
        cboVaiTro.setValue(vn.phenikaa.person.VaiTro.STAFF);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.add(new Label("Tên đăng nhập:"), 0, 0);
        grid.add(txtUser, 1, 0);
        grid.add(new Label("Mật khẩu:"), 0, 1);
        grid.add(txtPass, 1, 1);
        grid.add(new Label("Vai trò:"), 0, 2);
        grid.add(cboVaiTro, 1, 2);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.showAndWait().ifPresent(bt -> {
            if (bt == ButtonType.OK) {
                String u = txtUser.getText().trim();
                String p = txtPass.getText().trim();
                if (!u.isEmpty() && !p.isEmpty()) {
                    boolean ok = nguoiDungDAO.insert(u, p, cboVaiTro.getValue());
                    if (ok) {
                        new Alert(Alert.AlertType.INFORMATION, "Thành công!").show();
                    } else {
                        new Alert(Alert.AlertType.ERROR, "Tên đăng nhập đã tồn tại!").show();
                    }
                }
            }
        });
    }

    @FXML
    public void doiMatKhau() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Đổi mật khẩu");

        PasswordField txtOld = new PasswordField();
        PasswordField txtNew = new PasswordField();
        PasswordField txtConfirm = new PasswordField();

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.add(new Label("Mật khẩu cũ:"), 0, 0);
        grid.add(txtOld, 1, 0);
        grid.add(new Label("Mật khẩu mới:"), 0, 1);
        grid.add(txtNew, 1, 1);
        grid.add(new Label("Xác nhận:"), 0, 2);
        grid.add(txtConfirm, 1, 2);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.showAndWait().ifPresent(bt -> {
            if (bt == ButtonType.OK) {
                String oldP = txtOld.getText().trim();
                String newP = txtNew.getText().trim();
                String confirm = txtConfirm.getText().trim();

                if (newP.isEmpty() || !newP.equals(confirm)) {
                    new Alert(Alert.AlertType.ERROR, "Mật khẩu mới không khớp!").show();
                    return;
                }

                vn.phenikaa.person.NguoiDung cur = SessionManager.getCurrentUser();
                boolean ok = nguoiDungDAO.doiMatKhau(cur.getId(), oldP, newP);
                if (ok) {
                    new Alert(Alert.AlertType.INFORMATION, "Đổi mật khẩu thành công!").show();
                } else {
                    new Alert(Alert.AlertType.ERROR, "Mật khẩu cũ không chính xác!").show();
                }
            }
        });
    }
}
