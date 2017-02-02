/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.davidg95.jtill.javafxjtill;

import io.github.davidg95.JTill.jtill.Sale;
import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;

/**
 *
 * @author David
 */
public class PaymentScene extends Scene {

    public PaymentScene(GridPane parent, double width, double height, Sale sale) {
        super(parent, width, height);
        init();
    }

    private void init() {
        GridPane pane = new GridPane();

        Button fivePounds = new Button("£5");
        fivePounds.setMaxSize(150, 150);
        fivePounds.setMinSize(150, 150);
        HBox hFive = new HBox(0);
        hFive.getChildren().add(fivePounds);
        pane.add(hFive, 1, 1);
        fivePounds.setOnAction((ActionEvent event) -> {

        });

        Button tenPounds = new Button("£10");
        tenPounds.setMaxSize(150, 150);
        tenPounds.setMinSize(150, 150);
        HBox hTen = new HBox(0);
        hTen.getChildren().add(tenPounds);
        pane.add(hTen, 2, 1);
        tenPounds.setOnAction((ActionEvent event) -> {

        });

        super.setRoot(pane);
    }
}
