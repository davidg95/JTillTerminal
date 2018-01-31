/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.davidg95.jtill.javafxjtill;

import io.github.davidg95.JTill.jtill.Utilities;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
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

    private final String title;

    private Pane canvas;

    public NumberEntry(Window parent, String title) {
        init = false;
        this.title = title;
        init();
        setTitle(title);
        initOwner(parent);
        initModality(Modality.APPLICATION_MODAL);
        initStyle(StageStyle.UNDECORATED);
        makeDraggable(this, canvas);
    }

    public NumberEntry(Window parent, String title, int initValue) {
        init = true;
        this.title = title;
        this.initValue = initValue;
        init();
        setTitle(title);
        initOwner(parent);
        initModality(Modality.APPLICATION_MODAL);
        initStyle(StageStyle.UNDECORATED);
        makeDraggable(this, canvas);
    }

    private static class Delta {

        private double x;
        private double y;
    }

    public static void makeDraggable(final Stage stage, final Node byNode) {
        Delta d = new Delta();
        byNode.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                // record a delta distance for the drag and drop operation.
                d.x = stage.getX() - mouseEvent.getScreenX();
                d.y = stage.getY() - mouseEvent.getScreenY();
                byNode.setCursor(Cursor.MOVE);
            }
        });
        byNode.setOnMouseReleased(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                byNode.setCursor(Cursor.HAND);
            }
        });
        byNode.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                stage.setX(mouseEvent.getScreenX() + d.x);
                stage.setY(mouseEvent.getScreenY() + d.y);
            }
        });
        byNode.setOnMouseEntered(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if (!mouseEvent.isPrimaryButtonDown()) {
                    byNode.setCursor(Cursor.HAND);
                }
            }
        });
        byNode.setOnMouseExited(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if (!mouseEvent.isPrimaryButtonDown()) {
                    byNode.setCursor(Cursor.DEFAULT);
                }
            }
        });
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
        grid.setId("container");

        Scene scene = new Scene(grid, 404, 482);

        TextField number = new TextField();
        number.setMaxHeight(50);
        number.setMaxWidth(400);
        number.setMinHeight(50);
        number.setMaxWidth(400);
        number.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
        number.setOnAction((ActionEvent event) -> {
            onEnter(number);
        });
        grid.add(number, 1, 2, 4, 1);

        canvas = new Pane();
        canvas.setId("topbar");
        canvas.setPrefWidth(400);
        canvas.setPrefHeight(28);
        Label label = new Label(title);
        label.setId("title");
        canvas.getChildren().add(label);
        grid.add(canvas, 1, 1, 4, 1);

        if (init) {
            number.setText(initValue + "");
        }

        Button seven = new Button("7");
        seven.setId("number");
        seven.setMaxSize(100, 100);
        seven.setMinSize(100, 100);
        HBox hSeven = new HBox(0);
        //hSeven.setAlignment(Pos.TOP_LEFT);
        hSeven.getChildren().add(seven);
        grid.add(hSeven, 1, 3);

        seven.setOnAction((ActionEvent event) -> {
            if (init) {
                number.clear();
                init = false;
            }
            number.setText(number.getText() + "7");
        });

        Button eight = new Button("8");
        eight.setId("number");
        eight.setMaxSize(100, 100);
        eight.setMinSize(100, 100);
        HBox hEight = new HBox(0);
        //hSeven.setAlignment(Pos.TOP_CENTER);
        hEight.getChildren().add(eight);
        grid.add(hEight, 2, 3);

        eight.setOnAction((ActionEvent event) -> {
            if (init) {
                number.clear();
                init = false;
            }
            number.setText(number.getText() + "8");
        });

        Button nine = new Button("9");
        nine.setId("number");
        nine.setMaxSize(100, 100);
        nine.setMinSize(100, 100);
        HBox hNine = new HBox(0);
        //hSeven.setAlignment(Pos.TOP_RIGHT);
        hNine.getChildren().add(nine);
        grid.add(hNine, 3, 3);

        nine.setOnAction((ActionEvent event) -> {
            if (init) {
                number.clear();
                init = false;
            }
            number.setText(number.getText() + "9");
        });

        Button four = new Button("4");
        four.setId("number");
        four.setMaxSize(100, 100);
        four.setMinSize(100, 100);
        HBox hFour = new HBox(0);
        //hFour.setAlignment(Pos.CENTER_LEFT);
        hFour.getChildren().add(four);
        grid.add(hFour, 1, 4);

        four.setOnAction((ActionEvent event) -> {
            if (init) {
                number.clear();
                init = false;
            }
            number.setText(number.getText() + "4");
        });

        Button five = new Button("5");
        five.setId("number");
        five.setMaxSize(100, 100);
        five.setMinSize(100, 100);
        HBox hFive = new HBox(0);
        //hFive.setAlignment(Pos.CENTER);
        hFive.getChildren().add(five);
        grid.add(hFive, 2, 4);

        five.setOnAction((ActionEvent event) -> {
            if (init) {
                number.clear();
                init = false;
            }
            number.setText(number.getText() + "5");
        });

        Button six = new Button("6");
        six.setId("number");
        six.setMaxSize(100, 100);
        six.setMinSize(100, 100);
        HBox hSix = new HBox(0);
        //hSix.setAlignment(Pos.CENTER_RIGHT);
        hSix.getChildren().add(six);
        grid.add(hSix, 3, 4);

        six.setOnAction((ActionEvent event) -> {
            if (init) {
                number.clear();
                init = false;
            }
            number.setText(number.getText() + "6");
        });

        Button one = new Button("1");
        one.setId("number");
        one.setMaxSize(100, 100);
        one.setMinSize(100, 100);
        HBox hOne = new HBox(0);
        //hOne.setAlignment(Pos.BOTTOM_LEFT);
        hOne.getChildren().add(one);
        grid.add(hOne, 1, 5);

        one.setOnAction((ActionEvent event) -> {
            if (init) {
                number.clear();
                init = false;
            }
            number.setText(number.getText() + "1");
        });

        Button two = new Button("2");
        two.setId("number");
        two.setMaxSize(100, 100);
        two.setMinSize(100, 100);
        HBox hTwo = new HBox(0);
        //hTwo.setAlignment(Pos.BOTTOM_CENTER);
        hTwo.getChildren().add(two);
        grid.add(hTwo, 2, 5);

        two.setOnAction((ActionEvent event) -> {
            if (init) {
                number.clear();
                init = false;
            }
            number.setText(number.getText() + "2");
        });

        Button three = new Button("3");
        three.setId("number");
        three.setMaxSize(100, 100);
        three.setMinSize(100, 100);
        HBox hThree = new HBox(0);
        //hThree.setAlignment(Pos.BOTTOM_RIGHT);
        hThree.getChildren().add(three);
        grid.add(hThree, 3, 5);

        three.setOnAction((ActionEvent event) -> {
            if (init) {
                number.clear();
                init = false;
            }
            number.setText(number.getText() + "3");
        });

        Button zero = new Button("0");
        zero.setId("number");
        zero.setMaxSize(200, 100);
        zero.setMinSize(200, 100);
        HBox hZero = new HBox(0);
        hZero.getChildren().add(zero);
        grid.add(hZero, 1, 6, 2, 1);

        zero.setOnAction((ActionEvent event) -> {
            if (init) {
                number.clear();
                init = false;
            }
            number.setText(number.getText() + "0");
        });

        Button dZero = new Button("00");
        dZero.setId("number");
        dZero.setMaxSize(100, 100);
        dZero.setMinSize(100, 100);
        HBox hDzero = new HBox(0);
        hDzero.getChildren().add(dZero);
        grid.add(hDzero, 3, 6);

        dZero.setOnAction((ActionEvent event) -> {
            if (init) {
                number.clear();
                init = false;
            }
            number.setText(number.getText() + "00");
        });

        Button clear = new Button("Clear");
        clear.setId("number");
        clear.setMaxSize(100, 200);
        clear.setMinSize(100, 200);
        HBox hClear = new HBox(0);
        hClear.getChildren().add(clear);
        grid.add(hClear, 4, 3, 1, 2);

        clear.setOnAction((ActionEvent event) -> {
            onClear(number);
        });

        Button enter = new Button("Enter");
        enter.setId("number");
        enter.setMaxSize(100, 200);
        enter.setMinSize(100, 200);
        HBox hEnter = new HBox(0);
        hEnter.getChildren().add(enter);
        grid.add(hEnter, 4, 5, 1, 2);

        enter.setOnAction((ActionEvent event) -> {
            if (!enter.getText().equals("")) {
                onEnter(number);
            }
        });

        scene.getStylesheets().add((NumberEntry.class.getResource("style.css").toExternalForm()));
        scene.getStylesheets().add((NumberEntry.class.getResource("numberDialog.css").toExternalForm()));
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
            if (Utilities.isNumber(number.getText())) {
                try {
                    value = Integer.parseInt(number.getText());
                    hide();
                } catch (NumberFormatException ex) {

                }
            } else {
                MessageDialog.showMessage(this, "Invalid Input", "Invalid input");
            }
        }
    }
}
