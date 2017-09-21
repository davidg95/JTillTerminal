/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.davidg95.jtill.javafxjtill;

import io.github.davidg95.JTill.jtill.ProductEvent;
import io.github.davidg95.JTill.jtill.*;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.sql.Time;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import java.awt.print.PrinterAbortException;
import java.awt.print.PrinterException;
import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.geometry.Insets;
import javafx.scene.control.TableRow;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;
import javafx.stage.Window;
import javax.swing.Timer;

/**
 *
 * @author David
 */
public class MainStage extends Stage implements GUIInterface {

    private static final Logger LOG = Logger.getGlobal();

    public static final int CODE = 1;
    public static final int BUTTONS = 2;

    private int type;

    private Screen def_screen;
    private Screen last_screen;
    private Screen currentScreen;

    private Staff staff;
    private Till till;
    private Sale sale;
    private Sale lastSale;
    private int age;
    private int itemQuantity;
    private BigDecimal amountDue;
    private final ServerConnection dc;
    private int MAX_SALES;
    private String symbol;

    public static boolean printOk;
    private boolean refundMode;

    private final String stylesheet;

    private List<Screen> screens;

    private Scene mainScene;
    private BorderPane parentPane;

    private Timer timer;
    private volatile int logoutTimeout;

    //Login Scene Components
    private GridPane loginPane;
    private Button exit;
    private Button lock;
    private Button login;
    private Button print;
    private Label loginMessage;
    private GridPane staffLayout;
    private int x = 0;
    private int y = 0;
    private TextField loginNumber;
    private GridPane loginArea;

    //Main Scene Components
    private TableView itemsTable;
    private ObservableList<SaleItem> obTable;
    private GridPane mainPane;
    private StackPane buttonPane;
    private List<GridPane> buttonPanes;
    private Label staffLabel;
    private Label time;
    private Label total;
    private Label totalItems;
    private Label screenLabel;
    private Button logoff;
    private Button quantity;
    private Button voidSelected;
    private TextField barcode;
    private Button payment;
    private Button lookup;
    private Button halfPrice;
    private Button assisstance;
    private Label mainVersion;
    private Label alertMessage;
    private Label mainRefund;
    private Button voidLast;

    //Payment Scene Components
    private GridPane paymentPane;

    private TableView paymentItemsTable;
    private ObservableList<SaleItem> payObTable;

    private Button fivePounds;
    private Button tenPounds;
    private Button twentyPounds;
    private Button customValue;

    private Button exactValue;
    private Button card;
    private Button cheque;
    private Button voidItem;

    private Button addCustomer;
    private Button chargeAccount;
    private Button loyaltyButton;
    private Button coupon;

    private Button voidSale;
    private Button refundButton;
    private Button cashUp;
    private Button saveTransaction;

    private ListView<PaymentItem> paymentsList;
    private ObservableList<PaymentItem> obPayments;
    private Label saleCustomer;
    private Label paymentTotal;

    private Button settingsButton;
    
    private Label paymentMessages;

    private Button back;
    private Button paymentLogoff;
    private Button clockOff;

    private GridPane lockPane;

    private final double SCREEN_WIDTH = javafx.stage.Screen.getPrimary().getBounds().getWidth();
    private final double SCREEN_HEIGHT = javafx.stage.Screen.getPrimary().getBounds().getHeight();

    private String terminalName;

    private final int topfont = 10;

    private List<Staff> staffCache;

    private List<Sale> saleCache;

    private boolean newData = false;
    
    private String lastScreen;

    public MainStage(ServerConnection dc) {
        super();
        this.dc = dc;
        if (staff == null) {
            this.sale = new Sale(0, 0);
        } else {
            this.sale = new Sale(0, staff.getId());
        }
        setTitle("JTill Terminal");
        stylesheet = MainStage.class.getResource("style.css").toExternalForm();
        saleCache = new LinkedList<>();
    }

    private void setPanel(Pane panel) {
        parentPane.setCenter(panel);
        panel.requestLayout();
        parentPane.requestLayout();
    }

    public void initalise() {
        
        parentPane = new BorderPane();
        
        staffLabel = new Label("Not Logged In");
        staffLabel.setId("toplabel");
        staffLabel.setFont(Font.font("Tahoma", FontWeight.NORMAL, topfont));

        screenLabel = new Label("Login");
        screenLabel.setId("toplabel");
        screenLabel.setFont(Font.font("Tahoma", FontWeight.NORMAL, topfont));

        mainVersion = new Label("JTill Terminal");
        mainVersion.setId("toplabel");
        mainVersion.setFont(Font.font("Tahoma", FontWeight.NORMAL, topfont));

        mainRefund = new Label();
        mainRefund.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
        mainRefund.setMinSize(0, 0);
        mainRefund.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

        time = new Label("--:-- --/--/----");
        time.setId("timeLabel");
        time.setFont(Font.font("Tahoma", FontWeight.NORMAL, topfont));
        ClockThread.addClockLabel(time);
        ClockThread.setFormat(ClockThread.DATE_TIME_FORMAT);
        time.setTextAlignment(TextAlignment.RIGHT);
        
        GridPane topPane = new GridPane();
        
        for (int i = 1; i <= 10; i++) {
            ColumnConstraints col = new ColumnConstraints();
            col.setPercentWidth(10);
            col.setFillWidth(true);
            col.setHgrow(Priority.ALWAYS);
            topPane.getColumnConstraints().add(col);
        }
        
        topPane.add(screenLabel, 0, 0, 2, 1);
        topPane.add(staffLabel, 2, 0, 2, 1);
        topPane.add(mainVersion, 4, 0, 3, 1);
        topPane.add(time, 9, 0, 1, 1);
        topPane.add(mainRefund, 7, 0);
        
        topPane.setOpacity(100);
        
        parentPane.setTop(topPane);
        
        init();
        initPayment();
        initLogin();
        initLock();
        mainScene = new Scene(parentPane, SCREEN_WIDTH, SCREEN_HEIGHT);
        mainScene.getStylesheets().add(stylesheet);
        setPanel(loginPane);
        setScene(mainScene); //Show the login scene first
        if (!this.isShowing()) {
            initStyle(StageStyle.UNDECORATED);
        }
//        setFullScreen(true);
        setResizable(false);
        setMaximized(true);
        show();
        MessageScreen.changeMessage("Initialising");
        MessageScreen.showWindow();
        Platform.runLater(() -> {
            boolean tryCon = true;
            while (tryCon) {
                try {
                    tryConnect();
                    tryCon = false;
                } catch (IOException ex) {
                    MessageScreen.hideWindow();
                    LOG.log(Level.WARNING, "Error connecting to the server");
                    if (YesNoDialog.showDialog(this, "Could not connect to server", "A connection could not be established to the server, do you want to try again?") == YesNoDialog.NO) {
                        tryCon = false;
                    } else {
                        MessageScreen.changeMessage("Initialising");
                        MessageScreen.showWindow();
                    }
                }
            }
        });
        mainPane.addEventHandler(EventType.ROOT, new EventHandler() {
            @Override
            public void handle(Event event) {
                if (timer != null) {
                    timer.restart();
                }
            }
        });
    }

    @Override
    public void requestUpdate() {
        try {
            this.logoff();
            Platform.runLater(() -> {
                MessageScreen.changeMessage("Downloading Update");
                MessageScreen.showWindow();
            });
            List<byte[]> update = dc.downloadTerminalUpdate();
            File file = new File(System.getProperty("java.io.tmpdir") + "terminalinstaller.exe");
            FileOutputStream out = new FileOutputStream(file);
            for (byte[] b : update) {
                out.write(b);
            }
            out.close();
            Runtime.getRuntime().exec(System.getProperty("java.io.tmpdir") + "terminalinstaller.exe", null, new File(System.getProperty("java.io.tmpdir")));
            Platform.runLater(() -> {
                MessageScreen.hideWindow();
            });
        } catch (Exception ex) {
            Platform.runLater(() -> {
                MessageScreen.hideWindow();
                MessageDialog.showMessage(this, "Error", ex.getMessage());
            });
            Logger.getLogger(MainStage.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            Platform.runLater(() -> {
                MessageScreen.hideWindow();
            });
        }
    }

    @Override
    public void markNewData() {
        newData = true;
        if (staff == null) {
            initTill();
        } else {
            Platform.runLater(() -> {
                final String temp = terminalName + " (New Data)";
                MainStage.this.mainVersion.setText(temp);
            });
        }
    }

    public class TimerHandler implements ActionListener {

        @Override
        public void actionPerformed(java.awt.event.ActionEvent e) {
            MainStage.this.logout();
        }

    }

    private void tryConnect() throws IOException {
        JavaFXJTill.loadProperties();
        dc.setGUI(MainStage.this);
        if (dc instanceof ServerConnection) {
            ServerConnection sc = (ServerConnection) dc;
            MessageScreen.changeMessage("Connecting to server");
            LOG.log(Level.INFO, "Attempting connection to the server on IP address " + JavaFXJTill.SERVER);
            till = sc.connect(JavaFXJTill.SERVER, JavaFXJTill.PORT, JavaFXJTill.NAME, JavaFXJTill.uuid);
            JavaFXJTill.uuid = till.getUuid();
            JavaFXJTill.saveProperties();
            if (staff == null) {
                this.sale = new Sale(till.getId(), 0);
            } else {
                this.sale = new Sale(till.getId(), staff.getId());
            }
        }
    }

    private void getServerData() {
        try {
            Platform.runLater(() -> {
                MessageScreen.changeMessage("Getting configuration");
            });
            JavaFXJTill.settings = null;
            JavaFXJTill.settings = dc.getSettings();
            MAX_SALES = Integer.parseInt(JavaFXJTill.settings.getProperty("MAX_CACHE_SALES"));
            logoutTimeout = Integer.parseInt(JavaFXJTill.settings.getProperty("LOGOUT_TIMEOUT"));
            if (!JavaFXJTill.settings.getProperty("UNLOCK_CODE", "OFF").equals("OFF")) {
                Platform.runLater(() -> {
                    loginPane.add(lock, 1, 14, 1, 2);
                });
            } else{
                Platform.runLater(()->{
                    loginPane.getChildren().remove(lock);
                });
            }
            LOG.log(Level.INFO, "Max sales set to {0}", MAX_SALES);
            try {
                if (JavaFXJTill.settings.getProperty("SEND_PRODUCTS_START").equals("TRUE")) {
                    LOG.log(Level.INFO, "Downloading products list from server");
                    ProductCache.getInstance().setProducts(dc.getAllProducts());
                    LOG.log(Level.INFO, "Downloaded " + ProductCache.getInstance().getAllProducts().size() + " products from the server");
                    LOG.log(Level.INFO, "Downloading plu list from server");
                    ProductCache.getInstance().setPlus(dc.getAllPlus());
                    LOG.log(Level.INFO, "Downloaded " + ProductCache.getInstance().getAllProducts().size() + " plus from the server");
                }
            } catch (SQLException ex) {
                LOG.log(Level.SEVERE, null, ex);
            }
            try {
                till = dc.getTill(till.getId());
            } catch (JTillException ex) {
                Logger.getLogger(MainStage.class.getName()).log(Level.SEVERE, null, ex);
            }
            try {
                int color = Integer.parseInt(dc.getSetting("TERMINAL_BG"));
                if (color != 0) {
                    java.awt.Color col = new java.awt.Color(color);
                    Color c = Color.color((double) col.getRed() / 255, (double) col.getGreen() / 255, (double) col.getBlue() / 255);
                    parentPane.setBackground(new Background(new BackgroundFill(c, CornerRadii.EMPTY, Insets.EMPTY)));
                } else {
                    parentPane.setBackground(null);
                }
            } catch (IOException ex) {
                Logger.getLogger(MainStage.class.getName()).log(Level.SEVERE, null, ex);
            }
            symbol = JavaFXJTill.settings.getProperty("CURRENCY_SYMBOL");
            terminalName = till.getName() + " - " + JavaFXJTill.settings.getProperty("SITE_NAME");
            Platform.runLater(() -> {
                mainVersion.setText(terminalName);
                ((TableColumn) itemsTable.getColumns().get(2)).setText(symbol);
                ((TableColumn) paymentItemsTable.getColumns().get(2)).setText(symbol);
                twentyPounds.setText(symbol + "20");
                tenPounds.setText(symbol + "10");
                fivePounds.setText(symbol + "5");
            });
            List<Discount> discounts = dc.getValidDiscounts();
            for (Discount d : discounts) {
                try {
                    List<DiscountBucket> buckets = dc.getDiscountBuckets(d.getId());
                    for (DiscountBucket b : buckets) {
                        b.setTriggers(dc.getBucketTriggers(b.getId()));
                    }
                    d.setBuckets(buckets);
                } catch (DiscountNotFoundException | JTillException ex) {
                    Logger.getLogger(MainStage.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            DiscountCache.getInstance().setDiscounts(discounts);
            LOG.log(Level.INFO, "Loading screen and button configurations from the server");
            screens = dc.getAllScreens(); //Get all the screens from the server.
            buttonPane.getChildren().clear();
            if (!buttonPanes.isEmpty()) {
                buttonPane.getChildren().clear();
                buttonPane.getChildren().add(buttonPanes.get(0));
            }
            addScreens(screens); //Add the screens to the main interface.
            if (JavaFXJTill.settings.get("LOGINTYPE").equals("CODE")) {
                setLoginType(CODE);
            } else {
                setLoginType(BUTTONS);
            }
            try {
                BackgroundImage myBi = new BackgroundImage(new Image(dc.getLoginBackground().toURI().toString()), BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, BackgroundSize.DEFAULT);
                loginPane.setBackground(new Background(myBi));
            } catch (IOException e) {
                e.printStackTrace();
            }
            staffCache = dc.getAllStaff();
        } catch (IOException | SQLException ex) {
            Platform.runLater(() -> {
                MessageScreen.hideWindow();
                MessageDialog.showMessage(this, "Error", ex.getMessage());
            });
        }
    }

    private void init() {
        mainPane = new GridPane();

        buttonPanes = new ArrayList<>();
        buttonPane = new StackPane();
        buttonPane.setId("productsgrid");
        buttonPane.getChildren().clear();
        if (!buttonPanes.isEmpty()) {
            buttonPane.getChildren().clear();
            buttonPane.getChildren().add(buttonPanes.get(0));
        }

        itemsTable = new TableView();
        itemsTable.setId("ITEMS");
        itemsTable.setEditable(false);
        TableColumn qty = new TableColumn("Qty.");
        TableColumn itm = new TableColumn("Item");
        TableColumn cst = new TableColumn(symbol);
        qty.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        itm.setCellValueFactory(new PropertyValueFactory<>("name"));
        cst.setCellValueFactory(new PropertyValueFactory<>("totalPrice"));
        qty.prefWidthProperty().bind(itemsTable.widthProperty().divide(7));
        itm.prefWidthProperty().bind(itemsTable.widthProperty().divide(7).multiply(4));
        cst.prefWidthProperty().bind(itemsTable.widthProperty().divide(7).multiply(2));
        itemsTable.getColumns().addAll(qty, itm, cst);
        obTable = FXCollections.observableArrayList();
        itemsTable.setItems(obTable);

        itemsTable.setRowFactory(tv -> {
            TableRow<SaleItem> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    SaleItem rowData = row.getItem();
                    int q = NumberEntry.showNumberEntryDialog(this, "Set quantity", rowData.getQuantity());
                    rowData.setQuantity(q);
                    setTotalLabel();
                    itemsTable.refresh();
                    sale.updateTotal();
                    setTotalLabel();
                }
            });
            return row;
        });
        updateList();

        total = new Label("Total: " + symbol + "0.00");
        total.setId("total");
        total.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
        total.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

        totalItems = new Label("Items: 0");
        totalItems.setId("total");
        total.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
        total.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

        quantity = new Button("Quantity: 1");
        quantity.setId("red");
        quantity.setMinSize(0, 0);
        quantity.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        itemQuantity = 1;
        quantity.setOnAction((ActionEvent event) -> {
            if (barcode.getText().equals("")) {
                int val = NumberEntry.showNumberEntryDialog(this, "Enter Quantity", itemQuantity);
                if (val > 0) {
                    itemQuantity = val;
                }
            } else {
                if (Utilities.isNumber(barcode.getText())) {
                    int val = Integer.parseInt(barcode.getText());
                    if (val > 0) {
                        itemQuantity = Integer.parseInt(barcode.getText());
                    } else {
                        MessageDialog.showMessage(this, "Quantity", "Quantity must be greater than zero");
                    }
                } else {
                    MessageDialog.showMessage(this, "Quantity", "A number must be entered");
                }
                barcode.setText("");
            }
            quantity.setText("Quantity: " + itemQuantity);
            if (!barcode.isFocused()) {
                barcode.requestFocus();
            }
        });

        voidSelected = new Button("Void Selected");
        voidSelected.setId("red");
        voidSelected.setMinSize(0, 0);
        voidSelected.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        voidSelected.setOnAction((ActionEvent event) -> {
            if (itemsTable.getSelectionModel().getSelectedIndex() > -1) {
                final SaleItem item = (SaleItem) itemsTable.getSelectionModel().getSelectedItem();
                sale.voidItem(item);
                obTable.remove(item);
                payObTable.remove(item);
                final Product p = (Product) item.getItem();
                double taxP = p.getTax().getValue() / 100;
                setTotalLabel();
            }
            if (!barcode.isFocused()) {
                barcode.requestFocus();
            }
        });

        barcode = new TextField();
        barcode.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        barcode.setFont(Font.font("Tahoma", FontWeight.NORMAL, 25));
        barcode.setOnAction((ActionEvent event) -> {
            Platform.runLater(() -> {
                if (!barcode.getText().equals("")) {
                    if (Utilities.isNumber(barcode.getText())) {
                        getProductByBarcode(barcode.getText());
                    }
                    barcode.setText("");
                }
            });
        });

        GridPane numbers = createNumbersPane();

        payment = new Button("Payment");
        payment.setId("bottom");
        payment.setMinSize(0, 0);
        payment.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        payment.setOnAction((ActionEvent event) -> {
            setPanel(paymentPane);
            screenLabel.setText("Payment");
        });

        logoff = new Button("Logoff");
        logoff.setId("cRed");
        logoff.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        logoff.setMinSize(0, 0);
        logoff.setOnAction((ActionEvent event) -> {
            new Thread() {
                @Override
                public void run() {
                    try {
                        logoff();
                    } finally {
                        Platform.runLater(() -> {
                            MessageScreen.hideWindow();
                        });
                    }
                }
            }.start();
        });
        logoff.setStyle("-fx-base: #0000FF;");

        lookup = new Button("Lookup");
        lookup.setId("bottom");
        lookup.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        lookup.setMinSize(0, 0);
        lookup.setOnAction((ActionEvent event) -> {
            Product p = ProductSelectDialog.showDialog(this, dc);
            if (p != null) {
                addItemToSale(p);
            }
        });

        halfPrice = new Button("Half Price");
        halfPrice.setId("bottom");
        halfPrice.setMinSize(0, 0);
        halfPrice.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        halfPrice.setOnAction((ActionEvent event) -> {
            if (itemsTable.getSelectionModel().getSelectedIndex() > -1) {
                SaleItem item = (SaleItem) itemsTable.getSelectionModel().getSelectedItem();
                if (!(item.getType() == SaleItem.DISCOUNT)) {
                    sale.halfPriceItem(item);
                    setTotalLabel();
                    itemsTable.refresh();
                } else {
                    showMessageAlert("Item not discountable", 2000);
                }
            }
            if (!barcode.isFocused()) {
                barcode.requestFocus();
            }
        });

        assisstance = new Button("Assisstance");
        assisstance.setId("bottom");
        assisstance.setMinSize(0, 0);
        assisstance.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        assisstance.setOnAction((ActionEvent event) -> {
            String message = Keyboard.show(this, "Enter message to send");
            if (message == null || message.length() == 0) {
                showMessageAlert("Message Cancelled", 2000);
                return;
            }
            try {
                dc.assisstance(message);
                LOG.log(Level.INFO, "Assisstance message sent to server");
                showMessageAlert("Message Sent", 2000);
            } catch (IOException ex) {
                MessageDialog.showMessage(this, "Assisstance", "No contact with server");
            }
            if (!barcode.isFocused()) {
                barcode.requestFocus();
            }
        });

        alertMessage = new Label();
        alertMessage.setFont(Font.font("Tahoma", FontWeight.NORMAL, 40));
        alertMessage.setId("message");
        alertMessage.setMinSize(0, 0);
        alertMessage.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

        voidLast = new Button("Void Last");
        voidLast.setId("red");
        voidLast.setMinSize(0, 0);
        voidLast.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        voidLast.setOnAction((ActionEvent event) -> {
            SaleItem last = sale.getLastAdded();
            for (SaleItem i : sale.getSaleItems()) {
                if (i.getItem() == last.getItem()) {
                    if (i.getQuantity() == last.getQuantity()) {
                        obTable.remove(last);
                    } else {
                        i.decreaseQuantity(last.getQuantity());
                    }
                }
            }
            sale.voidLastItem();
            setTotalLabel();
            if (!barcode.isFocused()) {
                barcode.requestFocus();
            }
        });

        GridPane pane = new GridPane();
        pane.add(total, 0, 0);
        pane.add(totalItems, 1, 0);
        pane.add(quantity, 0, 1);
        pane.add(voidSelected, 1, 1);

        for (int i = 1; i <= 2; i++) {
            ColumnConstraints col = new ColumnConstraints();
            col.setPercentWidth(50);
            col.setFillWidth(true);
            col.setHgrow(Priority.ALWAYS);
            pane.getColumnConstraints().add(col);
        }

        for (int i = 1; i <= 2; i++) {
            RowConstraints row = new RowConstraints();
            row.setPercentHeight(50);
            row.setFillHeight(true);
            row.setVgrow(Priority.ALWAYS);
            pane.getRowConstraints().add(row);
        }

//        mainPane.add(staffLabel, 2, 0, 2, 1);
//        mainPane.add(mainVersion, 4, 0, 3, 1);
//        mainPane.add(time, 9, 0, 1, 1);
        mainPane.add(buttonPane, 0, 0, 7, 14);
        mainPane.add(itemsTable, 7, 0, 3, 5);
        mainPane.add(pane, 7, 5, 3, 2);
//        mainPane.add(total, 7, 6, 2, 1);
//        mainPane.add(totalItems, 9, 6);
//        mainPane.add(quantity, 7, 7, 2, 1);
//        mainPane.add(voidSelected, 9, 7);
        mainPane.add(barcode, 7, 7, 3, 1);
        mainPane.add(numbers, 7, 8, 3, 6);
        mainPane.add(payment, 7, 14, 3, 2);
        mainPane.add(halfPrice, 2, 14, 1, 2);
        mainPane.add(logoff, 0, 14, 1, 2);
        mainPane.add(lookup, 1, 14, 1, 2);
        mainPane.add(assisstance, 3, 14, 1, 2);
        mainPane.add(alertMessage, 4, 14, 3, 2);
//        mainPane.add(mainRefund, 7, 0);
//        mainPane.add(screenLabel, 0, 0, 2, 1);

        for (int i = 1; i <= 10; i++) {
            ColumnConstraints col = new ColumnConstraints();
            col.setPrefWidth(SCREEN_WIDTH / 10);
            col.setFillWidth(true);
            col.setHgrow(Priority.ALWAYS);
            mainPane.getColumnConstraints().add(col);
        }

        for (int i = 1; i <= 16; i++) {
            RowConstraints row = new RowConstraints();
            row.setPrefHeight(SCREEN_HEIGHT / 16);
            row.setFillHeight(true);
            row.setVgrow(Priority.ALWAYS);
            mainPane.getRowConstraints().add(row);
        }

//        mainPane.getRowConstraints().get(0).setPrefHeight(SCREEN_HEIGHT / 20);
//        mainPane.getColumnConstraints().get(0).setPrefWidth(SCREEN_WIDTH / 8);
    }

    private GridPane createNumbersPane() {
        GridPane grid = new GridPane();

        Button clear = new Button("clr");
        clear.setId("number");
        clear.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        HBox hClear = new HBox(0);
        hClear.getChildren().add(clear);
        clear.setOnAction((ActionEvent event) -> {
            barcode.setText("");
        });

        Button seven = new Button("7");
        seven.setId("number");
        HBox hSeven = new HBox(0);
        seven.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        hSeven.getChildren().add(seven);

        seven.setOnAction((ActionEvent event) -> {
            if (barcode.getText().length() == 20) {
                showMessageAlert("Maximum size reached", 2000);
                return;
            }
            barcode.setText(barcode.getText() + "7");
        });

        Button eight = new Button("8");
        eight.setId("number");
        HBox hEight = new HBox(0);
        eight.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        hEight.getChildren().add(eight);

        eight.setOnAction((ActionEvent event) -> {
            if (barcode.getText().length() == 20) {
                showMessageAlert("Maximum size reached", 2000);
                return;
            }
            barcode.setText(barcode.getText() + "8");
        });

        Button nine = new Button("9");
        nine.setId("number");
        HBox hNine = new HBox(0);
        nine.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        hNine.getChildren().add(nine);

        nine.setOnAction((ActionEvent event) -> {
            if (barcode.getText().length() == 20) {
                showMessageAlert("Maximum size reached", 2000);
                return;
            }
            barcode.setText(barcode.getText() + "9");
        });

        Button four = new Button("4");
        four.setId("number");
        HBox hFour = new HBox(0);
        four.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        hFour.getChildren().add(four);

        four.setOnAction((ActionEvent event) -> {
            if (barcode.getText().length() == 20) {
                showMessageAlert("Maximum size reached", 2000);
                return;
            }
            barcode.setText(barcode.getText() + "4");
        });

        Button five = new Button("5");
        five.setId("number");
        HBox hFive = new HBox(0);
        five.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        hFive.getChildren().add(five);

        five.setOnAction((ActionEvent event) -> {
            if (barcode.getText().length() == 20) {
                showMessageAlert("Maximum size reached", 2000);
                return;
            }
            barcode.setText(barcode.getText() + "5");
        });

        Button six = new Button("6");
        six.setId("number");
        HBox hSix = new HBox(0);
        six.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        hSix.getChildren().add(six);

        six.setOnAction((ActionEvent event) -> {
            if (barcode.getText().length() == 20) {
                showMessageAlert("Maximum size reached", 2000);
                return;
            }
            barcode.setText(barcode.getText() + "6");
        });

        Button one = new Button("1");
        one.setId("number");
        HBox hOne = new HBox(0);
        one.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        hOne.getChildren().add(one);

        one.setOnAction((ActionEvent event) -> {
            if (barcode.getText().length() == 20) {
                showMessageAlert("Maximum size reached", 2000);
                return;
            }
            barcode.setText(barcode.getText() + "1");
        });

        Button two = new Button("2");
        two.setId("number");
        HBox hTwo = new HBox(0);
        two.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        hTwo.getChildren().add(two);

        two.setOnAction((ActionEvent event) -> {
            if (barcode.getText().length() == 20) {
                showMessageAlert("Maximum size reached", 2000);
                return;
            }
            barcode.setText(barcode.getText() + "2");
        });

        Button three = new Button("3");
        three.setId("number");
        HBox hThree = new HBox(0);
        three.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        hThree.getChildren().add(three);

        three.setOnAction((ActionEvent event) -> {
            if (barcode.getText().length() == 20) {
                showMessageAlert("Maximum size reached", 2000);
                return;
            }
            barcode.setText(barcode.getText() + "3");
        });

        Button zero = new Button("0");
        zero.setId("number");
        HBox hZero = new HBox(0);
        zero.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        hZero.getChildren().add(zero);

        zero.setOnAction((ActionEvent event) -> {
            if (barcode.getText().length() == 20) {
                showMessageAlert("Maximum size reached", 2000);
                return;
            }
            barcode.setText(barcode.getText() + "0");
        });

        Button dZero = new Button("00");
        dZero.setId("number");
        HBox hDzero = new HBox(0);
        dZero.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        hDzero.getChildren().add(dZero);

        dZero.setOnAction((ActionEvent event) -> {
            if (barcode.getText().length() == 20) {
                showMessageAlert("Maximum size reached", 2000);
                return;
            }
            barcode.setText(barcode.getText() + "00");
        });

        Button enter = new Button("Ent");
        enter.setId("number");
        HBox hEnter = new HBox(0);
        enter.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        hEnter.getChildren().add(enter);

        enter.setOnAction((ActionEvent event) -> {
            if (!barcode.getText().equals("")) {
                getProductByBarcode(barcode.getText());
                barcode.setText("");
                if (!barcode.isFocused()) {
                    barcode.requestFocus();
                }
            }
        });

        grid.add(clear, 0, 0, 1, 1);
        grid.add(seven, 0, 1, 1, 1);
        grid.add(eight, 1, 1, 1, 1);
        grid.add(nine, 2, 1, 1, 1);
        grid.add(four, 0, 2, 1, 1);
        grid.add(five, 1, 2, 1, 1);
        grid.add(six, 2, 2, 1, 1);
        grid.add(one, 0, 3, 1, 1);
        grid.add(two, 1, 3, 1, 1);
        grid.add(three, 2, 3, 1, 1);
        grid.add(zero, 0, 4, 1, 1);
        grid.add(dZero, 1, 4, 1, 1);
        grid.add(enter, 2, 4, 1, 1);

        for (int i = 1; i <= 3; i++) {
            ColumnConstraints col = new ColumnConstraints();
            col.setFillWidth(true);
            col.setHgrow(Priority.ALWAYS);
            grid.getColumnConstraints().add(col);
        }

        for (int i = 1; i <= 5; i++) {
            RowConstraints row = new RowConstraints();
            row.setFillHeight(true);
            row.setVgrow(Priority.ALWAYS);
            grid.getRowConstraints().add(row);
        }

        return grid;
    }

    private void initPayment() {
        paymentPane = new GridPane();

        fivePounds = new Button(symbol + "5");
        fivePounds.setId("paymentMethods");
        fivePounds.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        fivePounds.setOnAction((ActionEvent event) -> {
            if (!sale.getSaleItems().isEmpty()) {
                Platform.runLater(() -> {
                    addMoney(PaymentItem.PaymentType.CASH, new BigDecimal("5.00"));
                });
            }
        });

        tenPounds = new Button(symbol + "10");
        tenPounds.setId("paymentMethods");
        tenPounds.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        tenPounds.setOnAction((ActionEvent event) -> {
            if (!sale.getSaleItems().isEmpty()) {
                Platform.runLater(() -> {
                    addMoney(PaymentItem.PaymentType.CASH, new BigDecimal("10.00"));
                });
            }
        });

        twentyPounds = new Button(symbol + "20");
        twentyPounds.setId("paymentMethods");
        twentyPounds.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        twentyPounds.setOnAction((ActionEvent event) -> {
            if (!sale.getSaleItems().isEmpty()) {
                Platform.runLater(() -> {
                    addMoney(PaymentItem.PaymentType.CASH, new BigDecimal("20.00"));
                });
            }
        });

        customValue = new Button("Custom Value");
        customValue.setId("paymentMethods");
        customValue.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        customValue.setOnAction((ActionEvent event) -> {
            if (!sale.getSaleItems().isEmpty()) {
                int value = NumberEntry.showNumberEntryDialog(this, "Enter amount");
                Platform.runLater(() -> {
                    double d = (double) value;
                    addMoney(PaymentItem.PaymentType.CASH, new BigDecimal(Double.toString(d / 100)));
                });
            }
        });

        exactValue = new Button("Exact Value");
        exactValue.setId("paymentMethods");
        exactValue.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        exactValue.setOnAction((ActionEvent event) -> {
            if (!sale.getSaleItems().isEmpty()) {
                Platform.runLater(() -> {
                    addMoney(PaymentItem.PaymentType.CASH, amountDue);
                });
            }
        });

        card = new Button("Credit Card");
        card.setId("paymentMethods");
        card.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        card.setOnAction((ActionEvent event) -> {
            if (!sale.getSaleItems().isEmpty()) {
                int val = NumberEntry.showNumberEntryDialog(this, "Enter Card Value");
                double d = (double) val;
                addMoney(PaymentItem.PaymentType.CARD, new BigDecimal(Double.toString(d / 100)));
            }
        });
        card.setDisable(true);

        addCustomer = new Button("Add Customer");
        addCustomer.setId("paymentMethods");
        addCustomer.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        addCustomer.setOnAction((ActionEvent event) -> {
            if (!sale.getSaleItems().isEmpty()) {
                Platform.runLater(() -> {
                    if (sale.getCustomerID() != 1) {
                        setCustomer(null);
                        chargeAccount.setDisable(true);
                        loyaltyButton.setDisable(true);
                        addCustomer.setText("Add Customer");
                        return;
                    }
                    Customer c = CustomerSelectDialog.showDialog(MainStage.this, dc, "Search for Customer");
                    if (c != null) {
                        setCustomer(c);
                        chargeAccount.setDisable(false);
                        loyaltyButton.setDisable(false);
                        addCustomer.setText("Remove Customer");
                    }
                });
            }
        });

        chargeAccount = new Button("Charge Account");
        chargeAccount.setId("paymentMethods");
        chargeAccount.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        chargeAccount.setDisable(true);
        chargeAccount.setOnAction((ActionEvent event) -> {
            sale.setMop(Sale.MOP_CHARGEACCOUNT);
            Platform.runLater(() -> {
                addMoney(PaymentItem.PaymentType.ACCOUNT, amountDue);
            });
        });

        cheque = new Button("Cheque");
        cheque.setId("paymentMethods");
        cheque.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        cheque.setOnAction((ActionEvent event) -> {
            if (!sale.getSaleItems().isEmpty()) {
                int val = NumberEntry.showNumberEntryDialog(this, "Enter Cheque Value");
                double d = (double) val;
                Platform.runLater(() -> {
                    addMoney(PaymentItem.PaymentType.CHEQUE, new BigDecimal(Double.toString(d / 100)));
                });
            }
        });

        saleCustomer = new Label("No Customer");
        saleCustomer.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        saleCustomer.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));

        back = new Button("Back");
        back.setId("bottom");
        back.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        back.setOnAction((ActionEvent event) -> {
            setPanel(mainPane);
            screenLabel.setText(lastScreen);
            barcode.requestFocus();
        });

        paymentsList = new ListView<>();
        paymentsList.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        obPayments = FXCollections.observableArrayList();
        paymentsList.setItems(obPayments);
        paymentsList.setId("PAYMENT_LIST");

        paymentTotal = new Label("Total: " + symbol + sale.getTotal().toString());
        paymentTotal.setId("total");
        paymentTotal.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        paymentTotal.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));

        voidItem = new Button("Void");
        voidItem.setId("paymentMethods");
        voidItem.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        voidItem.setOnAction((ActionEvent event) -> {
            if (paymentsList.getSelectionModel().getSelectedIndex() > -1) {
                PaymentItem pi = paymentsList.getSelectionModel().getSelectedItem().clone();
                obPayments.remove(paymentsList.getSelectionModel().getSelectedIndex());
                paymentsList.refresh();
                addMoney(pi.getType(), pi.getValue().negate());
            }
        });

        paymentItemsTable = new TableView();
        paymentItemsTable.setId("ITEMS");
        paymentItemsTable.setEditable(false);
        TableColumn qty = new TableColumn("Qty.");
        TableColumn itm = new TableColumn("Item");
        TableColumn cst = new TableColumn(symbol);
        qty.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        itm.setCellValueFactory(new PropertyValueFactory<>("name"));
        cst.setCellValueFactory(new PropertyValueFactory<>("totalPrice"));
        qty.prefWidthProperty().bind(paymentItemsTable.widthProperty().divide(7));
        itm.prefWidthProperty().bind(paymentItemsTable.widthProperty().divide(7).multiply(4));
        cst.prefWidthProperty().bind(paymentItemsTable.widthProperty().divide(7).multiply(2));
        paymentItemsTable.getColumns().addAll(qty, itm, cst);
        obTable = FXCollections.observableArrayList();
        payObTable = FXCollections.observableArrayList();
        paymentItemsTable.setItems(payObTable);

        paymentItemsTable.setRowFactory(tv -> {
            TableRow<SaleItem> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    SaleItem rowData = row.getItem();
                    int q = NumberEntry.showNumberEntryDialog(this, "Set quantity", rowData.getQuantity());
                    rowData.setQuantity(q);
                    setTotalLabel();
                    paymentItemsTable.refresh();
                    sale.updateTotal();
                    setTotalLabel();
                }
            });
            return row;
        });

        voidSale = new Button("Void Sale");
        voidSale.setId("paymentMethods");
        voidSale.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        voidSale.setOnAction((ActionEvent event) -> {
            if (YesNoDialog.showDialog(this, "Void Sale", "Are you sure you want to void the sale?") == YesNoDialog.YES) {
                newSale();
                setPanel(mainPane);
            }
        });

        settingsButton = new Button("Settings");
        settingsButton.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        settingsButton.setOnAction((ActionEvent event) -> {
            if (staff.getPosition() >= 3) {
                SetupDialog.showDialog(MainStage.this);
                MessageDialog.showMessage(this, "Settings", "Some changes will apply after restart");
            } else {
                MessageDialog.showMessage(this, "Settings", "You are not allowed to view this screen");
            }
        });

        cashUp = new Button("Cash Up");
        cashUp.setId("paymentMethods");
        cashUp.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        cashUp.setOnAction((ActionEvent event) -> {
            if (staff.getPosition() >= 3) {
                LOG.log(Level.INFO, "Submitting all sales to the server");
                sendSalesToServer();
                SaleCache.getInstance().clearAll();
                int res = CashUpDialog.showDialog(this, dc, till);
                if (res == 1) {
//                    clearLoginScreen();
                    new Thread() {
                        @Override
                        public void run() {
                            logoff();
                            Platform.runLater(() -> {
                                MessageScreen.hideWindow();
                            });
                        }
                    }.start();
                }
            } else {
                MessageDialog.showMessage(this, "Cash Up", "You are not allowed to view this screen");
            }
        });

        paymentLogoff = new Button("Logoff");
        paymentLogoff.setId("cRed");
        paymentLogoff.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        paymentLogoff.setOnAction((ActionEvent event) -> {
            new Thread() {
                @Override
                public void run() {
                    logoff();
                    Platform.runLater(() -> {
                        MessageScreen.hideWindow();
                    });
                }
            }.start();
        });
        paymentLogoff.setStyle("-fx-base: #0000FF;");

        paymentMessages = new Label();
        paymentMessages.setFont(Font.font("Tahoma", FontWeight.NORMAL, 40));
        paymentMessages.setId("message");
        paymentMessages.setMinSize(0, 0);
        paymentMessages.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

        clockOff = new Button("Clock Off");
        clockOff.setId("cRed");
        clockOff.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        clockOff.setOnAction((ActionEvent event) -> {
            try {
                dc.clockOff(staff.getId());
                MessageDialog.showMessage(this, "Clock Off", "Clocked off at " + new SimpleDateFormat("HH:mm:ss dd/MM/yyyy").format(new Date()));
            } catch (IOException | SQLException | StaffNotFoundException ex) {
                Logger.getLogger(MainStage.class.getName()).log(Level.SEVERE, null, ex);
            }
        });

        refundButton = new Button("Refund");
        refundButton.setId("paymentMethods");
        refundButton.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        refundButton.setOnAction((ActionEvent event) -> {
            setRefund(!refundMode);
        });

        loyaltyButton = new Button("Spend Points");
        loyaltyButton.setId("paymentMethods");
        loyaltyButton.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
        loyaltyButton.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        loyaltyButton.setDisable(true);
        loyaltyButton.setOnAction((ActionEvent event) -> {
            try {
                final Customer c = dc.getCustomer(sale.getCustomerID());
                int maxSpend = sale.getTotal().divide(new BigDecimal(JavaFXJTill.settings.getProperty("TOTAL_SPEND_VALE"))).intValue();
                if (c.getLoyaltyPoints() < maxSpend) {
                    maxSpend = c.getLoyaltyPoints();
                }
                final int toSpend = NumberEntry.showNumberEntryDialog(this, "Points remaining: " + c.getLoyaltyPoints() + ". Max for sale: " + maxSpend + ".", maxSpend);
                final int res = c.removeLoyaltyPoints(toSpend);
                if (res == -1) {
                    MessageDialog.showMessage(this, "Error", "Not Enough Points");
                    return;
                }
                double value = Double.parseDouble(JavaFXJTill.settings.getProperty("LOYALTY_VALUE"));
                BigDecimal roRemove = new BigDecimal(Double.toString(toSpend * value));
                sale.setTotal(sale.getTotal().subtract(roRemove));
                setTotalLabel();
                dc.updateCustomer(c);
            } catch (IOException | CustomerNotFoundException | SQLException ex) {
                Logger.getLogger(MainStage.class.getName()).log(Level.SEVERE, null, ex);
            }
        });

        GridPane pane = new GridPane();

        for (int i = 1; i <= 4; i++) {
            ColumnConstraints col = new ColumnConstraints();
            col.setPercentWidth(25);
            col.setFillWidth(true);
            col.setHgrow(Priority.ALWAYS);
            pane.getColumnConstraints().add(col);
        }

        for (int i = 1; i <= 4; i++) {
            RowConstraints row = new RowConstraints();
            row.setPercentHeight(25);
            row.setFillHeight(true);
            row.setVgrow(Priority.ALWAYS);
            pane.getRowConstraints().add(row);
        }

        coupon = new Button("Coupon");
        coupon.setId("paymentMethods");
        coupon.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
        coupon.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        coupon.setOnAction((ActionEvent) -> {

        });

        saveTransaction = new Button("Save Transaction");
        saveTransaction.setId("paymentMethods");
        saveTransaction.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
        saveTransaction.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        saveTransaction.setOnAction((ActionEvent) -> {

        });

        pane.add(fivePounds, 0, 0);
        pane.add(tenPounds, 1, 0);
        pane.add(twentyPounds, 2, 0);
        pane.add(customValue, 3, 0);
        pane.add(exactValue, 0, 1);
        pane.add(card, 1, 1);
        pane.add(cheque, 2, 1);
        pane.add(coupon, 3, 1);
        pane.add(addCustomer, 0, 2);
        pane.add(chargeAccount, 1, 2);
        pane.add(loyaltyButton, 2, 2);
        pane.add(cashUp, 3, 2);
        pane.add(voidSale, 0, 3);
        pane.add(refundButton, 1, 3);
        pane.add(voidItem, 2, 3);
        pane.add(saveTransaction, 3, 3);
        
        paymentPane.add(saleCustomer, 8, 6, 2, 1);
        paymentPane.add(pane, 0, 0, 7, 10);
        paymentPane.add(back, 7, 14, 3, 2);
        paymentPane.add(paymentsList, 7, 8, 3, 5);
        paymentPane.add(paymentItemsTable, 7, 0, 3, 5);
        paymentPane.add(paymentTotal, 7, 5, 3, 1);
        paymentPane.add(paymentMessages, 4, 14, 3, 2);
        paymentPane.add(clockOff, 1, 14, 1, 2);
        paymentPane.add(paymentLogoff, 0, 14, 1, 2);

        for (int i = 1; i <= 10; i++) {
            ColumnConstraints col = new ColumnConstraints();
            col.setPrefWidth(SCREEN_WIDTH / 10);
            col.setFillWidth(true);
            col.setHgrow(Priority.ALWAYS);
            paymentPane.getColumnConstraints().add(col);
        }

        for (int i = 1; i <= 16; i++) {
            RowConstraints row = new RowConstraints();
            row.setPrefHeight(SCREEN_HEIGHT / 16);
            row.setFillHeight(true);
            row.setVgrow(Priority.ALWAYS);
            paymentPane.getRowConstraints().add(row);
        }

//        paymentPane.getRowConstraints().get(0).setPrefHeight(SCREEN_HEIGHT / 20);
    }

    /**
     * The type of login to use.
     *
     * @param type can be either BUTTONS or CODE.
     */
    private void setLoginType(int type) {
        this.type = type;
        Platform.runLater(() -> {
            loginArea.getChildren().clear();
            loginArea.add(getLoginPane(), 5, 0);
        });
    }

    private void login(int id) {
        try {
            Platform.runLater(() -> {
                MessageScreen.changeMessage("Logging in");
                MessageScreen.showWindow();
            });
            try {
                MainStage.this.staff = dc.getStaff(id);
                dc.clockOn(id);
                dc.tillLogin(id);
            } catch (IOException ex) {
                for (Staff s : staffCache) {
                    if (s.getId() == id) {
                        this.staff = s;
                        break;
                    }
                }
            }
            if (staff == null) {
                throw new StaffNotFoundException(id + " not found");
            }
            Platform.runLater(() -> {
                staffLabel.setText(staff.getName());
                if (!buttonPanes.isEmpty()) {
                    buttonPane.getChildren().clear();
                    buttonPane.getChildren().add(def_screen.getPane());
                    screenLabel.setText(def_screen.getName());
                }
            });
            Platform.runLater(() -> {
                setPanel(mainPane);
                barcode.requestFocus();
            });
            try {
                Sale rs = null;
                for (Sale s : saleCache) {
                    if (s.getStaff().equals(staff)) {
                        rs = s;
                    }
                }
                saleCache.remove(rs);
                if (rs == null) {
                    rs = dc.resumeSale(staff);
                }
                if (rs != null) {
                    MainStage.this.sale = rs;
                    final Sale frs = rs;
                    Platform.runLater(() -> {
                        MessageScreen.changeMessage("Getting transaction");
                        obTable.setAll(frs.getSaleItems());
                        payObTable.setAll(frs.getSaleItems());
                        setTotalLabel();
                        resumeSale(frs);
                    });
                    try {
                        final Customer c = dc.getCustomer(rs.getCustomerID());
                        Platform.runLater(() -> {
                            setCustomer(c);
                        });
                    } catch (CustomerNotFoundException | IOException | SQLException ex) {
                        Logger.getLogger(MainStage.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } else {
                    Platform.runLater(() -> {
                        newSale();
                        MessageScreen.hideWindow();
                    });
                }
                if (logoutTimeout > 0) {
                    if (timer != null) {
                        timer.stop();
                    }
                    timer = new Timer(logoutTimeout * 1000, new TimerHandler());
                    timer.setRepeats(false);
                    timer.start();
                }
            } catch (IOException ex) {
                if (staff != null) {
                    Platform.runLater(() -> {
                        newSale();
                        MessageScreen.hideWindow();
                    });
                }
            }
        } catch (LoginException | SQLException | StaffNotFoundException ex) {
            Platform.runLater(() -> {
                MessageScreen.hideWindow();
                MessageDialog.showMessage(MainStage.this, "Error", ex.getMessage());
            });
        }
        Platform.runLater(() -> {
            MessageScreen.hideWindow();
        });
    }

    private void initLogin() {
        loginPane = new GridPane();

        for (int i = 1; i <= 10; i++) {
            ColumnConstraints col = new ColumnConstraints();         //login pane columns
            col.setPrefWidth(SCREEN_WIDTH / 10);
            col.setFillWidth(true);
            col.setHgrow(Priority.ALWAYS);
            loginPane.getColumnConstraints().add(col);
        }

        for (int i = 1; i <= 16; i++) {
            RowConstraints row = new RowConstraints();               //login pane rows
            row.setPrefHeight(SCREEN_HEIGHT / 16);
            row.setFillHeight(true);
            row.setVgrow(Priority.ALWAYS);
            loginPane.getRowConstraints().add(row);
        }

//        loginPane.getRowConstraints().get(0).setPrefHeight(SCREEN_HEIGHT / 20);

        exit = new Button("Exit JTill");
        exit.setId("blue");
        exit.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        HBox hExit = new HBox(0);
        hExit.getChildren().add(exit);
        exit.setOnAction((ActionEvent event) -> {
            Platform.runLater(() -> {
                if (YesNoDialog.showDialog(this, "Exit JTill", "Are you sure you want to exit JTill?") == YesNoDialog.YES) {
                    try {
                        dc.close();
                    } catch (IOException ex) {
                    } finally {
                        System.exit(0);
                    }
                }
            });
        });

        lock = new Button("Lock");
        lock.setId("blue");
        lock.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        lock.setOnAction((ActionEvent event) -> {
            Platform.runLater(() -> {
                setPanel(lockPane);
            });
        });

        login = new Button("Login");
        login.setId("blue");
        login.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        HBox hLogin = new HBox(0);
        hLogin.getChildren().add(login);
        login.setOnAction((ActionEvent event) -> {
            int val = NumberEntry.showNumberEntryDialog(this, "Enter Logon ID");
            if (val == 0) {
                return;
            }
            Platform.runLater(() -> {
                try {
                    final Staff s = dc.getStaff(val);
                    dc.clockOn(s.getId());
                    Button button = new Button(s.getName());
                    button.setId("blue");
                    button.prefWidthProperty().bind(staffLayout.widthProperty().divide(4));
                    button.prefHeightProperty().bind(staffLayout.heightProperty().divide(2));
                    button.setOnAction((ActionEvent evt) -> {
                        login(s.getId());
                    });
                    staffLayout.add(button, x, y);

                    x++;
                    if (x == 4) {
                        x = 0;
                        y++;
                    }
                } catch (StaffNotFoundException | SQLException ex) {
                    MessageDialog.showMessage(MainStage.this, "Log on", ex.getMessage());
                } catch (IOException ex) {
                    MessageDialog.showMessage(MainStage.this, "Error", "Server offline");
                }
            });
        });

        print = new Button("Print Last");
        print.setId("blue");
        print.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        print.setOnAction((ActionEvent event) -> {
            if (lastSale == null) {
                MainStage.this.showMessageAlert("No previous sale", 2000);
                return;
            }
            try {
                ReceiptPrinter.print(dc, lastSale);
            } catch (PrinterException ex) {
                MainStage.this.showMessageAlert("Printer Error", 2000);
            }
        });

        loginMessage = new Label();
        loginMessage.setFont(Font.font("Tahoma", FontWeight.NORMAL, 40));
        loginMessage.setId("message");
        loginMessage.setMinSize(0, 0);
        loginMessage.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

        loginArea = new GridPane();

        loginPane.add(exit, 0, 14, 1, 2);
        loginPane.add(print, 2, 14, 1, 2);
        loginPane.add(loginMessage, 4, 14, 3, 2);
        loginPane.add(loginArea, 1, 0, 8, 12);
    }

    private void initLock() {
        lockPane = new GridPane();

        for (int i = 1; i <= 5; i++) {
            ColumnConstraints col = new ColumnConstraints();         //login pane columns
            col.setPrefWidth(SCREEN_WIDTH / 5);
            col.setFillWidth(true);
            col.setHgrow(Priority.ALWAYS);
            lockPane.getColumnConstraints().add(col);
        }

        for (int i = 1; i <= 5; i++) {
            RowConstraints row = new RowConstraints();               //login pane rows
            row.setPrefHeight(SCREEN_HEIGHT / 5);
            row.setFillHeight(true);
            row.setVgrow(Priority.ALWAYS);
            lockPane.getRowConstraints().add(row);
        }

        Button button = new Button("Unlock");
        button.setId("blue");
        button.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        button.setOnAction((ActionEvent e) -> {
            int code = NumberEntry.showNumberEntryDialog(this, "Enter code");
            if (code == Integer.parseInt(JavaFXJTill.settings.getProperty("UNLOCK_CODE"))) {
                Platform.runLater(() -> {
                    setPanel(loginPane);
                });
            }
        });

        lockPane.add(button, 1, 1, 3, 3);
    }

    private GridPane getLoginPane() {
        staffLayout = new GridPane();
        if (type == BUTTONS) {
            loginPane.add(login, 1, 14, 1, 2);
            staffLayout.setHgap(40);
            staffLayout.setVgap(40);

            for (int i = 1; i <= 4; i++) {
                ColumnConstraints col = new ColumnConstraints();        //staff layout columns
                col.setPrefWidth(staffLayout.getWidth() / 4);
                col.setFillWidth(true);
                col.setHgrow(Priority.ALWAYS);
                staffLayout.getColumnConstraints().add(col);
            }

            for (int i = 1; i <= 2; i++) {
                RowConstraints row = new RowConstraints();              //staff layout rows
                row.setPrefHeight(staffLayout.getHeight() / 5);
                row.setFillHeight(true);
                row.setVgrow(Priority.ALWAYS);
                staffLayout.getRowConstraints().add(row);
            }
        } else {
            for (int i = 1; i <= 4; i++) {
                ColumnConstraints col = new ColumnConstraints();        //staff layout columns
                col.setPrefWidth(staffLayout.getWidth() / 5);
                col.setFillWidth(true);
                col.setHgrow(Priority.ALWAYS);
                staffLayout.getColumnConstraints().add(col);
            }

            for (int i = 1; i <= 2; i++) {
                RowConstraints row = new RowConstraints();              //staff layout rows
                row.setPrefHeight(staffLayout.getHeight() / 2);
                row.setFillHeight(true);
                row.setVgrow(Priority.ALWAYS);
                staffLayout.getRowConstraints().add(row);
            }

            GridPane nums = new GridPane();

            loginNumber = new TextField();
            loginNumber.setMaxHeight(50);
            loginNumber.setMaxWidth(400);
            loginNumber.setMinHeight(50);
            loginNumber.setMaxWidth(400);
            loginNumber.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
            loginNumber.setOnAction((ActionEvent event) -> {
                new Thread() {
                    @Override
                    public void run() {
                        if (!"".equals(loginNumber.getText())) {
                            int id = Integer.parseInt(loginNumber.getText());
                            login(id);
                            loginNumber.setText("");
                        }
                    }
                }.start();
            });
            nums.add(loginNumber, 1, 2, 4, 1);

            Button seven = new Button("7");
            seven.setId("number");
            seven.setMaxSize(100, 100);
            seven.setMinSize(100, 100);
            HBox hSeven = new HBox(0);
            //hSeven.setAlignment(Pos.TOP_LEFT);
            hSeven.getChildren().add(seven);
            nums.add(hSeven, 1, 3);

            seven.setOnAction((ActionEvent event) -> {
                loginNumber.setText(loginNumber.getText() + "7");
            });

            Button eight = new Button("8");
            eight.setId("number");
            eight.setMaxSize(100, 100);
            eight.setMinSize(100, 100);
            HBox hEight = new HBox(0);
            //hSeven.setAlignment(Pos.TOP_CENTER);
            hEight.getChildren().add(eight);
            nums.add(hEight, 2, 3);

            eight.setOnAction((ActionEvent event) -> {
                loginNumber.setText(loginNumber.getText() + "8");
            });

            Button nine = new Button("9");
            nine.setId("number");
            nine.setMaxSize(100, 100);
            nine.setMinSize(100, 100);
            HBox hNine = new HBox(0);
            //hSeven.setAlignment(Pos.TOP_RIGHT);
            hNine.getChildren().add(nine);
            nums.add(hNine, 3, 3);

            nine.setOnAction((ActionEvent event) -> {
                loginNumber.setText(loginNumber.getText() + "9");
            });

            Button four = new Button("4");
            four.setId("number");
            four.setMaxSize(100, 100);
            four.setMinSize(100, 100);
            HBox hFour = new HBox(0);
            //hFour.setAlignment(Pos.CENTER_LEFT);
            hFour.getChildren().add(four);
            nums.add(hFour, 1, 4);

            four.setOnAction((ActionEvent event) -> {
                loginNumber.setText(loginNumber.getText() + "4");
            });

            Button five = new Button("5");
            five.setId("number");
            five.setMaxSize(100, 100);
            five.setMinSize(100, 100);
            HBox hFive = new HBox(0);
            //hFive.setAlignment(Pos.CENTER);
            hFive.getChildren().add(five);
            nums.add(hFive, 2, 4);

            five.setOnAction((ActionEvent event) -> {
                loginNumber.setText(loginNumber.getText() + "5");
            });

            Button six = new Button("6");
            six.setId("number");
            six.setMaxSize(100, 100);
            six.setMinSize(100, 100);
            HBox hSix = new HBox(0);
            //hSix.setAlignment(Pos.CENTER_RIGHT);
            hSix.getChildren().add(six);
            nums.add(hSix, 3, 4);

            six.setOnAction((ActionEvent event) -> {
                loginNumber.setText(loginNumber.getText() + "6");
            });

            Button one = new Button("1");
            one.setId("number");
            one.setMaxSize(100, 100);
            one.setMinSize(100, 100);
            HBox hOne = new HBox(0);
            //hOne.setAlignment(Pos.BOTTOM_LEFT);
            hOne.getChildren().add(one);
            nums.add(hOne, 1, 5);

            one.setOnAction((ActionEvent event) -> {
                loginNumber.setText(loginNumber.getText() + "1");
            });

            Button two = new Button("2");
            two.setId("number");
            two.setMaxSize(100, 100);
            two.setMinSize(100, 100);
            HBox hTwo = new HBox(0);
            //hTwo.setAlignment(Pos.BOTTOM_CENTER);
            hTwo.getChildren().add(two);
            nums.add(hTwo, 2, 5);

            two.setOnAction((ActionEvent event) -> {
                loginNumber.setText(loginNumber.getText() + "2");
            });

            Button three = new Button("3");
            three.setId("number");
            three.setMaxSize(100, 100);
            three.setMinSize(100, 100);
            HBox hThree = new HBox(0);
            //hThree.setAlignment(Pos.BOTTOM_RIGHT);
            hThree.getChildren().add(three);
            nums.add(hThree, 3, 5);

            three.setOnAction((ActionEvent event) -> {
                loginNumber.setText(loginNumber.getText() + "3");
            });

            Button zero = new Button("0");
            zero.setId("number");
            zero.setMaxSize(200, 100);
            zero.setMinSize(200, 100);
            HBox hZero = new HBox(0);
            hZero.getChildren().add(zero);
            nums.add(hZero, 1, 6, 2, 1);

            zero.setOnAction((ActionEvent event) -> {
                loginNumber.setText(loginNumber.getText() + "0");
            });

            Button dZero = new Button("00");
            dZero.setId("number");
            dZero.setMaxSize(100, 100);
            dZero.setMinSize(100, 100);
            HBox hDzero = new HBox(0);
            hDzero.getChildren().add(dZero);
            nums.add(hDzero, 3, 6);

            dZero.setOnAction((ActionEvent event) -> {
                loginNumber.setText(loginNumber.getText() + "00");
            });

            Button clear = new Button("Clear");
            clear.setId("number");
            clear.setMaxSize(100, 200);
            clear.setMinSize(100, 200);
            HBox hClear = new HBox(0);
            hClear.getChildren().add(clear);
            nums.add(hClear, 4, 3, 1, 2);

            clear.setOnAction((ActionEvent event) -> {
                loginNumber.setText("");
            });

            Button enter = new Button("Enter");
            enter.setId("number");
            enter.setMaxSize(100, 200);
            enter.setMinSize(100, 200);
            HBox hEnter = new HBox(0);
            hEnter.getChildren().add(enter);
            nums.add(hEnter, 4, 5, 1, 2);

            enter.setOnAction((ActionEvent event) -> {
                new Thread() {
                    @Override
                    public void run() {
                        if (!"".equals(loginNumber.getText())) {
                            int id = Integer.parseInt(loginNumber.getText());
                            login(id);
                            loginNumber.setText("");
                        }
                    }
                }.start();
            });

            staffLayout.add(nums, 1, 1, 3, 3);
            loginNumber.requestFocus();
        }
        return staffLayout;
    }

    private void clearLoginScreen() {
        staffLayout.getChildren().clear();
        x = 0;
        y = 0;
    }

    private void setCustomer(Customer c) {
        if (c != null) {
            saleCustomer.setText(c.getName());
            addCustomer.setText("Remove Customer");
            chargeAccount.setDisable(false);
            sale.setCustomerID(c.getId());
        } else {
            saleCustomer.setText("No Customer");
            addCustomer.setText("Add Customer");
            chargeAccount.setDisable(true);
            sale.setCustomerID(1);
        }
    }

    private void addMoney(PaymentItem.PaymentType type, BigDecimal val) {
        amountDue = amountDue.subtract(val);
        if (val.compareTo(BigDecimal.ZERO) > 0) {
            obPayments.add(new PaymentItem(type, val));
        }
        total.setText("Total: " + symbol + amountDue);
        paymentTotal.setText("Total: " + symbol + amountDue);
        if (amountDue.compareTo(BigDecimal.ZERO) < 0) {
            MessageDialog.showMessage(this, "Change", "Change Due: " + symbol + amountDue.abs().toString());
        }
        if (amountDue.compareTo(BigDecimal.ZERO) <= 0) {
            completeCurrentSale();
        }
    }

    private void sendSalesToServer() {
        LOG.log(Level.INFO, "Sending sales data to the server");
        final List<Sale> sales = SaleCache.getInstance().getAllSales();
        try {
            dc.sendSales(sales);
            LOG.log(Level.INFO, "Sales data has been sent to the server");
            SaleCache.getInstance().clearAll();
        } catch (Throwable ex) {
            this.showMessageAlert("Error sending sales to Server", 5000);
        }
    }

    private void resumeSale(Sale s) {
        if (s.getCustomer() == null) {
            addCustomer.setText("Add Customer");
            chargeAccount.setDisable(true);
            loyaltyButton.setDisable(true);
        } else {
            addCustomer.setText("Remove Customer");
            chargeAccount.setDisable(false);
            loyaltyButton.setDisable(false);
        }
    }

    private void completeCurrentSale() {
        sale.setDate(new Date());
        Runnable printRun = () -> {
            try {
                ReceiptPrinter.print(dc, sale);
            } catch (PrinterAbortException ex) {
                MainStage.this.showMessageAlert("Printer Error", 2000);
            } catch (PrinterException ex) {
                Logger.getLogger(MainStage.class.getName()).log(Level.SEVERE, null, ex);
            }
        };
        Thread th = new Thread(printRun);
        th.start();
        lastSale = sale.clone();
        sale.complete();
        sale.setStaff(staff);
        sale.setStaffID(staff.getId());
        try {
            try {
                Sale s = dc.addSale(sale);
                LOG.log(Level.INFO, "Sale {0} sent to server", s.getId());
            } catch (IOException e) {
                LOG.log(Level.WARNING, "Error connecting to server");
                SaleCache.getInstance().addSale(sale);
                sale.setId(0);
            }
            if (JavaFXJTill.settings.getProperty("ASK_EMAIL_RECEIPT").equals("TRUE")) {
                if (YesNoDialog.showDialog(this, "Email Receipt", "Email Customer Receipt?") == YesNoDialog.YES) {
                    if (sale.getCustomerID() != 0) {
                        try {
                            final Customer c = dc.getCustomer(sale.getCustomerID());
                            dc.emailReceipt(c.getEmail(), sale);
                        } catch (CustomerNotFoundException ex) {
                            this.showMessage("Email", ex.getMessage());
                        }
                    } else {
                        String email = Keyboard.show(this, "Enter email");
                        dc.emailReceipt(email, sale);
                    }
                }
            }
        } catch (IOException | SQLException ex) {

        }

        try {
            if (JavaFXJTill.settings.getProperty("AUTO_LOGOUT").equals("TRUE")) {
                try {
                    dc.tillLogout(staff);
                } catch (StaffNotFoundException ex) {
                    Logger.getLogger(MainStage.class.getName()).log(Level.SEVERE, null, ex);
                }
                newSale();
                Platform.runLater(() -> {
                    setPanel(loginPane);
                });
            } else {
                newSale();
                setPanel(mainPane);
            }
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, null, ex);
            newSale();
            setPanel(mainPane);
        }
    }

    private void showErrorAlert(Exception ex) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setContentText(ex.toString());
        alert.showAndWait();
    }

    private void showMessageAlert(String message, long duration) {
        new Thread() {
            @Override
            public void run() {
                Platform.runLater(() -> {
                    MainStage.this.alertMessage.setText(message);
                    MainStage.this.paymentMessages.setText(message);
                    MainStage.this.loginMessage.setText(message);
                });
                try {
                    Thread.sleep(duration);
                } catch (InterruptedException ex) {
                    Logger.getLogger(MainStage.class.getName()).log(Level.SEVERE, null, ex);
                }
                Platform.runLater(() -> {
                    MainStage.this.alertMessage.setText("");
                    MainStage.this.paymentMessages.setText("");
                    MainStage.this.loginMessage.setText("");
                });
            }
        }.start();
    }

    /**
     * Log the member of staff off and return to the login screen.
     */
    private boolean logoff() {
        Window window = (Window) this;
        if (!window.isFocused()) {
            this.showMessageAlert("Unable to log off", 5000);
            Platform.runLater(() -> {
                MessageScreen.hideWindow();
            });
            return false;
        }
        if (timer != null) {
            timer.stop();
        }
        try {
            Platform.runLater(() -> {
                MessageScreen.changeMessage("Logging off...");
                MessageScreen.showWindow();
            });
            setRefund(false);
            dc.tillLogout(staff);
        } catch (IOException | StaffNotFoundException ex) {
            Platform.runLater(() -> {
                MessageScreen.hideWindow();
            });
        }
        sale.setStaff(staff);
        if (!sale.getSaleItems().isEmpty()) {
            Platform.runLater(() -> {
                MessageScreen.changeMessage("Saving Transaction");
            });
            try {
                dc.suspendSale(sale, staff);
            } catch (IOException ex) {
                saleCache.add(sale);
            }
        }

        staff = null;
        Platform.runLater(() -> {
            staffLabel.setText("Not Logged In");
            newSale();
            screenLabel.setText("Login");
            setPanel(loginPane);
            if (type == CODE) {
                loginNumber.requestFocus();
            }
        });
        if (newData) {
            initTill();
        }
        return true;
    }

    /**
     * Create a new sale.
     */
    private void newSale() {
        if (staff == null) {
            sale = new Sale(till.getId(), 0);
        } else {
            sale = new Sale(till.getId(), staff.getId());
        }
        sale.setCustomerID(1);
        List<Discount> discounts = DiscountCache.getInstance().getAllDiscounts();
        //Create the discount checkers for the discounts.
        discounts.stream().map((d) -> new DiscountChecker(this, d)).forEachOrdered((checker) -> {
            sale.addListener(checker);
        });
        obTable = FXCollections.observableArrayList();
        obPayments = FXCollections.observableArrayList();
        payObTable = FXCollections.observableArrayList();
        paymentsList.setItems(obPayments);
        paymentItemsTable.setItems(payObTable);
        updateList();
        total.setText("Total: " + symbol + "0.00");
        totalItems.setText("Items: 0");
        paymentTotal.setText("Total: " + symbol + "0.00");
        itemQuantity = 1;
        quantity.setText("Quantity: 1");
        saleCustomer.setText("No Customer");
        addCustomer.setText("Add Customer");
        chargeAccount.setDisable(true);
        loyaltyButton.setDisable(true);
        buttonPane.getChildren().clear();
        if (def_screen != null) {
            changeScreen(def_screen);
        }
        age = 0;
        barcode.requestFocus();
    }

    private void updateList() {
        itemsTable.setItems(obTable);
    }

    private void getProductByBarcode(String barcode) {
        this.barcode.setText("");
        try {
            Product p;
            try {
                p = ProductCache.getInstance().getProductByBarcode(barcode);
            } catch (JTillException | ProductNotFoundException ex) {
                LOG.log(Level.INFO, "Checking server for product {0}", barcode);
                p = dc.getProductByBarcode(barcode);
                LOG.log(Level.INFO, "Product was found on server");
                try {
                    final Plu pl = dc.getPluByProduct(p.getId());
                    ProductCache.getInstance().addProductAndPlu(p, pl);
                } catch (JTillException ex1) {
                    Logger.getLogger(MainStage.class.getName()).log(Level.SEVERE, null, ex1);
                }
            }
            addItemToSale(p);
        } catch (IOException | ProductNotFoundException | SQLException ex) {
            LOG.log(Level.WARNING, "Product not found on server");
            showMessageAlert(barcode + " not found", 2000);
        }
    }

    public void addItemToSale(Item i) {
        if (i instanceof Product) { //If the item is a product
            final Product p = (Product) i;
            if (!refundMode) {
                sale.notifyAllListeners(new ProductEvent(p), itemQuantity);
                Category cat = p.getCategory();
                if (cat.isTimeRestrict()) { //Check for time restrictions
                    final Calendar c = Calendar.getInstance();
                    final long now = c.getTimeInMillis();
                    c.set(Calendar.HOUR_OF_DAY, 1);
                    c.set(Calendar.MINUTE, 0);
                    c.set(Calendar.SECOND, 0);
                    c.set(Calendar.MILLISECOND, 0);
                    final long passed = now - c.getTimeInMillis();
                    if (!cat.isSellTime(new Time(passed))) { //If the item can not be sold now due to the time
                        MessageDialog.showMessage(this, "Time Restriction", "This item cannot be sold now");
                        return;
                    }
                }
                if (cat.getMinAge() > age) { //Check for age restrictions
                    if (YesNoDialog.showDialog(this, "Age Restriction", "Is customer over " + cat.getMinAge() + "?") == YesNoDialog.NO) {
                        return;
                    }
                    age = cat.getMinAge();
                }
            }
            if (p.isOpen()) { //Check if the product is open price
                int value;
                if (barcode.getText().equals("")) {
                    value = NumberEntry.showNumberEntryDialog(this, "Enter price"); //Show the dialog asking for the price
                } else {
                    final String bc = barcode.getText();
                    if (bc.contains(".") || !Utilities.isNumber(bc)) {
                        showMessageAlert("Illegal character", 2000L);
                        return;
                    }
                    value = Integer.parseInt(bc); //Get the price value from the input field
                    barcode.setText("");
                }
                if (value == 0) {
                    return; //Exit the method if nothing was entered
                }
                p.setPrice(new BigDecimal(Double.toString((double) value / 100)));
            }

            if (refundMode) {
                itemQuantity = -itemQuantity; //If in refund mode, set the quantity to negative.
            }
        } else { //If the item was a discount
            final Discount d = (Discount) i;
            //Check to see if it is a percentage discount for a price discount
            if (d.getAction() == Discount.PERCENTAGE_OFF) {
                //If it is a percentage discounts then the price to take of must be calculated
                d.setPrice(sale.getTotal().multiply(new BigDecimal(Double.toString(d.getPercentage() / 100)).negate()));
            } else {
                if (d.getPrice().doubleValue() > 0) {
                    d.setPrice(d.getPrice().negate());
                }
            }
        }
        final boolean inSale = sale.addItem(i, itemQuantity); //Add the item to the sale
        if (!inSale) {
            obTable.add(sale.getLastAdded());
            itemsTable.scrollTo(obTable.size() - 1);
            itemsTable.getSelectionModel().select(obTable.size() - 1);
            payObTable.add(sale.getLastAdded());
            paymentItemsTable.scrollTo(payObTable.size() - 1);
            paymentItemsTable.getSelectionModel().select(payObTable.size() - 1);
        } else {
            itemsTable.refresh();
            paymentItemsTable.refresh();
        }
        LOG.log(Level.INFO, "Item has been added to a sale");
        setTotalLabel();
        itemQuantity = 1;
        quantity.setText("Quantity: 1");
        setRefund(false);
    }

    private void setTotalLabel() {
        DecimalFormat df;
        if (sale.getTotal().compareTo(BigDecimal.ZERO) > 1) {
            df = new DecimalFormat("#.00");
        } else {
            df = new DecimalFormat("0.00");
        }
        total.setText("Total: " + symbol + df.format(sale.getTotal()));
        paymentTotal.setText("Total: " + symbol + df.format(sale.getTotal()));
        amountDue = sale.getTotal();
        totalItems.setText("Items: " + sale.getTotalItemCount());
    }

    private void setRefund(boolean set) {
        refundMode = set;
        if (refundMode) {
            mainRefund.setText("REFUND");
        } else {
            mainRefund.setText("");
        }
    }

    private void addScreens(List<Screen> screens) {
        LOG.log(Level.INFO, "Got {0} screens from the server", screens.size());

        try {
            def_screen = dc.getScreen(till.getDefaultScreen());
        } catch (IOException | SQLException | ScreenNotFoundException ex) {
            Logger.getLogger(MainStage.class.getName()).log(Level.SEVERE, null, ex);
        }

        for (Screen s : screens) {
            GridPane grid = setScreenButtons(s); //Set the buttons for that screen onto a new grid pane.
            if (s.getId() == def_screen.getId()) {
                def_screen.setPane(grid);
            }
            buttonPanes.add(grid); //Add the new grid pane to the main grid pane container.
        }
    }

    private GridPane setScreenButtons(Screen s) {
        GridPane grid = new GridPane(); //Create a new GridPane for this screen.
        grid.setId("productsgrid");
        try {
            LOG.log(Level.INFO, "Getting buttons for {0} screen", s.getName());
            List<TillButton> buttons = dc.getButtonsOnScreen(s); //Get all the buttons for this screen.

            LOG.log(Level.INFO, "Got {0} buttons for this screen", buttons.size());
            s.setPane(grid);

            for (int i = 1; i <= s.getWidth(); i++) {
                ColumnConstraints col = new ColumnConstraints();
                col.setPercentWidth(100D / s.getWidth());
                col.setFillWidth(true);
                col.setHgrow(Priority.ALWAYS);
                grid.getColumnConstraints().add(col);
            }

            for (int i = 1; i <= s.getHeight(); i++) {
                RowConstraints row = new RowConstraints();
                row.setPercentHeight(100D / s.getHeight());
                row.setFillHeight(true);
                row.setVgrow(Priority.ALWAYS);
                grid.getRowConstraints().add(row);
            }

            if (s.getInherits() != -1) {
                Screen parent = dc.getScreen(s.getInherits());
                List<TillButton> parB = dc.getButtonsOnScreen(parent);
                for (TillButton b : parB) {
                    if (b.getType() == TillButton.SPACE) { //If the button is a space, add en empty box.
                    } else { //If it is a button add a button.
                        Button button = new Button(b.getName()); //Create the button for this button.
                        button.wrapTextProperty().setValue(true);
                        switch (b.getColorValue()) {
                            case TillButton.BLUE:
                                button.setId("cBlue");
                                break;
                            case TillButton.RED:
                                button.setId("cRed");
                                break;
                            case TillButton.GREEN:
                                button.setId("cGreen");
                                break;
                            case TillButton.YELLOW:
                                button.setId("cYellow");
                                break;
                            case TillButton.ORANGE:
                                button.setId("cOrange");
                                break;
                            case TillButton.PURPLE:
                                button.setId("cPurple");
                                break;
                            case TillButton.WHITE:
                                button.setId("cWhite");
                                break;
                            case TillButton.BLACK:
                                button.setId("cBlack");
                                break;
                            default:
                                button.setId("productButton");
                                break;
                        }
                        int id = b.getItem();
                        try {
                            if (b.getType() == TillButton.ITEM) {
                                final Item i = dc.getProduct(id); //Get the item associated with this product.
                                button.setOnAction((ActionEvent e) -> {
                                    Platform.runLater(() -> {
                                        onProductButton(i); //When clicked, add the item to the sale.
                                        barcode.setText("");
                                        if (!barcode.isFocused()) {
                                            barcode.requestFocus();
                                        }
                                    });
                                });
                            } else if (b.getType() == TillButton.SCREEN) {
                                final Screen sc = getScreen(b.getItem());
                                if (sc == null) {
                                    throw new ScreenNotFoundException("Screen Missing");
                                }
                                button.setOnAction((ActionEvent e) -> {
                                    changeScreen(sc);
                                });
                            } else if (b.getType() == TillButton.BACK) {
                                button.setOnAction((ActionEvent e) -> {
                                    changeScreen(last_screen);
                                });
                            } else if (b.getType() == TillButton.MAIN) {
                                button.setOnAction((ActionEvent e) -> {
                                    changeScreen(def_screen);
                                });
                            }
                            button.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
                            GridPane.setFillHeight(button, true);
                            GridPane.setFillWidth(button, true);
                            grid.add(button, b.getX() - 1, b.getY() - 1, b.getWidth(), b.getHeight()); //Add the button to the grid.
                        } catch (IOException | ProductNotFoundException | SQLException | ScreenNotFoundException ex) {
                            Logger.getLogger(MainStage.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
            }
            //Add the buttons on top.
            for (TillButton b : buttons) {
                if (b.getType() == TillButton.SPACE) { //If the button is a space, add en empty box.
                } else { //If it is a button add a button.
                    Button button = new Button(b.getName()); //Create the button for this button.
                    button.wrapTextProperty().setValue(true);
                    switch (b.getColorValue()) {
                        case TillButton.BLUE:
                            button.setId("cBlue");
                            break;
                        case TillButton.RED:
                            button.setId("cRed");
                            break;
                        case TillButton.GREEN:
                            button.setId("cGreen");
                            break;
                        case TillButton.YELLOW:
                            button.setId("cYellow");
                            break;
                        case TillButton.ORANGE:
                            button.setId("cOrange");
                            break;
                        case TillButton.PURPLE:
                            button.setId("cPurple");
                            break;
                        case TillButton.WHITE:
                            button.setId("cWhite");
                            break;
                        case TillButton.BLACK:
                            button.setId("cBlack");
                            break;
                        default:
                            button.setId("productButton");
                            break;
                    }
                    int id = b.getItem();
                    try {
                        if (b.getType() == TillButton.ITEM) {
                            final Item i = dc.getProduct(id); //Get the item associated with this product.
                            button.setOnAction((ActionEvent e) -> {
                                Platform.runLater(() -> {
                                    onProductButton(i); //When clicked, add the item to the sale.
                                    barcode.setText("");
                                    if (!barcode.isFocused()) {
                                        barcode.requestFocus();
                                    }
                                });
                            });
                        } else if (b.getType() == TillButton.SCREEN) {
                            final Screen sc = getScreen(b.getItem());
                            if (sc == null) {
                                throw new ScreenNotFoundException("Screen Missing");
                            }
                            button.setOnAction((ActionEvent e) -> {
                                changeScreen(sc);
                            });
                        } else if (b.getType() == TillButton.BACK) {
                            button.setOnAction((ActionEvent e) -> {
                                changeScreen(last_screen);
                            });
                        } else if (b.getType() == TillButton.MAIN) {
                            button.setOnAction((ActionEvent e) -> {
                                changeScreen(def_screen);
                            });
                        }
                        button.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
                        GridPane.setFillHeight(button, true);
                        GridPane.setFillWidth(button, true);
                        grid.add(button, b.getX() - 1, b.getY() - 1, b.getWidth(), b.getHeight()); //Add the button to the grid.
                    } catch (IOException | ProductNotFoundException | SQLException | ScreenNotFoundException ex) {
                        Logger.getLogger(MainStage.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        } catch (IOException | SQLException | ScreenNotFoundException ex) {
            showErrorAlert(ex);
        }
        return grid;
    }

    private Screen getScreen(int id) {
        for (Screen s : screens) {
            if (s.getId() == id) {
                return s;
            }
        }
        return null;
    }

    private void onProductButton(Item i) {
        addItemToSale(i);
    }

    private void changeScreen(Screen sc) {
        last_screen = currentScreen;
        buttonPane.getChildren().clear();
        buttonPane.getChildren().add(sc.getPane());
        screenLabel.setText(sc.getName());
        lastScreen = sc.getName();
        if (!barcode.isFocused()) {
            barcode.requestFocus();
        }
        currentScreen = sc;
    }

    @Override
    public void log(Object o) {
    }

    @Override
    public void logWarning(Object o) {
    }

    @Override
    public void setClientLabel(String text) {
    }

    @Override
    public void showMessage(String title, String message) {
        MessageDialog.showMessage(this, title, message);
    }

    @Override
    public boolean showYesNoMessage(String title, String message) {
        return YesNoDialog.showDialog(this, title, message) == YesNoDialog.YES;
    }

    @Override
    public void addTill(Till t) {
    }

    @Override
    public void allow(Till t) {
        this.till = t;
        login.setDisable(false);
        this.getServerData();
        Platform.runLater(() -> {
            MessageScreen.hideWindow();
        });
    }

    @Override
    public void disallow() {
        showMessage("Not Allowed", "The server has not allowed this terminal to join");
    }

    @Override
    public void showModalMessage(String title, String message) {
        MessageScreen.changeMessage(message);
    }

    @Override
    public void hideModalMessage() {
        MessageScreen.hideWindow();
    }

    @Override
    public void updateTills() {
    }

    @Override
    public void connectionDrop() {
        Platform.runLater(() -> {
            final String temp = terminalName + " (Offline)";
            MainStage.this.mainVersion.setText(temp);
        });
    }

    @Override
    public void connectionReestablish() {
        sendSalesToServer();
        Platform.runLater(() -> {
            MainStage.this.mainVersion.setText(terminalName + (newData ? " (New Data)" : ""));
        });
    }

    @Override
    public void initTill() {
        Platform.runLater(() -> {
            MessageScreen.changeMessage("Initialising");
            MessageScreen.showWindow();
        });
//        MainStage.this.logoff();
        MainStage.this.getServerData();
        Platform.runLater(() -> {
            MessageScreen.hideWindow();
            loginNumber.requestFocus();
        });
        Platform.runLater(() -> {
            final String temp = terminalName;
            MainStage.this.mainVersion.setText(temp);
        });
        newData = false;
    }

    @Override
    public Till showTillSetupWindow(String name) {
        return null;
    }

    @Override
    public void renameTill(String name) {
        till.setName(name);
        JavaFXJTill.NAME = name;
        JavaFXJTill.saveProperties();
    }

    @Override
    public void logout() {
        this.logoff();
        Platform.runLater(() -> {
            MessageScreen.hideWindow();
        });
    }
}
