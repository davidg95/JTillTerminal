/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.davidg95.jtill.javafxjtill;

import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
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
public class MessageDialog extends Stage {

    private static MessageDialog dialog;

    private final String message;

    public MessageDialog(Window parent, String title, String message) {
        this.message = message;
        init();
        setTitle(title);
        initOwner(parent);
        initModality(Modality.APPLICATION_MODAL);
    }

    public static void showMessage(Window parent, String title, String message) {
        dialog = new MessageDialog(parent, title, message);
        dialog.showAndWait();
    }
    
    public static void showMessageNoWait(Window parent, String title, String message) {
        dialog = new MessageDialog(parent, title, message);
        dialog.show();
    }

    private void init() {
        final int WIDTH = 700;
        GridPane pane = new GridPane();

        Label label = new Label(message);
        label.setAlignment(Pos.CENTER);
        label.setMinHeight(100);
        label.setMaxHeight(100);
        label.setFont(Font.font("Tahoma", FontWeight.NORMAL, 40));
        pane.add(label, 0, 0);

        Button button = new Button("Ok");
        button.setMaxSize(WIDTH, 100);
        button.setMinSize(WIDTH, 100);
        button.setFont(Font.font("Tahoma", FontWeight.NORMAL, 40));
        HBox hButton = new HBox(0);
        hButton.getChildren().add(button);
        button.setOnAction((ActionEvent event) -> {
            hide();
        });
        pane.add(hButton, 0, 1);

        Scene scene = new Scene(pane, WIDTH, 200);
        String stylesheet = MainStage.class.getResource("dialog.css").toExternalForm();
        scene.getStylesheets().add(stylesheet);
        setScene(scene);
    }
}
