/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.davidg95.jtill.javafxjtill;

import io.github.davidg95.JTill.jtill.*;
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
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import io.github.davidg95.JTill.jtill.DataConnect;
import java.awt.print.PrinterAbortException;
import java.awt.print.PrinterException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.scene.shape.Box;
import javax.mail.MessagingException;

/**
 *
 * @author David
 */
public class MainStage extends Stage implements GUIInterface {

    private static final Logger LOG = Logger.getGlobal();

    private Staff staff;
    private Sale sale;
    private Sale lastSale;
    private int age;
    private int itemQuantity;
    private BigDecimal amountDue;
    private final DataConnect dc;
    private int MAX_SALES;
    private String symbol;

    public static boolean printOk;

    private final String stylesheet;

    //Login Scene Components
    private Scene loginScene;
    private GridPane loginPane;
    private Button exit;
    private Button login;
    private Button print;
    private Label loginMessage;
    private GridPane staffLayout;
    private Label loginTime;
    private Label notLoggedIn;
    private Label loginVersion;
    private int x = 0;
    private int y = 0;

    //Main Scene Components
    private TableView itemsTable;
    private ObservableList<SaleItem> obTable;
    private Scene mainScene;
    private GridPane mainPane;
    private Pane buttonPane;
    private List<GridPane> buttonPanes;
    private GridPane screenPane;
    private ToggleGroup screenButtonGroup;
    private Label staffLabel;
    private Label time;
    private Label total;
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

    //Payment Scene Components
    private Scene paymentScene;
    private GridPane paymentPane;
    private Button fivePounds;
    private Button tenPounds;
    private Button twentyPounds;
    private Button customValue;
    private Button exactValue;
    private Button card;
    private Button cheque;
    private Button addCustomer;
    private Label saleCustomer;
    private Label paymentTotal;
    private ListView<PaymentItem> paymentsList;
    private ObservableList<PaymentItem> obPayments;
    private Button discount;
    private Button chargeAccount;
    private Button settings;
    private Button voidItem;
    private Button voidSale;
    private Button cashUp;
    private Button clearLogins;
    private Button back;
    private Label paymentLoggedIn;
    private Label paymentVersion;
    private Label paymentTime;
    private Label paymentMessages;
    private Button paymentLogoff;
    private Button submitSales;
    private Button clockOff;

    private final double SCREEN_WIDTH = javafx.stage.Screen.getPrimary().getBounds().getWidth();
    private final double SCREEN_HEIGHT = javafx.stage.Screen.getPrimary().getBounds().getHeight();

    private final List<ProductListener> listeners;

    public MainStage(DataConnect dc) {
        super();
        this.dc = dc;
        if (staff == null) {
            this.sale = new Sale(JavaFXJTill.NAME, 0);

        } else {
            this.sale = new Sale(JavaFXJTill.NAME, staff.getId());
        }
        setTitle("JTill Terminal");
        stylesheet = MainStage.class.getResource("style.css").toExternalForm();
        listeners = new ArrayList<>();

    }

    public void initalise() {
        paymentLoggedIn = new Label();
        paymentLoggedIn.setId("toplabel");
        paymentLoggedIn.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
        init();
        initPayment();
        initLogin();
        mainScene.getStylesheets().add(stylesheet);
        paymentScene.getStylesheets().add(stylesheet);
        loginScene.getStylesheets().add(stylesheet);
        setScene(loginScene); //Show the login scene first
        initStyle(StageStyle.UNDECORATED);
        show();
        MessageScreen.changeMessage("Initialising");
        MessageScreen.showWindow();
        Platform.runLater(() -> {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException ex) {
                LOG.log(Level.SEVERE, null, ex);
            }
            boolean tryCon = true;
            while (tryCon) {
                try {
                    tryConnect();
                    MessageScreen.hideWindow();
                    tryCon = false;
                } catch (IOException ex) {
                    MessageScreen.hideWindow();
                    LOG.log(Level.WARNING, "Error connecting to the server");
                    if (YesNoDialog.showDialog(this, "Try Again?", "Do you want to try connect again?") == YesNoDialog.NO) {
                        LOG.log(Level.INFO, "Stopping JTill Terminal");
                        System.exit(0);
                    }
                }
            }
        });
    }

    private void tryConnect() throws IOException {
        JavaFXJTill.loadProperties();
        dc.setGUI(MainStage.this);
        if (dc instanceof ServerConnection) {
            ServerConnection sc = (ServerConnection) dc;
            LOG.log(Level.INFO, "Attempting connection to the server on IP address {0}", JavaFXJTill.SERVER);
            sc.connect(JavaFXJTill.SERVER, JavaFXJTill.PORT, JavaFXJTill.NAME);
        }
    }

    private void getServerData() {
        try {
            try {
                MAX_SALES = Integer.parseInt(dc.getSetting("MAX_CACHE_SALES"));
                LOG.log(Level.INFO, "Max sales set to {0}", MAX_SALES);
            } catch (IOException ex) {
                LOG.log(Level.SEVERE, null, ex);
            }
            try {
                if (dc.getSetting("SEND_PRODUCTS_START").equals("TRUE")) {
                    LOG.log(Level.INFO, "Downloading products list from server");
                    ProductCache.getInstance().setProducts(dc.getAllProducts());
                    LOG.log(Level.INFO, "Products list downloaded from server");
                }
            } catch (IOException | SQLException ex) {
                LOG.log(Level.SEVERE, null, ex);
            }
            try {
                symbol = dc.getSetting("CURRENCY_SYMBOL");
            } catch (IOException ex) {
                LOG.log(Level.WARNING, "Could not get currency symbol from server", ex);
                symbol = "£";
            }
            try {
                String siteName = "JTill Terminal - " + dc.getSetting("SITE_NAME");
                loginVersion.setText(siteName);
                mainVersion.setText(siteName);
                paymentVersion.setText(siteName);
            } catch (IOException ex) {
                LOG.log(Level.WARNING, "Could not get site name", ex);
            }
            ((TableColumn) itemsTable.getColumns().get(2)).setText(symbol);
            twentyPounds.setText(symbol + "20");
            tenPounds.setText(symbol + "10");
            fivePounds.setText(symbol + "5");
            DiscountCache.getInstance().setDiscounts(dc.getAllDiscounts(), this);
            LOG.log(Level.INFO, "Loading screen and button configurations from the server");
            List<Screen> screens = dc.getAllScreens();
            addScreens(screens, screenPane);
        } catch (IOException | SQLException ex) {
            MessageDialog.showMessage(this, "Error", ex.getMessage());
        }
    }

    private void init() {
        mainPane = new GridPane();

        staffLabel = new Label("Staff: ");
        staffLabel.setId("toplabel");
        staffLabel.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));

        mainVersion = new Label("JTill Terminal");
        mainVersion.setId("toplabel");
        mainVersion.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));

        time = new Label("--:-- --/--/----");
        time.setId("toplabel");
        time.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
        ClockThread.addClockLabel(time);
        time.setAlignment(Pos.CENTER_RIGHT);

        buttonPanes = new ArrayList<>();
        buttonPane = new StackPane();
        screenPane = new GridPane();
        screenButtonGroup = new ToggleGroup();
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

        quantity = new Button("Quantity: 1");
        quantity.setId("red");
        quantity.setMinSize(0, 0);
        quantity.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        itemQuantity = 1;
        quantity.setOnAction((ActionEvent event) -> {
            if (barcode.getText().equals("")) {
                int val = NumberEntry.showNumberEntryDialog(this, "Enter Quantity", itemQuantity);
                if (val != 0) {
                    itemQuantity = val;
                }
            } else {
                itemQuantity = Integer.parseInt(barcode.getText());
                barcode.setText("");
            }
            quantity.setText("Quantity: " + itemQuantity);
        });

        voidSelected = new Button("Void Selected");
        voidSelected.setId("red");
        voidSelected.setMinSize(0, 0);
        voidSelected.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        voidSelected.setOnAction((ActionEvent event) -> {
            if (itemsTable.getSelectionModel().getSelectedIndex() > -1) {
                sale.voidItem((SaleItem) itemsTable.getSelectionModel().getSelectedItem());
                obTable.remove((SaleItem) itemsTable.getSelectionModel().getSelectedItem());
                setTotalLabel();
            }
        });

        barcode = new TextField();
        barcode.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        barcode.setFont(Font.font("Tahoma", FontWeight.NORMAL, 25));
        barcode.setOnAction((ActionEvent event) -> {
            Platform.runLater(() -> {
                if (!barcode.getText().equals("")) {
                    getProductByBarcode(barcode.getText());
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
            setScene(paymentScene);
        });

        logoff = new Button("Logoff");
        logoff.setId("bottom");
        logoff.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        logoff.setMinSize(0, 0);
        logoff.setOnAction((ActionEvent event) -> {
            Platform.runLater(this::logoff);
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
        });

        assisstance = new Button("Assisstance");
        assisstance.setId("bottom");
        assisstance.setMinSize(0, 0);
        assisstance.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        assisstance.setOnAction((ActionEvent event) -> {
            String message = EntryDialog.show(this, "Assisstance", "Enter message");
            try {
                dc.assisstance(message);
                LOG.log(Level.INFO, "Assisstance message sent to server");
                showMessageAlert("Message Sent", 2000);
            } catch (IOException ex) {
                MessageDialog.showMessage(this, "Assisstance", ex.getMessage());
            }
        });

        alertMessage = new Label();
        alertMessage.setFont(Font.font("Tahoma", FontWeight.NORMAL, 40));
        alertMessage.setId("message");
        alertMessage.setMinSize(0, 0);
        alertMessage.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

        mainPane.add(staffLabel, 0, 0, 2, 1);
        mainPane.add(mainVersion, 3, 0, 3, 1);
        mainPane.add(time, 8, 0, 2, 1);
        mainPane.add(buttonPane, 0, 1, 8, 11);
        mainPane.add(screenPane, 0, 12, 8, 2);
        mainPane.add(itemsTable, 8, 1, 2, 5);
        mainPane.add(total, 8, 6, 2, 1);
        mainPane.add(quantity, 8, 7);
        mainPane.add(voidSelected, 9, 7);
        mainPane.add(barcode, 8, 8, 2, 1);
        mainPane.add(numbers, 8, 9, 2, 5);
        mainPane.add(payment, 8, 14, 2, 2);
        mainPane.add(halfPrice, 2, 14, 1, 2);
        mainPane.add(logoff, 0, 14, 1, 2);
        mainPane.add(lookup, 1, 14, 1, 2);
        mainPane.add(assisstance, 3, 14, 1, 2);
        mainPane.add(alertMessage, 4, 14, 4, 2);

        for (int i = 1; i <= 10; i++) {
            ColumnConstraints col = new ColumnConstraints();
            col.setPercentWidth(10);
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

        mainScene = new Scene(mainPane, SCREEN_WIDTH, SCREEN_HEIGHT);
    }

    private GridPane createNumbersPane() {
        GridPane grid = new GridPane();

        Button clear = new Button("clr");
        clear.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        HBox hClear = new HBox(0);
        hClear.getChildren().add(clear);
        clear.setOnAction((ActionEvent event) -> {
            barcode.setText("");
        });

        Button seven = new Button("7");
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
        HBox hEnter = new HBox(0);
        enter.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        hEnter.getChildren().add(enter);

        enter.setOnAction((ActionEvent event) -> {
            if (!barcode.getText().equals("")) {
                getProductByBarcode(barcode.getText());
                barcode.setText("");
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

        paymentVersion = new Label("JTill Terminal");
        paymentVersion.setId("toplabel");
        paymentVersion.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));

        paymentTime = new Label("--:-- --/--/----");
        paymentTime.setId("toplabel");
        paymentTime.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
        ClockThread.addClockLabel(paymentTime);
        paymentTime.setAlignment(Pos.CENTER_RIGHT);

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
                    if (sale.getCustomer() != 0) {
                        setCustomer(null);
                        chargeAccount.setDisable(true);
                        addCustomer.setText("Add Customer");
                        return;
                    }
                    Customer c = CustomerSelectDialog.showDialog(MainStage.this, dc, "Search for Customer");
                    if (c != null) {
                        setCustomer(c);
                        chargeAccount.setDisable(false);
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
            sale.setChargeAccount(true);
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
            setScene(mainScene);
        });

        paymentsList = new ListView<>();
        paymentsList.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        obPayments = FXCollections.observableArrayList();
        paymentsList.setItems(obPayments);
        paymentsList.setId("PAYMENT_LIST");

        paymentTotal = new Label("Total: " + symbol + sale.getTotal().toString());
        paymentTotal.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        paymentTotal.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));

        voidItem = new Button("Void");
        voidItem.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        voidItem.setOnAction((ActionEvent event) -> {
            if (paymentsList.getSelectionModel().getSelectedIndex() > -1) {
                PaymentItem pi = paymentsList.getSelectionModel().getSelectedItem().clone();
                obPayments.remove(paymentsList.getSelectionModel().getSelectedIndex());
                paymentsList.refresh();
                addMoney(pi.getType(), pi.getValue().negate());
            }
        });

        voidSale = new Button("Void Sale");
        voidSale.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        voidSale.setOnAction((ActionEvent event) -> {
            if (YesNoDialog.showDialog(this, "Void Sale", "Are you sure you want to void the sale?") == YesNoDialog.YES) {
                newSale();
            }
        });

        discount = new Button("Discounts");
        discount.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        discount.setOnAction((ActionEvent event) -> {
            Discount d = DiscountSelectDialog.showDialog(this, dc);
            if (d != null) {
                d.setPrice(sale.getTotal().multiply(new BigDecimal(Double.toString(d.getPercentage() / 100)).negate()));
                boolean inSale = sale.addItem(d, 1);
                if (!inSale) {
                    obTable.add(sale.getLastAdded());
                    itemsTable.scrollTo(obTable.size() - 1);
                } else {
                    itemsTable.refresh();
                }
                setTotalLabel();
                itemsTable.refresh();
            }
        });

        settings = new Button("Settings");
        settings.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        settings.setOnAction((ActionEvent event) -> {
            if (staff.getPosition() >= 3) {
                SetupDialog.showDialog(MainStage.this);
                MessageDialog.showMessage(this, "Settings", "Some changes will apply after restart");
            } else {
                MessageDialog.showMessage(this, "Settings", "You are not allowed to view this screen");
            }
        });

        cashUp = new Button("Cash Up");
        cashUp.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        cashUp.setOnAction((ActionEvent event) -> {
            if (staff.getPosition() >= 3) {
                LOG.log(Level.INFO, "Submitting all sales to the server");
                sendSalesToServer();
                SaleCache.getInstance().clearAll();
                CashUpDialog.showDialog(this, dc);
                clearLoginScreen();
                logoff();
            } else {
                MessageDialog.showMessage(this, "Cash Up", "You are not allowed to view this screen");
            }
        });

        clearLogins = new Button("Clear Login Screen");
        clearLogins.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        clearLogins.setOnAction((ActionEvent event) -> {
            if (staff.getPosition() >= 2) {
                clearLoginScreen();
                MessageDialog.showMessage(this, "Login Screen", "Staff cleared from login screen");
            } else {
                MessageDialog.showMessage(this, "Login Screen", "You are not allowed to do this");
            }
        });

        submitSales = new Button("Submit all sales");
        submitSales.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        submitSales.setOnAction((ActionEvent event) -> {
            sendSalesToServer();
        });

        paymentLogoff = new Button("Logoff");
        paymentLogoff.setId("bottom");
        paymentLogoff.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        paymentLogoff.setOnAction((ActionEvent event) -> {
            Platform.runLater(this::logoff);
        });
        paymentLogoff.setStyle("-fx-base: #0000FF;");

        paymentMessages = new Label();
        paymentMessages.setFont(Font.font("Tahoma", FontWeight.NORMAL, 40));
        paymentMessages.setId("message");
        paymentMessages.setMinSize(0, 0);
        paymentMessages.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

        clockOff = new Button("Clock Off");
        clockOff.setId("bottom");
        clockOff.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        clockOff.setOnAction((ActionEvent event) -> {
            try {
                dc.clockOff(staff.getId());
                MessageDialog.showMessage(this, "Clock Off", "Clocked off at " + new SimpleDateFormat("HH:mm:ss dd/MM/yyyy").format(new Date()));
            } catch (IOException | SQLException | StaffNotFoundException ex) {
                Logger.getLogger(MainStage.class.getName()).log(Level.SEVERE, null, ex);
            }
        });

        paymentPane.add(paymentLoggedIn, 0, 0, 2, 1);
        paymentPane.add(paymentVersion, 3, 0, 3, 1);
        paymentPane.add(paymentTime, 8, 0, 2, 1);
        paymentPane.add(fivePounds, 0, 1, 1, 2);
        paymentPane.add(tenPounds, 1, 1, 1, 2);
        paymentPane.add(twentyPounds, 2, 1, 1, 2);
        paymentPane.add(customValue, 0, 3, 1, 2);
        paymentPane.add(exactValue, 1, 3, 1, 2);
        paymentPane.add(card, 2, 3, 1, 2);
        paymentPane.add(addCustomer, 0, 5, 1, 2);
        paymentPane.add(chargeAccount, 1, 5, 1, 2);
        paymentPane.add(cheque, 2, 5, 1, 2);
        paymentPane.add(saleCustomer, 8, 7, 2, 1);
        paymentPane.add(back, 8, 14, 2, 2);
        paymentPane.add(paymentsList, 8, 1, 2, 5);
        paymentPane.add(paymentTotal, 8, 6, 2, 1);
        paymentPane.add(voidItem, 7, 1, 1, 2);
        paymentPane.add(voidSale, 6, 1, 1, 2);
        paymentPane.add(discount, 5, 1, 1, 2);
        paymentPane.add(settings, 7, 3, 1, 2);
        paymentPane.add(cashUp, 6, 3, 1, 2);
        paymentPane.add(clearLogins, 5, 3, 1, 2);
        paymentPane.add(submitSales, 5, 5, 1, 2);
        paymentPane.add(paymentMessages, 4, 14, 4, 2);
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

        paymentScene = new Scene(paymentPane, SCREEN_WIDTH, SCREEN_HEIGHT);
    }

    private void initLogin() {
        loginPane = new GridPane();

        staffLayout = new GridPane();
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
            row.setPrefHeight(staffLayout.getHeight() / 2);
            row.setFillHeight(true);
            row.setVgrow(Priority.ALWAYS);
            staffLayout.getRowConstraints().add(row);
        }

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

        notLoggedIn = new Label("Not Logged In");
        notLoggedIn.setId("toplabel");
        notLoggedIn.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));

        loginVersion = new Label("JTill Terminal");
        loginVersion.setId("toplabel");
        loginVersion.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));

        loginTime = new Label("--:-- --/--/----");
        loginTime.setId("toplabel");
        loginTime.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
        ClockThread.addClockLabel(loginTime);
        ClockThread.setFormat(ClockThread.DATE_TIME_FORMAT);

        exit = new Button("Exit JTill");
        exit.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        HBox hExit = new HBox(0);
        hExit.getChildren().add(exit);
        exit.setOnAction((ActionEvent event) -> {
            Platform.runLater(() -> {
                if (dc.isConnected()) {
                    try {
                        dc.close();
                    } catch (IOException ex) {

                    }
                }
                System.exit(0);
            });
        });

        login = new Button("Login");
        login.setDisable(true);
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
                    Staff s = dc.getStaff(val);
                    dc.clockOn(s.getId());
                    Button button = new Button(s.getName());
                    button.prefWidthProperty().bind(staffLayout.widthProperty().divide(4));
                    button.prefHeightProperty().bind(staffLayout.heightProperty().divide(2));
                    button.setOnAction((ActionEvent evt) -> {
                        try {
                            MainStage.this.staff = s;
                            dc.tillLogin(s.getId());
                            Sale rs = dc.resumeSale(s);
                            if (rs != null) {
                                MainStage.this.sale = rs;
                                obTable.setAll(rs.getSaleItems());
                                setTotalLabel();
                                try {
                                    final Customer c = dc.getCustomer(rs.getCustomer());
                                    setCustomer(c);
                                } catch (CustomerNotFoundException ex) {
                                    Logger.getLogger(MainStage.class.getName()).log(Level.SEVERE, null, ex);
                                }
                            } else {
                                newSale();
                            }
                            staffLabel.setText("Staff: " + s.getName());
                            paymentLoggedIn.setText("Staff: " + s.getName());
                            if (!buttonPanes.isEmpty()) {
                                buttonPane.getChildren().clear();
                                buttonPane.getChildren().add(buttonPanes.get(0));
                            }
                            setScene(mainScene);
                        } catch (LoginException | SQLException ex) {
                            MessageDialog.showMessage(MainStage.this, "Log on", ex.getMessage());
                        } catch (IOException ex) {
                            MessageDialog.showMessage(MainStage.this, "Error", "Server offline");
                        }
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

        loginPane.add(staffLayout, 2, 2, 8, 12);
        loginPane.add(exit, 0, 14, 1, 2);
        loginPane.add(login, 1, 14, 1, 2);
        loginPane.add(print, 2, 14, 1, 2);
        loginPane.add(loginMessage, 4, 14, 4, 2);
        loginPane.add(loginTime, 8, 0, 2, 1);
        loginPane.add(notLoggedIn, 0, 0, 2, 1);
        loginPane.add(loginVersion, 3, 0, 3, 1);

        loginScene = new Scene(loginPane, SCREEN_WIDTH, SCREEN_HEIGHT);
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
        } else {
            saleCustomer.setText("No Customer");
            addCustomer.setText("Add Customer");
            chargeAccount.setDisable(true);
        }
        sale.setCustomer(c.getId());
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
        List<Sale> sales = SaleCache.getInstance().getAllSales();
        sales.forEach((s) -> {
            try {
                s = dc.addSale(s);
                LOG.log(Level.INFO, "Sale {0} has been sent to the server", s.getId());
            } catch (IOException | SQLException ex) {
                LOG.log(Level.SEVERE, null, ex);
            }
        });
        SaleCache.getInstance().clearAll();
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
        try {
            Sale s = dc.addSale(sale);
            LOG.log(Level.INFO, "Sale {0} sent to server", s.getId());
            if (YesNoDialog.showDialog(this, "Email Receipt", "Email Customer Receipt?") == YesNoDialog.YES) {
                if (sale.getCustomer() != 0) {
                    try {
                        final Customer c = dc.getCustomer(sale.getCustomer());
                        dc.emailReceipt(c.getEmail(), sale);
                    } catch (CustomerNotFoundException ex) {
                        this.showMessage("Email", ex.getMessage());
                    }
                } else {
                    String email = EntryDialog.show(this, "Email Receipt", "Enter email");
                    dc.emailReceipt(email, sale);
                }
            }
        } catch (IOException | SQLException ex) {
            LOG.log(Level.WARNING, "Error connecting to server");
            SaleCache.getInstance().addSale(sale);
            sale.setId(0);
        } catch (MessagingException ex) {
            MessageDialog.showMessage(this, "Error", ex);
        }

        try {
            if (dc.getSetting("AUTO_LOGOUT").equals("TRUE")) {
                try {
                    dc.tillLogout(staff);
                } catch (StaffNotFoundException ex) {
                    Logger.getLogger(MainStage.class.getName()).log(Level.SEVERE, null, ex);
                }
                newSale();
                setScene(loginScene);
            } else {
                newSale();
                setScene(mainScene);
            }
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, null, ex);
            newSale();
            setScene(mainScene);
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
    private void logoff() {
        try {
            dc.tillLogout(staff);
            if (!sale.getSaleItems().isEmpty()) {
                dc.suspendSale(sale, staff);
            }
        } catch (IOException | StaffNotFoundException ex) {

        }
        staff = null;
        newSale();
        setScene(loginScene);
    }

    /**
     * Create a new sale.
     */
    private void newSale() {
        if (staff == null) {
            sale = new Sale(JavaFXJTill.NAME, 0);
        } else {
            sale = new Sale(JavaFXJTill.NAME, staff.getId());
        }
        sale.setCustomer(1);
        obTable = FXCollections.observableArrayList();
        obPayments = FXCollections.observableArrayList();
        paymentsList.setItems(obPayments);
        updateList();
        total.setText("Total: " + symbol + "0.00");
        paymentTotal.setText("Total: " + symbol + "0.00");
        itemQuantity = 1;
        quantity.setText("Quantity: 1");
        saleCustomer.setText("No Customer");
        addCustomer.setText("Add Customer");
        chargeAccount.setDisable(true);
        age = 0;
    }

    private void updateList() {
        itemsTable.setItems(obTable);
    }

    private void getProductByBarcode(String barcode) {
        try {
            Product p;
            try {
                p = ProductCache.getInstance().getProductByBarcode(barcode);
            } catch (JTillException | ProductNotFoundException ex) {
                LOG.log(Level.INFO, "Checking server for product {0}", barcode);
                p = dc.getProductByBarcode(barcode);
                LOG.log(Level.INFO, "Product was found on server");
                ProductCache.getInstance().addProductToCache(p);
            }
            addItemToSale(p);
        } catch (IOException | ProductNotFoundException | SQLException ex) {
            LOG.log(Level.WARNING, "Product not found on server");
            showMessageAlert(barcode + " not found", 2000);
        }
    }

    public void addItemToSale(Item i) {
        if (i instanceof Product) { //If the item is a product
            Product p = (Product) i;
            notifyAllListeners(new ProductEvent(p));
            try {
                Category cat = dc.getCategory(p.getCategory());
                if (cat.isTimeRestrict()) { //Check for time restrictions
                    Calendar c = Calendar.getInstance();
                    long now = c.getTimeInMillis();
                    c.set(Calendar.HOUR_OF_DAY, 1);
                    c.set(Calendar.MINUTE, 0);
                    c.set(Calendar.SECOND, 0);
                    c.set(Calendar.MILLISECOND, 0);
                    long passed = now - c.getTimeInMillis();
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
                if (p.isOpen()) { //Check if the product is open price
                    int value;
                    if (barcode.getText().equals("")) {
                        value = NumberEntry.showNumberEntryDialog(this, "Enter price"); //Show the dialog asking for the price
                    } else {
                        value = Integer.parseInt(barcode.getText()); //Get the price value from the input field
                        barcode.setText("");
                    }
                    if (value == 0) {
                        return; //Exit the method if nothing was entered
                    }
                    p.setPrice(new BigDecimal(Double.toString((double) value / 100)));
                }
            } catch (IOException | SQLException | CategoryNotFoundException ex) {
                Logger.getLogger(MainStage.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else { //If the item was a discount
            Discount d = (Discount) i;
            d.setPrice(sale.getTotal().multiply(new BigDecimal(Double.toString(d.getPercentage() / 100)).negate()));
        }
        boolean inSale = sale.addItem(i, itemQuantity); //Add the item to the sale
        if (!inSale) {
            obTable.add(sale.getLastAdded());
            itemsTable.scrollTo(obTable.size() - 1);
        } else {
            itemsTable.refresh();
        }
        LOG.log(Level.INFO, "Item has been added to a sale");
        setTotalLabel();
        itemQuantity = 1;
        quantity.setText("Quantity: 1");
    }

    public void addListener(ProductListener pl) {
        listeners.add(pl);
    }

    private void notifyAllListeners(ProductEvent pe) {
        listeners.forEach((pl) -> {
            new Thread() {
                @Override
                public void run() {
                    pl.onProductAdd(pe);
                }
            }.start();
        });
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
    }

    private void addScreens(List<Screen> screens, GridPane pane) {
        int xPos = 0;
        int yPos = 0;

        LOG.log(Level.INFO, "Got {0} screens from the server", screens.size());

        for (Screen s : screens) {
            GridPane grid = new GridPane();
            ToggleButton button = new ToggleButton(s.getName());
            button.setToggleGroup(screenButtonGroup);
            button.setId("screenButton");
            button.prefWidthProperty().bind(pane.widthProperty().divide(4));
            button.prefHeightProperty().bind(pane.heightProperty().divide(2));
            pane.add(button, xPos, yPos);
            setScreenButtons(s, grid);
            buttonPanes.add(grid);
            button.setOnAction((ActionEvent event) -> {
                buttonPane.getChildren().clear();
                buttonPane.getChildren().add(grid);
            });
            xPos++;
            if (xPos == 4) {
                xPos = 0;
                yPos++;
            }
        }
    }

    private void setScreenButtons(Screen s, GridPane pane) {
        try {
            LOG.log(Level.INFO, "Getting buttons for {0} screen", s.getName());
            List<TillButton> buttons = dc.getButtonsOnScreen(s);
            addButtons(buttons, pane);
        } catch (IOException | SQLException | ScreenNotFoundException ex) {
            showErrorAlert(ex);
        }
    }

    private void addButtons(List<TillButton> buttons, GridPane grid) {
        int xPos = 0;
        int yPos = 0;

        LOG.log(Level.INFO, "Got {0} buttons for this screen", buttons.size());

        for (TillButton b : buttons) {
            if (b.getName().equals("[SPACE]")) {
                Box box = new Box();
                box.widthProperty().bind(grid.widthProperty().divide(5));
                box.heightProperty().bind(grid.heightProperty().divide(10));
                grid.add(box, xPos, yPos);
            } else {
                Button button = new Button(b.getName());
                button.setId("productButton");
                button.prefWidthProperty().bind(grid.widthProperty().divide(5));
                button.prefHeightProperty().bind(grid.heightProperty().divide(10));
                button.setOnAction((ActionEvent e) -> {
                    Item i = b.getItem().clone();
                    Platform.runLater(() -> {
                        onProductButton(i);
                    });
                });
                grid.add(button, xPos, yPos);
            }

            xPos++;
            if (xPos == 5) {
                xPos = 0;
                yPos++;
            }
        }

        for (int i = 1; i <= 5; i++) {
            ColumnConstraints col = new ColumnConstraints();
            col.setFillWidth(true);
            col.setHgrow(Priority.ALWAYS);
            mainPane.getColumnConstraints().add(col);
        }

        for (int i = 1; i <= 10; i++) {
            RowConstraints row = new RowConstraints();
            row.setFillHeight(true);
            row.setVgrow(Priority.ALWAYS);
            mainPane.getRowConstraints().add(row);
        }
    }

    private void onProductButton(Item i) {
        addItemToSale(i);
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
    public void allow() {
        login.setDisable(false);
        this.getServerData();
    }

    @Override
    public void disallow() {
        showMessage("Not Allowed", "The server has not allowed this terminal to join");
    }

    @Override
    public void showModalMessage(String title, String message) {
        MessageScreen.changeMessage(message);
        MessageScreen.showWindow();
    }

    @Override
    public void hideModalMessage() {
        MessageScreen.hideWindow();
    }

    @Override
    public void updateTills() {
    }
}
