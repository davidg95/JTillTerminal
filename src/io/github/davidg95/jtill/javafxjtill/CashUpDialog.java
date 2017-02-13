/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.davidg95.jtill.javafxjtill;

import io.github.davidg95.JTill.jtill.DataConnectInterface;
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

/**
 *
 * @author David
 */
public class CashUpDialog extends Stage {

    private static Stage dialog;

    private final DataConnectInterface dc;

    private Label cashLabel;
    private TextField cashValue;
    private Button declare;
    private Label takingsLabel;
    private TextField takingsField;
    private Label differenceLabel;
    private TextField differenceField;
    private Button close;

    public CashUpDialog(Window parent, DataConnectInterface dc) {
        this.dc = dc;
        init();
        setTitle("Cash Up");
        initOwner(parent);
        initModality(Modality.APPLICATION_MODAL);
    }

    public static void showDialog(Window parent, DataConnectInterface dc) {
        dialog = new CashUpDialog(parent, dc);
        dialog.showAndWait();
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

//        pane.add(fifty, 1, 1);
//        pane.add(textFifty, 2, 1);
//        pane.add(twenty, 1, 2);
//        pane.add(textTwenty, 2, 2);
//        pane.add(ten, 1, 3);
//        pane.add(textTen, 2, 3);
//        pane.add(five, 1, 4);
//        pane.add(textFive, 2, 4);
//        pane.add(two, 1, 5);
//        pane.add(textTwo, 2, 5);
//        pane.add(one, 1, 6);
//        pane.add(textOne, 2, 6);
//        pane.add(fiftyp, 1, 7);
//        pane.add(textFiftyp, 2, 7);
//        pane.add(twentyp, 1, 8);
//        pane.add(textTwentyp, 2, 8);
//        pane.add(tenp, 1, 9);
//        pane.add(textTenp, 2, 9);
//        pane.add(fivep, 1, 10);
//        pane.add(textFivep, 2, 10);
//        pane.add(twop, 1, 11);
//        pane.add(textTwop, 2, 11);
//        pane.add(onep, 1, 12);
//        pane.add(textOnep, 2, 12);
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
            try {
                cashValue.setDisable(true);
                BigDecimal takings = dc.getTillTakings(JavaFXJTill.NAME);
                takings = takings.setScale(2);
                BigDecimal valueCounted = new BigDecimal(cashValue.getText());
                valueCounted = valueCounted.setScale(2);
                BigDecimal difference = valueCounted.subtract(takings);
                difference = difference.setScale(2);
                DecimalFormat df;
                if (takings.compareTo(BigDecimal.ONE) >= 1) {
                    df = new DecimalFormat("#.00");
                } else {
                    df = new DecimalFormat("0.00");
                }
                takingsField.setText(df.format(takings.doubleValue()));
                if (difference.compareTo(BigDecimal.ONE) >= 1) {
                    df = new DecimalFormat("#.00");
                } else {
                    df = new DecimalFormat("0.00");
                }
                differenceField.setText(df.format(difference.doubleValue()));
                if (YesNoDialog.showDialog(this, "Cash up", "Do you want the report emailed?") == YesNoDialog.YES) {
                    String message = "Cashup for terminal " + JavaFXJTill.NAME
                            + "\nValue counted: £" + valueCounted.toString()
                            + "\nActual takings: £" + takings.toString()
                            + "\nDifference: £" + difference.toString();
                    dc.sendEmail(message);
                }
            } catch (IOException | SQLException ex) {
                MessageDialog.showMessage(this, "Cash Up", "Server error");
            }
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
