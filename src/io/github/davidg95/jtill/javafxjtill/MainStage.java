/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.davidg95.jtill.javafxjtill;

import io.github.davidg95.JTill.jtill.DataConnectInterface;
import io.github.davidg95.JTill.jtill.Product;
import io.github.davidg95.JTill.jtill.ProductNotFoundException;
import io.github.davidg95.JTill.jtill.Sale;
import io.github.davidg95.JTill.jtill.SaleItem;
import io.github.davidg95.JTill.jtill.Staff;
import io.github.davidg95.JTill.jtill.StaffNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 *
 * @author David
 */
public class MainStage extends Stage {

    private final Staff staff;
    private final DataConnectInterface dc;

    private ListView<SaleItem> itemsInSale;
    private ObservableList<SaleItem> items;

    private Sale sale;

    private int itemQuantity;

    private BigDecimal amountDue;

    //Main Scene Components
    private Scene mainScene;
    private GridPane mainPane;
    private Label staffLabel;
    private Label time;
    private Text total;
    private Button logoff;
    private Button quantity;
    private TextField barcode;
    private Button payment;

    //Payment Scene Components
    private Scene paymentScene;
    private GridPane paymentPane;
    private Button fivePounds;
    private Button tenPounds;
    private Button twentyPounds;
    private Button customValue;
    private Button exactValue;
    private Button card;

    public MainStage(Staff s, DataConnectInterface dc) {
        super();
        this.staff = s;
        this.dc = dc;
        this.sale = new Sale();
        setTitle("JTill Terminal");
        init();
        initPayment();

    }

    private void init() {
        try {
            mainPane = new GridPane();
            staffLabel = new Label("Staff: " + staff.getName());
            staffLabel.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
            mainPane.add(staffLabel, 1, 1, 2, 1);
            time = new Label("00:00");
            time.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
            ClockThread.setClockLabel(time);
            mainPane.add(time, 6, 1);
            //scene.getStylesheets().add(JavaFXJTill.class.getResource("login.css").toExternalForm());
            List<io.github.davidg95.JTill.jtill.Button> buttons = dc.getAllButtons();
            addButtons(buttons, mainPane);
            itemsInSale = new ListView<>();
            itemsInSale.setMinSize(240, 300);
            itemsInSale.setMaxSize(240, 300);
            items = FXCollections.observableArrayList();
            updateList();
            mainPane.add(itemsInSale, 6, 2, 2, 6);
            logoff = new Button("Logoff");
            logoff.setMaxSize(100, 100);
            logoff.setMinSize(100, 100);
            logoff.setOnAction((ActionEvent event) -> {
                logoff();
            });
            logoff.setStyle("-fx-base: #0000FF;");
            //pane.add(logoff, 7, 2, 1, 2);
            total = new Text("Total: £0.00");
            total.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
            mainPane.add(total, 6, 8, 2, 1);
            quantity = new Button("Quantity: 1");
            quantity.setMinSize(120, 50);
            quantity.setMaxSize(120, 50);
            itemQuantity = 1;
            quantity.setOnAction((ActionEvent event) -> {
                if (barcode.getText().equals("")) {
                    int val = NumberEntry.showNumberEntryDialog(this, "Enter Quantity");
                    if (val != 0) {
                        itemQuantity = val;
                    }
                } else {
                    itemQuantity = Integer.parseInt(barcode.getText());
                    barcode.setText("");
                }
                quantity.setText("Quantity: " + itemQuantity);
            });
            mainPane.add(quantity, 6, 9);
            barcode = new TextField();
            barcode.setMinSize(240, 50);
            barcode.setMaxSize(240, 50);
            barcode.setFont(Font.font("Tahoma", FontWeight.NORMAL, 25));
            barcode.setOnAction((ActionEvent event) -> {
                getProductByBarcode(barcode.getText());
                barcode.setText("");
            });
            mainPane.add(barcode, 6, 10, 2, 1);
            GridPane numbers = createNumbersPane();
            mainPane.add(numbers, 6, 11, 2, 3);
            payment = new Button("Payment");
            payment.setMinSize(240, 100);
            payment.setMaxSize(240, 100);
            payment.setOnAction((ActionEvent event) -> {
                setScene(paymentScene);
            });
            mainPane.add(payment, 6, 14, 2, 2);
            mainScene = new Scene(mainPane, 1024, 768);
            mainScene.getStylesheets().add((MainStage.class.getResource("style.css").toExternalForm()));
            setScene(mainScene);
            show();
        } catch (IOException | SQLException ex) {
            showErrorAlert(ex);
        }
    }

    private GridPane createNumbersPane() {
        final int bWidth = 80;
        final int bHeight = 40;
        GridPane grid = new GridPane();

        Button seven = new Button("7");
        HBox hSeven = new HBox(0);
        seven.setMaxSize(bWidth, bHeight);
        seven.setMinSize(bWidth, bHeight);
        //hSeven.setAlignment(Pos.TOP_LEFT);
        hSeven.getChildren().add(seven);
        grid.add(hSeven, 1, 1);

        seven.setOnAction((ActionEvent event) -> {
            barcode.setText(barcode.getText() + "7");
        });

        Button eight = new Button("8");
        HBox hEight = new HBox(0);
        eight.setMaxSize(bWidth, bHeight);
        eight.setMinSize(bWidth, bHeight);
        //hSeven.setAlignment(Pos.TOP_CENTER);
        hEight.getChildren().add(eight);
        grid.add(hEight, 2, 1);

        eight.setOnAction((ActionEvent event) -> {
            barcode.setText(barcode.getText() + "8");
        });

        Button nine = new Button("9");
        HBox hNine = new HBox(0);
        nine.setMaxSize(bWidth, bHeight);
        nine.setMinSize(bWidth, bHeight);
        //hSeven.setAlignment(Pos.TOP_RIGHT);
        hNine.getChildren().add(nine);
        grid.add(hNine, 3, 1);

        nine.setOnAction((ActionEvent event) -> {
            barcode.setText(barcode.getText() + "9");
        });

        Button four = new Button("4");
        HBox hFour = new HBox(0);
        four.setMaxSize(bWidth, bHeight);
        four.setMinSize(bWidth, bHeight);
        //hFour.setAlignment(Pos.CENTER_LEFT);
        hFour.getChildren().add(four);
        grid.add(hFour, 1, 2);

        four.setOnAction((ActionEvent event) -> {
            barcode.setText(barcode.getText() + "4");
        });

        Button five = new Button("5");
        HBox hFive = new HBox(0);
        five.setMaxSize(bWidth, bHeight);
        five.setMinSize(bWidth, bHeight);
        //hFive.setAlignment(Pos.CENTER);
        hFive.getChildren().add(five);
        grid.add(hFive, 2, 2);

        five.setOnAction((ActionEvent event) -> {
            barcode.setText(barcode.getText() + "5");
        });

        Button six = new Button("6");
        HBox hSix = new HBox(0);
        six.setMaxSize(bWidth, bHeight);
        six.setMinSize(bWidth, bHeight);
        //hSix.setAlignment(Pos.CENTER_RIGHT);
        hSix.getChildren().add(six);
        grid.add(hSix, 3, 2);

        six.setOnAction((ActionEvent event) -> {
            barcode.setText(barcode.getText() + "6");
        });

        Button one = new Button("1");
        HBox hOne = new HBox(0);
        one.setMaxSize(bWidth, bHeight);
        one.setMinSize(bWidth, bHeight);
        //hOne.setAlignment(Pos.BOTTOM_LEFT);
        hOne.getChildren().add(one);
        grid.add(hOne, 1, 3);

        one.setOnAction((ActionEvent event) -> {
            barcode.setText(barcode.getText() + "1");
        });

        Button two = new Button("2");
        HBox hTwo = new HBox(0);
        two.setMaxSize(bWidth, bHeight);
        two.setMinSize(bWidth, bHeight);
        //hTwo.setAlignment(Pos.BOTTOM_CENTER);
        hTwo.getChildren().add(two);
        grid.add(hTwo, 2, 3);

        two.setOnAction((ActionEvent event) -> {
            barcode.setText(barcode.getText() + "2");
        });

        Button three = new Button("3");
        HBox hThree = new HBox(0);
        three.setMaxSize(bWidth, bHeight);
        three.setMinSize(bWidth, bHeight);
        //hThree.setAlignment(Pos.BOTTOM_RIGHT);
        hThree.getChildren().add(three);
        grid.add(hThree, 3, 3);

        three.setOnAction((ActionEvent event) -> {
            barcode.setText(barcode.getText() + "3");
        });

        Button zero = new Button("0");
        HBox hZero = new HBox(0);
        zero.setMaxSize(bWidth, bHeight);
        zero.setMinSize(bWidth, bHeight);
        hZero.getChildren().add(zero);
        grid.add(hZero, 1, 4);

        zero.setOnAction((ActionEvent event) -> {
            barcode.setText(barcode.getText() + "0");
        });

        Button dZero = new Button("00");
        HBox hDzero = new HBox(0);
        dZero.setMaxSize(bWidth, bHeight);
        dZero.setMinSize(bWidth, bHeight);
        hDzero.getChildren().add(dZero);
        grid.add(hDzero, 2, 4);

        dZero.setOnAction((ActionEvent event) -> {
            barcode.setText(barcode.getText() + "00");
        });

        Button enter = new Button("Ent");
        HBox hEnter = new HBox(0);
        enter.setMaxSize(bWidth, bHeight);
        enter.setMinSize(bWidth, bHeight);
        hEnter.getChildren().add(enter);
        grid.add(hEnter, 3, 4);

        enter.setOnAction((ActionEvent event) -> {
            getProductByBarcode(barcode.getText());
            barcode.setText("");
        });

        return grid;
    }

    private void initPayment() {
        paymentPane = new GridPane();

        fivePounds = new Button("£5");
        fivePounds.setMaxSize(150, 150);
        fivePounds.setMinSize(150, 150);
        HBox hFive = new HBox(0);
        hFive.getChildren().add(fivePounds);
        paymentPane.add(hFive, 1, 1);
        fivePounds.setOnAction((ActionEvent event) -> {
            if (!sale.getSaleItems().isEmpty()) {
                addMoney(new BigDecimal("5"));
            }
        });

        tenPounds = new Button("£10");
        tenPounds.setMaxSize(150, 150);
        tenPounds.setMinSize(150, 150);
        HBox hTen = new HBox(0);
        hTen.getChildren().add(tenPounds);
        paymentPane.add(hTen, 2, 1);
        tenPounds.setOnAction((ActionEvent event) -> {
            if (!sale.getSaleItems().isEmpty()) {
                addMoney(new BigDecimal("10"));
            }
        });

        twentyPounds = new Button("£20");
        twentyPounds.setMaxSize(150, 150);
        twentyPounds.setMinSize(150, 150);
        HBox hTwenty = new HBox(0);
        hTwenty.getChildren().add(twentyPounds);
        paymentPane.add(hTwenty, 3, 1);
        twentyPounds.setOnAction((ActionEvent event) -> {
            if (!sale.getSaleItems().isEmpty()) {
                addMoney(new BigDecimal("20"));
            }
        });

        customValue = new Button("Custom Value");
        customValue.setMaxSize(150, 150);
        customValue.setMinSize(150, 150);
        HBox hCustom = new HBox(0);
        hCustom.getChildren().add(customValue);
        paymentPane.add(hCustom, 1, 2);
        customValue.setOnAction((ActionEvent event) -> {
            if (!sale.getSaleItems().isEmpty()) {
                int value = NumberEntry.showNumberEntryDialog(this, "Enter amount");
                addMoney(new BigDecimal(Double.toString(value / 100)));
            }
        });

        exactValue = new Button("Exact Value");
        exactValue.setMaxSize(150, 150);
        exactValue.setMinSize(150, 150);
        HBox hExact = new HBox(0);
        hExact.getChildren().add(exactValue);
        paymentPane.add(hExact, 2, 2);
        exactValue.setOnAction((ActionEvent event) -> {
            if (!sale.getSaleItems().isEmpty()) {
                addMoney(amountDue);
            }
        });

        card = new Button("Credit Card");
        card.setMaxSize(150, 150);
        card.setMinSize(150, 150);
        HBox hCard = new HBox(0);
        hCard.getChildren().add(card);
        paymentPane.add(hCard, 3, 2);
        card.setOnAction((ActionEvent event) -> {
            if (!sale.getSaleItems().isEmpty()) {

            }
        });
        card.setDisable(true);

        paymentPane.add(total, 4, 2);

        Button back = new Button("Back");
        back.setMaxSize(150, 150);
        back.setMinSize(150, 150);
        HBox hBack = new HBox(0);
        hBack.getChildren().add(back);
        back.setOnAction((ActionEvent event) -> {
            setScene(mainScene);
        });
        paymentPane.add(back, 6, 4);

        paymentScene = new Scene(paymentPane, 1024, 768);
    }

    private void addMoney(BigDecimal val) {
        amountDue = amountDue.subtract(val);
        total.setText("Total Due: £" + amountDue.toString());
        if (amountDue.compareTo(BigDecimal.ZERO) < 0) {
            //showAlert("Change", "Change Due: £" + amountDue.toString());
            completeCurrentSale();
        } else if (amountDue.compareTo(BigDecimal.ZERO) == 0) {
            completeCurrentSale();
        }
    }

    private void completeCurrentSale() {
        try {
            dc.addSale(sale);
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

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.NONE);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.show();
    }

    private void logoff() {
        try {
            dc.logout(staff.getId());
            close();
        } catch (IOException | StaffNotFoundException ex) {
            showErrorAlert(ex);
        }
    }

    private void newSale() {
        sale = new Sale();
        items = FXCollections.observableArrayList();
        updateList();
        total.setText("Total: £0.00");
        itemQuantity = 1;
        quantity.setText("Quantity: 1");
        setScene(mainScene);
    }

    private void updateList() {
        itemsInSale.setItems(items);
    }

    private void getProductByBarcode(String barcode) {
        try {
            Product p = dc.getProductByBarcode(barcode);
            addItemToSale(p);
        } catch (IOException | ProductNotFoundException | SQLException ex) {
            showErrorAlert(ex);
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
            items.add(sale.getLastAdded());
            itemsInSale.scrollTo(items.size() - 1);
        } else {
            itemsInSale.refresh();
        }
        setTotalLabel();
        itemQuantity = 1;
        quantity.setText("Quantity: 1");
    }

    private void setTotalLabel() {
        total.setText("Total: £" + sale.getTotal());
        amountDue = sale.getTotal();
    }

    private void addButtons(List<io.github.davidg95.JTill.jtill.Button> buttons, GridPane grid) {
        int x = 1;
        int y = 2;
        for (io.github.davidg95.JTill.jtill.Button b : buttons) {
            Button button = new Button(b.getName());
            button.setId("productButton");
            button.setMaxSize(150, 50);
            button.setMinSize(150, 50);
            HBox hbBtn = new HBox(10);
            hbBtn.setAlignment(Pos.BOTTOM_RIGHT);
            hbBtn.getChildren().add(button);
            grid.add(hbBtn, x, y);

            x++;
            if (x == 6) {
                x = 1;
                y++;
            }

            button.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent e) {
                    Product p = b.getProduct().clone();
                    onProductButton(p);
                }
            });
        }
    }

    private void onProductButton(Product p) {
        addItemToSale(p);
    }
}
