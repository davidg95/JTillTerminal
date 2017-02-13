/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.davidg95.jtill.javafxjtill;

import io.github.davidg95.JTill.jtill.Customer;
import io.github.davidg95.JTill.jtill.DataConnectInterface;
import io.github.davidg95.JTill.jtill.Discount;
import io.github.davidg95.JTill.jtill.LoginException;
import io.github.davidg95.JTill.jtill.Product;
import io.github.davidg95.JTill.jtill.ProductNotFoundException;
import io.github.davidg95.JTill.jtill.Sale;
import io.github.davidg95.JTill.jtill.SaleItem;
import io.github.davidg95.JTill.jtill.Screen;
import io.github.davidg95.JTill.jtill.ScreenNotFoundException;
import io.github.davidg95.JTill.jtill.Staff;
import io.github.davidg95.JTill.jtill.StaffNotFoundException;
import io.github.davidg95.JTill.jtill.TillButton;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.sql.Time;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
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
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 *
 * @author David
 */
public class MainStage extends Stage {

    private Staff staff;
    private Sale sale;
    private int itemQuantity;
    private BigDecimal amountDue;
    private final DataConnectInterface dc;

    private final String stylesheet;

    //Login Scene Components
    private Scene loginScene;
    private BorderPane loginPane;
    private Button exit;
    private Button login;

    //Main Scene Components
    private TableView itemsTable;
    private ObservableList<SaleItem> obTable;
    private Scene mainScene;
    private GridPane mainPane;
    private Pane buttonPane;
    private List<FlowPane> buttonPanes;
    private FlowPane screenPane;
    private ToggleGroup screenButtonGroup;
    private Label staffLabel;
    private Label time;
    private Text total;
    private Button logoff;
    private Button quantity;
    private Button voidSelected;
    private TextField barcode;
    private Button payment;
    private Button lookup;
    private Button halfPrice;
    private Button assisstance;

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

    public MainStage(DataConnectInterface dc) {
        super();
        this.dc = dc;
        this.sale = new Sale(JavaFXJTill.NAME, staff);
        setTitle("JTill Terminal");
        stylesheet = MainStage.class.getResource("style.css").toExternalForm();
        //Created the scenes
        init();
        initPayment();
        initLogin();
        //Set the stylesheets
        mainScene.getStylesheets().add(stylesheet);
        paymentScene.getStylesheets().add(stylesheet);
        loginScene.getStylesheets().add(stylesheet);
        setScene(loginScene); //Show the login scene first
        initStyle(StageStyle.UNDECORATED);
        show();
    }

    private void init() {
        try {
            mainPane = new GridPane();

            staffLabel = new Label("Staff: ");
            staffLabel.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));

            time = new Label("00:00");
            time.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
            ClockThread.setClockLabel(time);
            time.setAlignment(Pos.CENTER_RIGHT);

            List<Screen> screens = dc.getAllScreens();
            buttonPanes = new ArrayList<>();
            buttonPane = new StackPane();
            screenPane = new FlowPane();
            screenPane.setPrefWrapLength(750);
            screenButtonGroup = new ToggleGroup();
            addScreens(screens, screenPane);
            buttonPane.getChildren().clear();
            if (!buttonPanes.isEmpty()) {
                buttonPane.getChildren().add(buttonPanes.get(0));
            }

            itemsTable = new TableView();
            itemsTable.setEditable(false);
            itemsTable.setMinSize(240, 250);
            itemsTable.setMaxSize(240, 250);
            TableColumn qty = new TableColumn("Qty.");
            TableColumn itm = new TableColumn("Item");
            TableColumn cst = new TableColumn("£");
            qty.setCellValueFactory(new PropertyValueFactory<>("quantity"));
            itm.setCellValueFactory(new PropertyValueFactory<>("item"));
            cst.setCellValueFactory(new PropertyValueFactory<>("price"));
            qty.setMaxWidth(40);
            qty.setMinWidth(40);
            itm.setMaxWidth(150);
            itm.setMinWidth(150);
            cst.setMaxWidth(50);
            cst.setMinWidth(50);
            itemsTable.getColumns().addAll(qty, itm, cst);
            HBox hTable = new HBox();
            hTable.getChildren().add(itemsTable);
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

            total = new Text("Total: £0.00");
            total.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));

            quantity = new Button("Quantity: 1");
            quantity.setMinSize(120, 50);
            quantity.setMaxSize(120, 50);
            itemQuantity = 1;
            HBox hQuantity = new HBox(0);
            hQuantity.getChildren().add(quantity);
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
            voidSelected.setMinSize(120, 50);
            voidSelected.setMaxSize(120, 50);
            HBox hVoid = new HBox(0);
            hVoid.getChildren().add(voidSelected);
            voidSelected.setOnAction((ActionEvent event) -> {
                if (itemsTable.getSelectionModel().getSelectedIndex() > -1) {
                    sale.voidItem((SaleItem) itemsTable.getSelectionModel().getSelectedItem());
                    obTable.remove((SaleItem) itemsTable.getSelectionModel().getSelectedItem());
                    setTotalLabel();
                }
            });

            barcode = new TextField();
            barcode.setMinSize(240, 50);
            barcode.setMaxSize(240, 50);
            barcode.setFont(Font.font("Tahoma", FontWeight.NORMAL, 25));
            barcode.setOnAction((ActionEvent event) -> {
                Platform.runLater(() -> {
                    getProductByBarcode(barcode.getText());
                    barcode.setText("");
                });
            });

            GridPane numbers = createNumbersPane();

            payment = new Button("Payment");
            payment.setId("payment");
            payment.setMinSize(240, 100);
            payment.setMaxSize(240, 100);
            HBox hPayment = new HBox(0);
            hPayment.getChildren().add(payment);
            payment.setOnAction((ActionEvent event) -> {
                setScene(paymentScene);
            });

            logoff = new Button("Logoff");
            logoff.setId("logoff");
            logoff.setMaxSize(100, 100);
            logoff.setMinSize(100, 100);
            HBox hLogoff = new HBox(0);
            hLogoff.getChildren().add(logoff);
            logoff.setOnAction((ActionEvent event) -> {
                Platform.runLater(this::logoff);
            });
            logoff.setStyle("-fx-base: #0000FF;");

            lookup = new Button("Lookup");
            lookup.setId("lookup");
            lookup.setMaxSize(100, 100);
            lookup.setMinSize(100, 100);
            HBox hLookup = new HBox(0);
            hLookup.getChildren().add(lookup);
            lookup.setOnAction((ActionEvent event) -> {
                Product p = ProductSelectDialog.showDialog(this, dc);
                if (p != null) {
                    addItemToSale(p);
                }
            });

            halfPrice = new Button("Half Price");
            halfPrice.setId("halfprice");
            halfPrice.setMinSize(100, 100);
            halfPrice.setMaxSize(100, 100);
            HBox hHalfPrice = new HBox(0);
            hHalfPrice.getChildren().add(halfPrice);
            halfPrice.setOnAction((ActionEvent event) -> {
                if (itemsTable.getSelectionModel().getSelectedIndex() > -1) {
                    SaleItem item = (SaleItem) itemsTable.getSelectionModel().getSelectedItem();
                    if (!(item.getItem() instanceof Discount)) {
                        sale.halfPriceItem(item);
                        setTotalLabel();
                        itemsTable.refresh();
                    } else {
                        MessageDialog.showMessage(this, "Hald Price", "Item not discountable");
                    }
                }
            });

            assisstance = new Button("Assisstance");
            assisstance.setId("assisstance");
            assisstance.setMinSize(100, 100);
            assisstance.setMaxSize(100, 100);
            HBox hAssisstance = new HBox(0);
            hAssisstance.getChildren().add(assisstance);
            assisstance.setOnAction((ActionEvent event) -> {
                String message = EntryDialog.show(this, "Assisstance", "Enter message");
                try {
                    dc.assisstance(message);
                    MessageDialog.showMessage(this, "Assisstance", "Message sent");
                } catch (IOException ex) {
                    MessageDialog.showMessage(this, "Assisstance", ex.getMessage());
                }
            });

            mainPane.add(staffLabel, 0, 0, 2, 1);
            mainPane.add(time, 6, 0, 2, 1);
            mainPane.add(buttonPane, 0, 1, 5, 10);
            mainPane.add(screenPane, 0, 11, 5, 2);
            mainPane.add(itemsTable, 6, 1, 2, 5);
            mainPane.add(total, 6, 6, 2, 1);
            mainPane.add(hQuantity, 6, 7);
            mainPane.add(hVoid, 7, 7);
            mainPane.add(barcode, 6, 8, 2, 1);
            mainPane.add(numbers, 6, 9, 2, 3);
            mainPane.add(hPayment, 6, 19, 2, 2);
            mainPane.add(hHalfPrice, 2, 19);
            mainPane.add(hLogoff, 0, 19);
            mainPane.add(hLookup, 1, 19);
            mainPane.add(hAssisstance, 3, 19);

            mainScene = new Scene(mainPane, 1024, 768);
        } catch (IOException | SQLException ex) {
            showErrorAlert(ex);
        }
    }

    private GridPane createNumbersPane() {
        final int bWidth = 80;
        final int bHeight = 50;
        GridPane grid = new GridPane();

        Button clear = new Button("clr");
        clear.setMinSize(bWidth, bHeight);
        clear.setMaxSize(bWidth, bHeight);
        HBox hClear = new HBox(0);
        hClear.getChildren().add(clear);
        clear.setOnAction((ActionEvent event) -> {
            barcode.setText("");
        });

        Button seven = new Button("7");
        HBox hSeven = new HBox(0);
        seven.setMaxSize(bWidth, bHeight);
        seven.setMinSize(bWidth, bHeight);
        hSeven.getChildren().add(seven);

        seven.setOnAction((ActionEvent event) -> {
            barcode.setText(barcode.getText() + "7");
        });

        Button eight = new Button("8");
        HBox hEight = new HBox(0);
        eight.setMaxSize(bWidth, bHeight);
        eight.setMinSize(bWidth, bHeight);
        hEight.getChildren().add(eight);

        eight.setOnAction((ActionEvent event) -> {
            barcode.setText(barcode.getText() + "8");
        });

        Button nine = new Button("9");
        HBox hNine = new HBox(0);
        nine.setMaxSize(bWidth, bHeight);
        nine.setMinSize(bWidth, bHeight);
        hNine.getChildren().add(nine);

        nine.setOnAction((ActionEvent event) -> {
            barcode.setText(barcode.getText() + "9");
        });

        Button four = new Button("4");
        HBox hFour = new HBox(0);
        four.setMaxSize(bWidth, bHeight);
        four.setMinSize(bWidth, bHeight);
        hFour.getChildren().add(four);

        four.setOnAction((ActionEvent event) -> {
            barcode.setText(barcode.getText() + "4");
        });

        Button five = new Button("5");
        HBox hFive = new HBox(0);
        five.setMaxSize(bWidth, bHeight);
        five.setMinSize(bWidth, bHeight);
        hFive.getChildren().add(five);

        five.setOnAction((ActionEvent event) -> {
            barcode.setText(barcode.getText() + "5");
        });

        Button six = new Button("6");
        HBox hSix = new HBox(0);
        six.setMaxSize(bWidth, bHeight);
        six.setMinSize(bWidth, bHeight);
        hSix.getChildren().add(six);

        six.setOnAction((ActionEvent event) -> {
            barcode.setText(barcode.getText() + "6");
        });

        Button one = new Button("1");
        HBox hOne = new HBox(0);
        one.setMaxSize(bWidth, bHeight);
        one.setMinSize(bWidth, bHeight);
        hOne.getChildren().add(one);

        one.setOnAction((ActionEvent event) -> {
            barcode.setText(barcode.getText() + "1");
        });

        Button two = new Button("2");
        HBox hTwo = new HBox(0);
        two.setMaxSize(bWidth, bHeight);
        two.setMinSize(bWidth, bHeight);
        hTwo.getChildren().add(two);

        two.setOnAction((ActionEvent event) -> {
            barcode.setText(barcode.getText() + "2");
        });

        Button three = new Button("3");
        HBox hThree = new HBox(0);
        three.setMaxSize(bWidth, bHeight);
        three.setMinSize(bWidth, bHeight);
        hThree.getChildren().add(three);

        three.setOnAction((ActionEvent event) -> {
            barcode.setText(barcode.getText() + "3");
        });

        Button zero = new Button("0");
        HBox hZero = new HBox(0);
        zero.setMaxSize(bWidth, bHeight);
        zero.setMinSize(bWidth, bHeight);
        hZero.getChildren().add(zero);

        zero.setOnAction((ActionEvent event) -> {
            barcode.setText(barcode.getText() + "0");
        });

        Button dZero = new Button("00");
        HBox hDzero = new HBox(0);
        dZero.setMaxSize(bWidth, bHeight);
        dZero.setMinSize(bWidth, bHeight);
        hDzero.getChildren().add(dZero);

        dZero.setOnAction((ActionEvent event) -> {
            barcode.setText(barcode.getText() + "00");
        });

        Button enter = new Button("Ent");
        HBox hEnter = new HBox(0);
        enter.setMaxSize(bWidth, bHeight);
        enter.setMinSize(bWidth, bHeight);
        hEnter.getChildren().add(enter);

        enter.setOnAction((ActionEvent event) -> {
            if (!barcode.getText().equals("")) {
                getProductByBarcode(barcode.getText());
                barcode.setText("");
            }
        });

        grid.add(hClear, 1, 0);
        grid.add(hSeven, 1, 1);
        grid.add(hEight, 2, 1);
        grid.add(hNine, 3, 1);
        grid.add(hFour, 1, 2);
        grid.add(hFive, 2, 2);
        grid.add(hSix, 3, 2);
        grid.add(hOne, 1, 3);
        grid.add(hTwo, 2, 3);
        grid.add(hThree, 3, 3);
        grid.add(hZero, 1, 4);
        grid.add(hDzero, 2, 4);
        grid.add(hEnter, 3, 4);

        return grid;
    }

    private void initPayment() {
        paymentPane = new GridPane();

        fivePounds = new Button("£5");
        fivePounds.setMaxSize(150, 150);
        fivePounds.setMinSize(150, 150);
        HBox hFive = new HBox(0);
        hFive.getChildren().add(fivePounds);
        fivePounds.setOnAction((ActionEvent event) -> {
            if (!sale.getSaleItems().isEmpty()) {
                Platform.runLater(() -> {
                    addMoney(PaymentItem.PaymentType.CASH, new BigDecimal("5.00"));
                });
            }
        });

        tenPounds = new Button("£10");
        tenPounds.setMaxSize(150, 150);
        tenPounds.setMinSize(150, 150);
        HBox hTen = new HBox(0);
        hTen.getChildren().add(tenPounds);
        tenPounds.setOnAction((ActionEvent event) -> {
            if (!sale.getSaleItems().isEmpty()) {
                Platform.runLater(() -> {
                    addMoney(PaymentItem.PaymentType.CASH, new BigDecimal("10.00"));
                });
            }
        });

        twentyPounds = new Button("£20");
        twentyPounds.setMaxSize(150, 150);
        twentyPounds.setMinSize(150, 150);
        HBox hTwenty = new HBox(0);
        hTwenty.getChildren().add(twentyPounds);
        twentyPounds.setOnAction((ActionEvent event) -> {
            if (!sale.getSaleItems().isEmpty()) {
                Platform.runLater(() -> {
                    addMoney(PaymentItem.PaymentType.CASH, new BigDecimal("20.00"));
                });
            }
        });

        customValue = new Button("Custom Value");
        customValue.setMaxSize(150, 150);
        customValue.setMinSize(150, 150);
        HBox hCustom = new HBox(0);
        hCustom.getChildren().add(customValue);
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
        exactValue.setMaxSize(150, 150);
        exactValue.setMinSize(150, 150);
        HBox hExact = new HBox(0);
        hExact.getChildren().add(exactValue);
        exactValue.setOnAction((ActionEvent event) -> {
            if (!sale.getSaleItems().isEmpty()) {
                Platform.runLater(() -> {
                    addMoney(PaymentItem.PaymentType.CASH, amountDue);
                });
            }
        });

        card = new Button("Credit Card");
        card.setMaxSize(150, 150);
        card.setMinSize(150, 150);
        HBox hCard = new HBox(0);
        hCard.getChildren().add(card);
        card.setOnAction((ActionEvent event) -> {
            if (!sale.getSaleItems().isEmpty()) {
                int val = NumberEntry.showNumberEntryDialog(this, "Enter Card Value");
                double d = (double) val;
                addMoney(PaymentItem.PaymentType.CARD, new BigDecimal(Double.toString(d / 100)));
            }
        });
        card.setDisable(true);

        addCustomer = new Button("Add Customer");
        addCustomer.setMaxSize(150, 150);
        addCustomer.setMinSize(150, 150);
        HBox hCustomer = new HBox(0);
        hCustomer.getChildren().add(addCustomer);
        addCustomer.setOnAction((ActionEvent event) -> {
            if (!sale.getSaleItems().isEmpty()) {
                Platform.runLater(() -> {
                    if (sale.getCustomer() != null) {
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
        chargeAccount.setMinSize(150, 150);
        chargeAccount.setMaxSize(150, 150);
        chargeAccount.setDisable(true);
        HBox hCharge = new HBox(0);
        hCharge.getChildren().add(chargeAccount);
        chargeAccount.setOnAction((ActionEvent event) -> {
            sale.setChargeAccount(true);
            Platform.runLater(() -> {
                addMoney(PaymentItem.PaymentType.ACCOUNT, amountDue);
            });
        });

        cheque = new Button("Cheque");
        cheque.setMinSize(150, 150);
        cheque.setMaxSize(150, 150);
        HBox hCheque = new HBox(0);
        hCheque.getChildren().add(cheque);
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
        saleCustomer.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));

        Button back = new Button("Back");
        back.setMaxSize(150, 150);
        back.setMinSize(150, 150);
        HBox hBack = new HBox(0);
        hBack.getChildren().add(back);
        back.setOnAction((ActionEvent event) -> {
            setScene(mainScene);
        });

        paymentsList = new ListView<>();
        obPayments = FXCollections.observableArrayList();
        paymentsList.setItems(obPayments);
        paymentsList.setId("PAYMENT_LIST");

        paymentTotal = new Label("Total: £" + sale.getTotal().toString());
        paymentTotal.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
        paymentTotal.setMinSize(150, 150);
        paymentTotal.setMaxSize(150, 150);
        HBox hTotal = new HBox(0);
        hTotal.getChildren().add(paymentTotal);

        voidItem = new Button("Void");
        voidItem.setMinSize(150, 150);
        voidItem.setMaxSize(150, 150);
        HBox hVoid = new HBox(0);
        hVoid.getChildren().add(voidItem);
        voidItem.setOnAction((ActionEvent event) -> {
            if (paymentsList.getSelectionModel().getSelectedIndex() > -1) {
                PaymentItem pi = paymentsList.getSelectionModel().getSelectedItem().clone();
                obPayments.remove(paymentsList.getSelectionModel().getSelectedIndex());
                paymentsList.refresh();
                addMoney(pi.getType(), pi.getValue().negate());
            }
        });

        voidSale = new Button("Void Sale");
        voidSale.setMinSize(150, 150);
        voidSale.setMaxSize(150, 150);
        HBox hVoidSale = new HBox(0);
        hVoidSale.getChildren().add(voidSale);
        voidSale.setOnAction((ActionEvent event) -> {
            if (YesNoDialog.showDialog(this, "Void Sale", "Are you sure you want to void the sale?") == YesNoDialog.YES) {
                newSale();
            }
        });

        discount = new Button("Discounts");
        discount.setMinSize(150, 150);
        discount.setMaxSize(150, 150);
        HBox hDiscount = new HBox(0);
        hDiscount.getChildren().add(discount);
        discount.setOnAction((ActionEvent event) -> {
            Discount d = DiscountSelectDialog.showDialog(this, dc);
            if (d != null) {
                d.setPrice(sale.getTotal().multiply(new BigDecimal(Double.toString(d.getPercentage() / 100)).negate()));
                sale.addItem(d, 1);
//                sale.setDiscount(d);
                setTotalLabel();
                itemsTable.refresh();
            }
        });

        settings = new Button("Settings");
        settings.setMinSize(150, 150);
        settings.setMaxSize(150, 150);
        HBox hSettings = new HBox(0);
        hSettings.getChildren().add(settings);
        settings.setOnAction((ActionEvent event) -> {
            if (staff.getPosition() == Staff.Position.MANAGER || staff.getPosition() == Staff.Position.AREA_MANAGER) {
                SetupDialog.showDialog(MainStage.this);
                MessageDialog.showMessage(this, "Settings", "Changes will apply after restart");
            } else {
                MessageDialog.showMessage(this, "Settings", "You are not allowed to view this screen");
            }
        });

        cashUp = new Button("Cash Up");
        cashUp.setMinSize(150, 150);
        cashUp.setMaxSize(150, 150);
        HBox hCashUp = new HBox(0);
        hCashUp.getChildren().add(cashUp);
        cashUp.setOnAction((ActionEvent event) -> {
            if (staff.getPosition() == Staff.Position.MANAGER || staff.getPosition() == Staff.Position.AREA_MANAGER) {
                CashUpDialog.showDialog(this, dc);
            } else {
                MessageDialog.showMessage(this, "Cash Up", "You are not allowed to view this screen");
            }
        });

        paymentPane.add(hFive, 1, 1);
        paymentPane.add(hTen, 2, 1);
        paymentPane.add(hTwenty, 3, 1);
        paymentPane.add(hCustom, 1, 2);
        paymentPane.add(hExact, 2, 2);
        paymentPane.add(hCard, 3, 2);
        paymentPane.add(hCustomer, 1, 3);
        paymentPane.add(hCharge, 2, 3);
        paymentPane.add(hCheque, 3, 3);
        paymentPane.add(saleCustomer, 1, 4);
        paymentPane.add(hBack, 7, 4);
        paymentPane.add(paymentsList, 5, 1, 2, 3);
        paymentPane.add(hTotal, 5, 4);
        paymentPane.add(hVoid, 7, 1);
        paymentPane.add(hVoidSale, 7, 2);
        paymentPane.add(hDiscount, 7, 3);
        paymentPane.add(hSettings, 8, 1);
        paymentPane.add(hCashUp, 8, 2);

        paymentScene = new Scene(paymentPane, 1024, 768);
    }

    private void initLogin() {
        loginPane = new BorderPane();

        FlowPane staffLayout = new FlowPane();
        staffLayout.setPadding(new Insets(150, 152, 0, 152));
        staffLayout.setHgap(40);
        staffLayout.setVgap(40);
        staffLayout.setPrefWrapLength(480);

        FlowPane buttons = new FlowPane();
        buttons.setPadding(new Insets(20));
        buttons.setVgap(20);

        exit = new Button("Exit JTill");
        exit.setMinSize(100, 100);
        exit.setMaxSize(100, 100);
        HBox hExit = new HBox(0);
        hExit.getChildren().add(exit);
        exit.setOnAction((ActionEvent event) -> {
            Platform.runLater(() -> {
                dc.close();
                System.exit(0);
            });
        });
        buttons.getChildren().add(exit);

        login = new Button("Login");
        login.setMinSize(100, 100);
        login.setMaxSize(100, 100);
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
                    Button button = new Button(s.getName());
                    button.setMinSize(150, 150);
                    button.setMaxSize(150, 150);
                    button.setOnAction((ActionEvent evt) -> {
                        try {
                            MainStage.this.staff = s;
                            dc.tillLogin(s.getId());
                            Sale rs = dc.resumeSale(s);
                            if (rs != null) {
                                MainStage.this.sale = rs;
                                obTable.setAll(rs.getSaleItems());
                                setTotalLabel();
                                setCustomer(rs.getCustomer());
                            }
                            staffLabel.setText("Staff: " + s.getName());
                            setScene(mainScene);
                        } catch (IOException | LoginException | SQLException ex) {
                            MessageDialog.showMessage(MainStage.this, "Log on", ex.getMessage());
                        }
                    });
                    staffLayout.getChildren().add(button);
                } catch (StaffNotFoundException | IOException | SQLException ex) {
                    MessageDialog.showMessage(MainStage.this, "Log on", ex.getMessage());
                }
            });
        });
        buttons.getChildren().add(login);

        loginPane.setBottom(buttons);

        loginPane.setCenter(staffLayout);

//        try {
//            Image image = dc.getFXImage();
//            ImageView background = new ImageView(image);
//            loginPane.getChildren().add(background);
//        } catch (IOException ex) {
//            Logger.getLogger(MainStage.class.getName()).log(Level.SEVERE, null, ex);
//        }
        loginScene = new Scene(loginPane, 1024, 768);
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
        sale.setCustomer(c);
    }

    private void addMoney(PaymentItem.PaymentType type, BigDecimal val) {
        amountDue = amountDue.subtract(val);
        if (val.compareTo(BigDecimal.ZERO) > 0) {
            obPayments.add(new PaymentItem(type, val));
        }
        total.setText("Total: £" + amountDue);
        paymentTotal.setText("Total: £" + amountDue);
        if (amountDue.compareTo(BigDecimal.ZERO) < 0) {
            MessageDialog.showMessage(this, "Change", "Change Due: £" + amountDue.abs().toString());
            completeCurrentSale();
        } else if (amountDue.compareTo(BigDecimal.ZERO) == 0) {
            completeCurrentSale();
        }
    }

    private void completeCurrentSale() {
        try {
            sale.setTime(new Time(System.currentTimeMillis()));
            dc.addSale(sale);
            if (YesNoDialog.showDialog(this, "Email Receipt", "Email Customer Receipt?") == YesNoDialog.YES) {
                if (sale.getCustomer() != null) {
                    dc.emailReceipt(sale.getCustomer().getEmail(), sale);
                } else {
                    String email = EntryDialog.show(this, "Email Receipt", "Enter email");
                    dc.emailReceipt(email, sale);
                }
            }
            newSale();
        } catch (IOException | SQLException ex) {
            showErrorAlert(ex);
        }
    }

    private void showErrorAlert(Exception ex) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setContentText(ex.toString());
        alert.showAndWait();
    }

    private void logoff() {
        try {
            dc.tillLogout(staff);
            if (!sale.getSaleItems().isEmpty()) {
                dc.suspendSale(sale, staff);
            }
            staff = null;
            newSale();
            setScene(loginScene);
        } catch (IOException | StaffNotFoundException ex) {
            showErrorAlert(ex);
        }
    }

    private void newSale() {
        sale = new Sale(JavaFXJTill.NAME, staff);
        obTable = FXCollections.observableArrayList();
        obPayments = FXCollections.observableArrayList();
        paymentsList.setItems(obPayments);
        updateList();
        total.setText("Total: £0.00");
        paymentTotal.setText("Total: £0.00");
        itemQuantity = 1;
        quantity.setText("Quantity: 1");
        saleCustomer.setText("No Customer");
        addCustomer.setText("Add Customer");
        chargeAccount.setDisable(true);
        setScene(mainScene);
    }

    private void updateList() {
        itemsTable.setItems(obTable);
    }

    private void getProductByBarcode(String barcode) {
        try {
            Product p = dc.getProductByBarcode(barcode);
            addItemToSale(p);
        } catch (IOException | ProductNotFoundException | SQLException ex) {
            MessageDialog.showMessage(this, "Barcode", ex.getMessage());
        }
    }

    private void addItemToSale(Product p) {
        if (p.isOpen()) {
            int value;
            if (barcode.getText().equals("")) {
                value = NumberEntry.showNumberEntryDialog(this, "Enter price");
            } else {
                value = Integer.parseInt(barcode.getText());
                barcode.setText("");
            }
            if (value == 0) {
                return;
            }
            p.setPrice(new BigDecimal(Double.toString((double) value / 100)));
        }
        boolean inSale = sale.addItem(p, itemQuantity);
        if (!inSale) {
            obTable.add(sale.getLastAdded());
            itemsTable.scrollTo(obTable.size() - 1);
        } else {
            itemsTable.refresh();
        }
        setTotalLabel();
        itemQuantity = 1;
        quantity.setText("Quantity: 1");
    }

    private void setTotalLabel() {
        DecimalFormat df;
        if (sale.getTotal().compareTo(BigDecimal.ZERO) > 1) {
            df = new DecimalFormat("#.00");
        } else {
            df = new DecimalFormat("0.00");
        }
        total.setText("Total: £" + df.format(sale.getTotal()));
        paymentTotal.setText("Total: £" + df.format(sale.getTotal()));
        amountDue = sale.getTotal();
    }

    private void addScreens(List<Screen> screens, FlowPane pane) {
        final int WIDTH = 180;
        final int HEIGHT = 50;

        int x = 0;
        int y = 0;

        for (Screen s : screens) {
            FlowPane grid = new FlowPane();
            grid.setPrefWrapLength(750);
            ToggleButton button = new ToggleButton(s.getName());
            button.setToggleGroup(screenButtonGroup);
            button.setId("screenButton");
            button.setMaxSize(WIDTH, HEIGHT);
            button.setMinSize(WIDTH, HEIGHT);
            HBox hButton = new HBox(0);
            hButton.getChildren().add(button);
            pane.getChildren().add(hButton);
            setScreenButtons(s, grid);
            buttonPanes.add(grid);
            button.setOnAction((ActionEvent event) -> {
                buttonPane.getChildren().clear();
                buttonPane.getChildren().add(grid);
            });
            x++;
            if (x == 5) {
                x = 1;
                y++;
            }
        }
    }

    private void setScreenButtons(Screen s, FlowPane pane) {
        try {
            List<TillButton> buttons = dc.getButtonsOnScreen(s);
            addButtons(buttons, pane);
        } catch (IOException | SQLException | ScreenNotFoundException ex) {
            showErrorAlert(ex);
        }
    }

    private void addButtons(List<TillButton> buttons, FlowPane grid) {
        int x = 0;
        int y = 0;
        for (TillButton b : buttons) {
            HBox hbBtn;
            if (b.getName().equals("[SPACE]")) {
                hbBtn = new HBox(10);
                hbBtn.setAlignment(Pos.BOTTOM_RIGHT);
                hbBtn.setMinSize(150, 50);
                hbBtn.setMaxSize(150, 50);
            } else {
                Button button = new Button(b.getName());
                button.setId("productButton");
                button.setMaxSize(150, 50);
                button.setMinSize(150, 50);
                hbBtn = new HBox(10);
                hbBtn.setAlignment(Pos.BOTTOM_RIGHT);
                hbBtn.getChildren().add(button);
                button.setOnAction((ActionEvent e) -> {
                    Product p = b.getProduct().clone();
                    Platform.runLater(() -> {
                        onProductButton(p);
                    });
                });
            }
            grid.getChildren().add(hbBtn);

            x++;
            if (x == 5) {
                x = 0;
                y++;
            }
        }
    }

    private void onProductButton(Product p) {
        addItemToSale(p);
    }
}
