/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.davidg95.jtill.javafxjtill;

import io.github.davidg95.JTill.jtill.*;
import java.util.LinkedList;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
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
    private static List<Condiment> condiments;

    private final Product product;

    public CondimentDialog(Window parent, Product p) {
        this.product = p;
        init();
        setTitle("Condiments for " + p.getName());
        initOwner(parent);
        initModality(Modality.APPLICATION_MODAL);
    }

    public static List<Condiment> showDialog(Window parent, Product p) {
        dialog = new CondimentDialog(parent, p);
        condiments = new LinkedList<>();
        dialog.showAndWait();
        return condiments;
    }

    private void init() {
        GridPane pane = new GridPane();

        ListView<Condiment> items = new ListView();
        items.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        ObservableList<Condiment> obItems = FXCollections.observableArrayList();
        items.setItems(obItems);

        items.setStyle("-fx-text-fill: white;\n"
                + "    -fx-font-family: \"Tahoma\";\n"
                + "    -fx-font-size: 28;");

        String message;
        if (product.getMaxCon() == -2) {
            message = "Select a minimum of " + product.getMinCon() + " items";
        } else if (product.getMaxCon() == product.getMinCon()) {
            message = "Select " + product.getMaxCon() + (product.getMaxCon() == 1 ? " item" : " items");
        } else {
            message = "Select between " + product.getMinCon() + " and " + product.getMaxCon() + " items";
        }
        Label selectLabel = new Label(message);
        selectLabel.setAlignment(Pos.CENTER);
        selectLabel.setMinHeight(100);
        selectLabel.setMaxHeight(100);
        selectLabel.setFont(Font.font("Tahoma", FontWeight.NORMAL, 25));
        
        pane.add(selectLabel, 0, 0, 4, 1);

        Button cancel = new Button("Cancel");
        Button startAgain = new Button("Start Again");
        Button complete = new Button("Complete");

        int row = 1;
        int col = 0;
        for (Condiment c : product.getCondiments()) {
            Button b = new Button(c.getProduct_con().getName());
            b.setMinSize(240, 140);
            b.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
            HBox hb = new HBox(0);
            hb.getChildren().add(b);
            b.setOnAction((ActionEvent e) -> {
                condiments.add(c);
                obItems.add(c);
                items.refresh();
                if (condiments.size() >= product.getMinCon()) {
                    complete.setDisable(false);
                }
                if (condiments.size() == product.getMaxCon()) {
                    hide();
                }
            });
            pane.add(hb, col, row);
            col++;
            if (col == 4) {
                col = 0;
                row++;
            }
        }
        row++;
        cancel.setFont(Font.font("Tahoma", FontWeight.NORMAL, 35));
        HBox hCancel = new HBox(0);
        hCancel.getChildren().add(cancel);
        cancel.setOnAction((ActionEvent event) -> {
            condiments = null;
            hide();
        });

        startAgain.setFont(Font.font("Tahoma", FontWeight.NORMAL, 35));
        HBox hStart = new HBox(0);
        hStart.getChildren().add(startAgain);
        startAgain.setOnAction((ActionEvent e) -> {
            condiments.clear();
            obItems.clear();
            items.refresh();
            if (product.getMinCon() == 0) {
                complete.setDisable(false);
            } else {
                complete.setDisable(true);
            }
        });

        complete.setFont(Font.font("Tahoma", FontWeight.NORMAL, 35));
        if (product.getMinCon() == 0) {
            complete.setDisable(false);
        } else {
            complete.setDisable(true);
        }
        HBox hComplete = new HBox(0);
        hComplete.getChildren().add(complete);
        complete.setOnAction((ActionEvent event) -> {
            hide();
        });
        complete.setOnKeyPressed((KeyEvent ke) -> {
            hide();
        });

        pane.add(hCancel, 0, row, 2, 1);
        pane.add(hStart, 5, row);
        pane.add(hComplete, 6, row);
        pane.add(items, 5, 0, 3, row);

        Scene scene = new Scene(pane, null);
        this.setResizable(false);
//        String stylesheet = MainStage.class.getResource("style.css").toExternalForm();
//        scene.getStylesheets().add(stylesheet);
        setScene(scene);

        this.setOnCloseRequest(event -> {
            condiments.clear();
        });
    }

}
