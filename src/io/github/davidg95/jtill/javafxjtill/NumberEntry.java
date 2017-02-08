/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.davidg95.jtill.javafxjtill;

import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
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
public class NumberEntry extends Stage {

    private static Stage dialog;

    private static int value;

    private int initValue;
    private boolean init;

    public NumberEntry(Window parent, String title) {
        init = false;
        init();
        setTitle(title);
        initOwner(parent);
        initModality(Modality.APPLICATION_MODAL);
    }

    public NumberEntry(Window parent, String title, int initValue) {
        init = true;
        this.initValue = initValue;
        init();
        setTitle(title);
        initOwner(parent);
        initModality(Modality.APPLICATION_MODAL);
    }

    public static int showNumberEntryDialog(Window parent, String title) {
        dialog = new NumberEntry(parent, title);
        value = 0;
        dialog.showAndWait();
        return value;
    }

    public static int showNumberEntryDialog(Window parent, String title, int initValue) {
        dialog = new NumberEntry(parent, title, initValue);
        value = 0;
        dialog.showAndWait();
        return value;
    }

    private void init() {
        GridPane grid = new GridPane();
        TextField number = new TextField();
        number.setMaxHeight(50);
        number.setMaxWidth(400);
        number.setMinHeight(50);
        number.setMaxWidth(400);
        number.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
        number.setOnAction((ActionEvent event) -> {
            onEnter(number);
        });
        grid.add(number, 1, 1, 4, 1);

        if (init) {
            number.setText(initValue + "");
        }

        Button seven = new Button("7");
        seven.setMaxSize(100, 100);
        seven.setMinSize(100, 100);
        HBox hSeven = new HBox(0);
        //hSeven.setAlignment(Pos.TOP_LEFT);
        hSeven.getChildren().add(seven);
        grid.add(hSeven, 1, 2);

        seven.setOnAction((ActionEvent event) -> {
            number.deleteText(number.getSelection().getStart(), number.getSelection().getEnd() + 1);
            number.setText(number.getText() + "7");
        });

        Button eight = new Button("8");
        eight.setMaxSize(100, 100);
        eight.setMinSize(100, 100);
        HBox hEight = new HBox(0);
        //hSeven.setAlignment(Pos.TOP_CENTER);
        hEight.getChildren().add(eight);
        grid.add(hEight, 2, 2);

        eight.setOnAction((ActionEvent event) -> {
            number.deleteText(number.getSelection().getStart(), number.getSelection().getEnd() + 1);
            number.setText(number.getText() + "8");
        });

        Button nine = new Button("9");
        nine.setMaxSize(100, 100);
        nine.setMinSize(100, 100);
        HBox hNine = new HBox(0);
        //hSeven.setAlignment(Pos.TOP_RIGHT);
        hNine.getChildren().add(nine);
        grid.add(hNine, 3, 2);

        nine.setOnAction((ActionEvent event) -> {
            number.deleteText(number.getSelection().getStart(), number.getSelection().getEnd() + 1);
            number.setText(number.getText() + "9");
        });

        Button four = new Button("4");
        four.setMaxSize(100, 100);
        four.setMinSize(100, 100);
        HBox hFour = new HBox(0);
        //hFour.setAlignment(Pos.CENTER_LEFT);
        hFour.getChildren().add(four);
        grid.add(hFour, 1, 3);

        four.setOnAction((ActionEvent event) -> {
            number.deleteText(number.getSelection().getStart(), number.getSelection().getEnd() + 1);
            number.setText(number.getText() + "4");
        });

        Button five = new Button("5");
        five.setMaxSize(100, 100);
        five.setMinSize(100, 100);
        HBox hFive = new HBox(0);
        //hFive.setAlignment(Pos.CENTER);
        hFive.getChildren().add(five);
        grid.add(hFive, 2, 3);

        five.setOnAction((ActionEvent event) -> {
            number.deleteText(number.getSelection().getStart(), number.getSelection().getEnd() + 1);
            number.setText(number.getText() + "5");
        });

        Button six = new Button("6");
        six.setMaxSize(100, 100);
        six.setMinSize(100, 100);
        HBox hSix = new HBox(0);
        //hSix.setAlignment(Pos.CENTER_RIGHT);
        hSix.getChildren().add(six);
        grid.add(hSix, 3, 3);

        six.setOnAction((ActionEvent event) -> {
            number.deleteText(number.getSelection().getStart(), number.getSelection().getEnd() + 1);
            number.setText(number.getText() + "6");
        });

        Button one = new Button("1");
        one.setMaxSize(100, 100);
        one.setMinSize(100, 100);
        HBox hOne = new HBox(0);
        //hOne.setAlignment(Pos.BOTTOM_LEFT);
        hOne.getChildren().add(one);
        grid.add(hOne, 1, 4);

        one.setOnAction((ActionEvent event) -> {
            number.deleteText(number.getSelection().getStart(), number.getSelection().getEnd() + 1);
            number.setText(number.getText() + "1");
        });

        Button two = new Button("2");
        two.setMaxSize(100, 100);
        two.setMinSize(100, 100);
        HBox hTwo = new HBox(0);
        //hTwo.setAlignment(Pos.BOTTOM_CENTER);
        hTwo.getChildren().add(two);
        grid.add(hTwo, 2, 4);

        two.setOnAction((ActionEvent event) -> {
            number.deleteText(number.getSelection().getStart(), number.getSelection().getEnd() + 1);
            number.setText(number.getText() + "2");
        });

        Button three = new Button("3");
        three.setMaxSize(100, 100);
        three.setMinSize(100, 100);
        HBox hThree = new HBox(0);
        //hThree.setAlignment(Pos.BOTTOM_RIGHT);
        hThree.getChildren().add(three);
        grid.add(hThree, 3, 4);

        three.setOnAction((ActionEvent event) -> {
            number.deleteText(number.getSelection().getStart(), number.getSelection().getEnd() + 1);
            number.setText(number.getText() + "3");
        });

        Button zero = new Button("0");
        zero.setMaxSize(200, 100);
        zero.setMinSize(200, 100);
        HBox hZero = new HBox(0);
        hZero.getChildren().add(zero);
        grid.add(hZero, 1, 5, 2, 1);

        zero.setOnAction((ActionEvent event) -> {
            number.deleteText(number.getSelection().getStart(), number.getSelection().getEnd() + 1);
            number.setText(number.getText() + "0");
        });

        Button dZero = new Button("00");
        dZero.setMaxSize(100, 100);
        dZero.setMinSize(100, 100);
        HBox hDzero = new HBox(0);
        hDzero.getChildren().add(dZero);
        grid.add(hDzero, 3, 5);

        dZero.setOnAction((ActionEvent event) -> {
            number.deleteText(number.getSelection().getStart(), number.getSelection().getEnd() + 1);
            number.setText(number.getText() + "00");
        });

        Button clear = new Button("Clear");
        clear.setMaxSize(100, 200);
        clear.setMinSize(100, 200);
        HBox hClear = new HBox(0);
        hClear.getChildren().add(clear);
        grid.add(hClear, 4, 2, 1, 2);

        clear.setOnAction((ActionEvent event) -> {
            onClear(number);
        });

        Button enter = new Button("Enter");
        enter.setMaxSize(100, 200);
        enter.setMinSize(100, 200);
        HBox hEnter = new HBox(0);
        hEnter.getChildren().add(enter);
        grid.add(hEnter, 4, 4, 1, 2);

        enter.setOnAction((ActionEvent event) -> {
            if (!enter.getText().equals("")) {
                onEnter(number);
            }
        });

        Scene scene = new Scene(grid, 400, 450);
        scene.getStylesheets().add((NumberEntry.class.getResource("style.css").toExternalForm()));
        setScene(scene);
    }

    private void onClear(TextField number) {
        if (number.getText().equals("")) {
            hide();
        } else {
            number.setText("");
        }
    }

    private void onEnter(TextField number) {
        if (!number.getText().equals("")) {
            value = Integer.parseInt(number.getText());
        }
        hide();
    }
}
