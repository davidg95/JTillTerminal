/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.davidg95.jtill.javafxjtill;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.text.DecimalFormat;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import io.github.davidg95.JTill.jtill.*;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;

/**
 *
 * @author David
 */
public class CashUpDialog extends Stage {

    private static Stage dialog;

    private final DataConnect dc;
    private final Till till;

    private Label cashLabel;
    private TextField cashValue;
    private Button declare;
    private Label takingsLabel;
    private TextField takingsField;
    private Label differenceLabel;
    private TextField differenceField;
    private Button close;

    private static int result;

    public CashUpDialog(Window parent, DataConnect dc, Till t) {
        this.dc = dc;
        this.till = t;
        init();
        setTitle("Cash Up");
        initOwner(parent);
        initModality(Modality.APPLICATION_MODAL);
    }

    public static int showDialog(Window parent, DataConnect dc, Till t) {
        dialog = new CashUpDialog(parent, dc, t);
        result = 0;
        dialog.showAndWait();
        return result;
    }

    private void init() {
        GridPane pane = new GridPane();
        pane.setVgap(10);
        pane.setHgap(10);
        pane.setPadding(new Insets(10));

        Label fifty = new Label("£50");
        Label twenty = new Label("£20");
        Label ten = new Label("£10");
        Label five = new Label("£5");
        Label two = new Label("£2");
        Label one = new Label("£1");
        Label fiftyp = new Label("50p");
        Label twentyp = new Label("20p");
        Label tenp = new Label("10p");
        Label fivep = new Label("5p");
        Label twop = new Label("2p");
        Label onep = new Label("1p");

        TextField textFifty = new TextField();
        TextField textTwenty = new TextField();
        TextField textTen = new TextField();
        TextField textFive = new TextField();
        TextField textTwo = new TextField();
        TextField textOne = new TextField();
        TextField textFiftyp = new TextField();
        TextField textTwentyp = new TextField();
        TextField textTenp = new TextField();
        TextField textFivep = new TextField();
        TextField textTwop = new TextField();
        TextField textOnep = new TextField();

        textFifty.setOnAction(new MoneyFieldEvent(textFifty));
        textTwenty.setOnAction(new MoneyFieldEvent(textTwenty));
        textTen.setOnAction(new MoneyFieldEvent(textTen));
        textFive.setOnAction(new MoneyFieldEvent(textFive));
        textTwo.setOnAction(new MoneyFieldEvent(textTwo));
        textOne.setOnAction(new MoneyFieldEvent(textOne));
        textFiftyp.setOnAction(new MoneyFieldEvent(textFiftyp));
        textTwentyp.setOnAction(new MoneyFieldEvent(textTwentyp));
        textTenp.setOnAction(new MoneyFieldEvent(textTenp));
        textFivep.setOnAction(new MoneyFieldEvent(textFivep));
        textTwop.setOnAction(new MoneyFieldEvent(textTwop));
        textOnep.setOnAction(new MoneyFieldEvent(textOnep));
        cashLabel = new Label("Enter value of cash:");
        cashLabel.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));

        cashValue = new TextField("0");
        cashValue.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
        cashValue.setOnMousePressed((MouseEvent event) -> {
            double d;
            if (cashValue.getText().equals("")) {
                d = ((double) NumberEntry.showNumberEntryDialog(this, "Enter cash counted")) / 100;
            } else {
                d = ((double) NumberEntry.showNumberEntryDialog(this, "Enter cash counted", (int) (Double.parseDouble(cashValue.getText()) * 100))) / 100;
            }
            DecimalFormat df;
            if (d >= 1) {
                df = new DecimalFormat("#.00");
            } else {
                df = new DecimalFormat("0.00");
            }
            cashValue.setText(df.format(d) + "");
        });
        cashValue.setOnAction((ActionEvent event) -> {
            declare.fire();
        });

        takingsLabel = new Label("Takings:");
        takingsLabel.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));

        takingsField = new TextField();
        takingsField.setEditable(false);
        takingsField.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));

        differenceLabel = new Label("Difference:");
        differenceLabel.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));

        differenceField = new TextField();
        differenceField.setEditable(false);
        differenceField.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));

        declare = new Button("Declare");
        declare.setMinSize(100, 60);
        declare.setMaxSize(100, 60);
        HBox hDeclare = new HBox(0);
        hDeclare.getChildren().add(declare);
        declare.setOnAction((ActionEvent event) -> {
            new Thread() {
                @Override
                public void run() {
                    declare();
                }
            }.start();
        });

        close = new Button("Close");
        close.setMinSize(100, 60);
        close.setMaxSize(100, 60);
        HBox hClose = new HBox(0);
        hClose.getChildren().add(close);
        close.setOnAction((ActionEvent event) -> {
            hide();
        });

        pane.add(cashLabel, 1, 1);
        pane.add(cashValue, 2, 1);
        pane.add(hDeclare, 2, 2);
        pane.add(takingsLabel, 1, 3);
        pane.add(takingsField, 2, 3);
        pane.add(differenceLabel, 1, 4);
        pane.add(differenceField, 2, 4);
        pane.add(hClose, 3, 5);

        Scene scene = new Scene(pane);
        String stylesheet = MainStage.class.getResource("style.css").toExternalForm();
        scene.getStylesheets().add(stylesheet);
        setScene(scene);

    }

    private void declare() {
        try {
            Platform.runLater(() -> {
                cashValue.setDisable(true);
                MessageScreen.changeMessage("Declaring...");
                MessageScreen.showWindow();
            });
            List<Sale> sales = dc.getTerminalSales(till.getId(), true);
            BigDecimal takings = BigDecimal.ZERO;
            BigDecimal tax = BigDecimal.ZERO;
            for (Sale s : sales) {
                takings = takings.add(s.getTotal());
                for (SaleItem si : s.getSaleItems()) {
                    tax = tax.add(si.getTaxValue());
                }
            }
            takings = takings.setScale(2);
            tax = tax.setScale(2);
            BigDecimal valueCounted = new BigDecimal(cashValue.getText());
            valueCounted = valueCounted.setScale(2);
            BigDecimal difference = valueCounted.subtract(takings);
            difference = difference.setScale(2);
            final DecimalFormat df = new DecimalFormat("0.00");
            dc.cashUncashedSales(till.getId());
            result = 1;
            final BigDecimal fTakings = takings;
            final BigDecimal fDiff = difference;
            Platform.runLater(() -> {
                takingsField.setText(df.format(fTakings.doubleValue()));
                differenceField.setText(df.format(fDiff.doubleValue()));
                MessageScreen.hideWindow();
            });
            final BigDecimal fValCou = valueCounted;
            Platform.runLater(() -> {
                if (YesNoDialog.showDialog(this, "Cash up", "Do you want the report emailed?") == YesNoDialog.YES) {
                    try {
                        String message = "Cashup for terminal " + JavaFXJTill.NAME
                                + "\nValue counted: £" + fValCou.toString()
                                + "\nActual takings: £" + fTakings.toString()
                                + "\nDifference: £" + fDiff.toString();
                        dc.sendEmail(message);
                    } catch (IOException ex) {
                        MessageDialog.showMessage(this, "Cash Up", "Error sending email");
                    }
                }
            });
        } catch (IOException | SQLException | JTillException ex) {
            MessageDialog.showMessage(this, "Cash Up", "Server error");
        }
    }

    private class MoneyFieldEvent implements EventHandler {

        private final TextField field;

        public MoneyFieldEvent(TextField field) {
            this.field = field;
        }

        @Override
        public void handle(Event event) {
            int value = NumberEntry.showNumberEntryDialog(CashUpDialog.this, "Enter Value");
            double d = (double) value;
            field.setText((value / 100) + "");
        }
    }
}
