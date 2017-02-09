/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.davidg95.jtill.javafxjtill;

import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

/**
 *
 * @author David
 */
public class SetupDialog extends Stage {

    private static Stage dialog;

    public SetupDialog(Window parent) {
        init();
        setTitle("JTill Setup");
        initOwner(parent);
        initModality(Modality.APPLICATION_MODAL);
    }

    public static void showDialog(Window parent) {
        dialog = new SetupDialog(parent);
        dialog.showAndWait();
    }

    private void init() {
        GridPane pane = new GridPane();
        pane.setHgap(10);
        pane.setVgap(10);
        pane.setPadding(new Insets(25));

        Label terminalLabel = new Label("Enter Terminal Name:");
        Label serverLabel = new Label("Enter Server Address:");
        Label portLabel = new Label("Enter Server Port Number:");

        TextField terminalName = new TextField(JavaFXJTill.NAME);
        TextField serverAddress = new TextField(JavaFXJTill.HOST);
        TextField serverPort = new TextField(Integer.toString(JavaFXJTill.PORT));

        Button enter = new Button("Enter");
        HBox hEnter = new HBox(10);
        hEnter.getChildren().add(enter);
        enter.setOnAction((ActionEvent event) -> {
            JavaFXJTill.NAME = terminalName.getText();
            JavaFXJTill.HOST = serverAddress.getText();
            JavaFXJTill.PORT = Integer.parseInt(serverPort.getText());
            hide();
        });

        pane.add(terminalLabel, 1, 1);
        pane.add(terminalName, 2, 1);
        pane.add(serverLabel, 1, 2);
        pane.add(serverAddress, 2, 2);
        pane.add(portLabel, 1, 3);
        pane.add(serverPort, 2, 3);
        pane.add(hEnter, 2, 4);

        Scene scene = new Scene(pane, 360, 200);
        String stylesheet = MainStage.class.getResource("style.css").toExternalForm();
        scene.getStylesheets().add(stylesheet);
        setScene(scene);
    }
}
