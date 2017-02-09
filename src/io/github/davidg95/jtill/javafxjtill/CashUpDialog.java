/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.davidg95.jtill.javafxjtill;

import java.math.BigDecimal;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

/**
 *
 * @author David
 */
public class CashUpDialog extends Stage {

    private static Stage dialog;

    private BigDecimal valueCounted;

    public CashUpDialog(Window parent) {
        init();
        setTitle("Cash Up");
        initOwner(parent);
        initModality(Modality.APPLICATION_MODAL);
    }

    public static void showDialog(Window parent) {
        dialog = new CashUpDialog(parent);
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
        valueCounted = new BigDecimal("0");
        Label counted = new Label("Enter value of cash:");
        TextField textValue = new TextField();
        textValue.setOnMousePressed((MouseEvent event) -> {
            double val = (double) NumberEntry.showNumberEntryDialog(CashUpDialog.this, "Enter Cash Value");
            textValue.setText((val / 100) + "");
            valueCounted = new BigDecimal(Double.toString(val / 100));
        });

        pane.add(counted, 1, 1);
        pane.add(textValue, 2, 1);

        Scene scene = new Scene(pane, 400, 600);

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
