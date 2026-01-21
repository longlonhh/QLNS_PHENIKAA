package vn.phenikaa.controller;

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
import javafx.scene.control.DatePicker;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import vn.phenikaa.database.NhanSuDAO;
import vn.phenikaa.person.GiangVien;
import vn.phenikaa.person.NhanSu;
import vn.phenikaa.person.NhanVien;



public class MainController {

    @FXML private StackPane rootPane;
    @FXML private TableView<NhanSu> table;
    @FXML private TableColumn<NhanSu, String> colMa;
    @FXML private TableColumn<NhanSu, String> colTen;
    @FXML private TableColumn<NhanSu, String> colLoai;
    @FXML private TableColumn<NhanSu, Double> colLuong;
    @FXML private TableColumn<NhanSu, Void> colAction;
    @FXML private TextField txtSearch;

    private final NhanSuDAO dao = new NhanSuDAO();
    private NhanSu lastDeleted;

    @FXML
    public void initialize() {

        colMa.setCellValueFactory(d ->
            new SimpleStringProperty(d.getValue().getMaNV())
        );

        colTen.setCellValueFactory(d ->
            new SimpleStringProperty(d.getValue().getHoTen())
        );

        colLoai.setCellValueFactory(d ->
            new SimpleStringProperty(
                d.getValue().getClass().getSimpleName()
            )
        );

        colLuong.setCellValueFactory(d ->
            new SimpleObjectProperty<>(d.getValue().tinhLuong())
        );

        addActionButtons();
        loadData();
    }

    @FXML
    public void loadData() {
        table.setItems(
            FXCollections.observableArrayList(dao.getAll())
        );
    }

    @FXML
    public void search() {
        table.setItems(
            FXCollections.observableArrayList(
                dao.search(txtSearch.getText().trim())
            )
        );
    }

    private void addActionButtons() {

        colAction.setCellFactory(col -> new TableCell<>() {

            private final Button btnSua = new Button("✏");
            private final Button btnXoa = new Button("❌");

            {
                btnSua.setOnAction(e -> {
                    NhanSu ns = getTableRow().getItem();
                    if (ns != null) suaNhanSu(ns);
                });

                btnXoa.setOnAction(e -> {
                    NhanSu ns = getTableRow().getItem();
                    if (ns == null) return;

                    Alert cf = new Alert(Alert.AlertType.CONFIRMATION);
                    cf.setTitle("Xác nhận");
                    cf.setHeaderText(null);
                    cf.setContentText("Xóa nhân sự " + ns.getMaNV() + "?");

                    if (cf.showAndWait().orElse(ButtonType.CANCEL)
                            != ButtonType.OK) return;

                    if (dao.delete(ns.getMaNV())) {
                        lastDeleted = ns;
                        table.getItems().remove(ns);
                        showUndoToast();
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : new HBox(5, btnSua, btnXoa));
            }
        });
    }

    private void showUndoToast() {

    Label msg = new Label("✔ Đã xóa");
    Button undo = new Button("UNDO");

    Region spacer = new Region();
    HBox.setHgrow(spacer, Priority.ALWAYS);

    HBox toast = new HBox(10, msg, spacer, undo);   // 🔧 CHỈ SỬA DÒNG NÀY
    toast.setAlignment(Pos.CENTER_LEFT);        
    toast.setPadding(new Insets(10, 16, 10, 16));
    toast.setMaxWidth(300);   // ❗ KHÔNG CHO GIÃN NGANG
    toast.setMaxHeight(40);
    toast.setPickOnBounds(false);               // ❗ KHÔNG BẮT CHUỘT NGOÀI VÙNG
    toast.setMouseTransparent(false);           // chỉ bắt chuột trong toast

    toast.setStyle("""
        -fx-background-color: #323232;
        -fx-background-radius: 20;
    """);

    msg.setStyle("-fx-text-fill: white;");
    undo.setStyle("""
        -fx-text-fill: orange;
        -fx-background-color: transparent;
    """);

    StackPane.setAlignment(toast, javafx.geometry.Pos.BOTTOM_CENTER);
    StackPane.setMargin(toast, new Insets(0, 0, 40, 0));

    rootPane.getChildren().add(toast);

    // UNDO
    undo.setOnAction(e -> {
        dao.insert(lastDeleted);
        loadData();
        rootPane.getChildren().remove(toast);
    });

    // TỰ ẨN
    PauseTransition pt = new PauseTransition(Duration.seconds(3));
    pt.setOnFinished(e -> rootPane.getChildren().remove(toast));
    pt.play();
}


    private void suaNhanSu(NhanSu ns) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Sửa thông tin: " + ns.getMaNV());

        TextField txtTen = new TextField(ns.getHoTen());
        DatePicker dpNgay = new DatePicker(ns.getNgaySinh());
        TextField txtLuong =
            new TextField(String.valueOf(ns.getLuongCoBan()));

        VBox box = new VBox(10,
            new Label("Họ tên"), txtTen,
            new Label("Ngày sinh"), dpNgay,
            new Label("Lương cơ bản"), txtLuong
        );

        if (ns instanceof GiangVien gv) {
            TextField soGio =
                new TextField(String.valueOf(gv.getSoGioGiang()));
            soGio.setId("soGio");

            TextField tien =
                new TextField(String.valueOf(gv.getTienMoiGio()));
            tien.setId("tien");

            box.getChildren().addAll(
                new Label("Số giờ giảng"), soGio,
                new Label("Tiền mỗi giờ"), tien
            );
        }

        if (ns instanceof NhanVien nv) {
            TextField phuCap =
                new TextField(String.valueOf(nv.getPhuCap()));
            phuCap.setId("phuCap");

            box.getChildren().addAll(
                new Label("Phụ cấp"), phuCap
            );
        }

        dialog.getDialogPane().setContent(box);
        dialog.getDialogPane()
              .getButtonTypes()
              .addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.showAndWait().ifPresent(btn -> {

            if (btn != ButtonType.OK) return;

            try {
                ns.setHoTen(txtTen.getText());
                ns.setNgaySinh(dpNgay.getValue());
                ns.setLuongCoBan(
                    Double.parseDouble(txtLuong.getText())
                );

                if (ns instanceof GiangVien gv) {
                    gv.setSoGioGiang(
                        Integer.parseInt(
                            ((TextField) box.lookup("#soGio")).getText()
                        )
                    );
                    gv.setTienMoiGio(
                        Double.parseDouble(
                            ((TextField) box.lookup("#tien")).getText()
                        )
                    );
                }

                if (ns instanceof NhanVien nv) {
                    nv.setPhuCap(
                        Double.parseDouble(
                            ((TextField) box.lookup("#phuCap")).getText()
                        )
                    );
                }

                dao.update(ns);
                loadData();

            } catch (Exception ex) {
                new Alert(
                    Alert.AlertType.ERROR,
                    "Vui lòng nhập đúng định dạng số!"
                ).show();
            }
        });
    }

    @FXML
    public void themGiangVien() {
        dao.insert(new GiangVien(
            "Giảng viên mới",
            java.time.LocalDate.of(1990, 1, 1),
            "", "",
            0,
            0.0
        ));
        loadData();
    }

    @FXML
    public void themNhanVien() {
        dao.insert(new NhanVien(
            "Nhân viên mới",
            java.time.LocalDate.of(1995, 1, 1),
            "", "",
            0.0
        ));
        loadData();
    }
}
