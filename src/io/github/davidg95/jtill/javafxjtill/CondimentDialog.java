/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.davidg95.jtill.javafxjtill;

import io.github.davidg95.JTill.jtill.Product;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyEvent;
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
public class CondimentDialog extends Stage {

    private static CondimentDialog dialog;

    private final Product product;

    public CondimentDialog(Window parent, Product p) {
        this.product = p;
        init();
        setTitle("Condiments for " + p.getName());
        initOwner(parent);
        initModality(Modality.APPLICATION_MODAL);
    }

    public static void showDialog(Window parent, Product p) {
        dialog = new CondimentDialog(parent, p);
        dialog.showAndWait();
    }

    private void init() {
        final int WIDTH = 700;
        GridPane pane = new GridPane();

        String message;
        if (product.getMaxCon() == product.getMinCon()) {
            message = "Select " + product.getMaxCon();
        } else {
            message = "Select between " + product.getMinCon() + " and " + product.getMaxCon();
        }
        Label label = new Label(message);
        label.setAlignment(Pos.CENTER);
        label.setMinHeight(100);
        label.setMaxHeight(100);
        label.setFont(Font.font("Tahoma", FontWeight.NORMAL, 25));
        pane.add(label, 0, 0);

        Button cancel = new Button("Cancel");
        HBox hCancel = new HBox(0);
        hCancel.getChildren().add(cancel);

        Button startAgain = new Button("Start Again");
        HBox hStart = new HBox(0);
        hStart.getChildren().add(startAgain);

        Button complete = new Button("Complete");
        complete.setMaxSize(WIDTH, 100);
        complete.setMinSize(WIDTH, 100);
        complete.setFont(Font.font("Tahoma", FontWeight.NORMAL, 40));
        HBox hComplete = new HBox(0);
        hComplete.getChildren().add(complete);
        complete.setOnAction((ActionEvent event) -> {
            hide();
        });
        complete.setOnKeyPressed((KeyEvent ke) -> {
            hide();
        });

        pane.add(hCancel, 4, 2);
        pane.add(hStart, 4, 3);
        pane.add(hComplete, 4, 4);

        Scene scene = new Scene(pane, WIDTH, 200);
        String stylesheet = MainStage.class.getResource("style.css").toExternalForm();
        scene.getStylesheets().add(stylesheet);
        setScene(scene);
    }

}
