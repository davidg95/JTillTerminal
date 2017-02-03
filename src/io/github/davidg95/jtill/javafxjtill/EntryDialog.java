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
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

/**
 *
 * @author David
 */
public class EntryDialog extends Stage {

    private static Stage dialog;
    private static String s;

    private String message;

    public EntryDialog(Window parent, String title, String message) {
        init();
        setTitle(title);
        initOwner(parent);
        initModality(Modality.APPLICATION_MODAL);
    }

    public static String show(Window parent, String title, String message) {
        dialog = new EntryDialog(parent, title, message);
        message = "";
        dialog.showAndWait();
        return s;
    }

    private void init() {
        GridPane pane = new GridPane();

        Text txtMessage = new Text(message);
        //pane.add(txtMessage, 0, 0);

        TextField input = new TextField();
        input.setMaxSize(300, 30);
        input.setMinSize(300, 30);
        input.setOnAction((ActionEvent event) -> {
            onEnter(input.getText());
        });
        pane.add(input, 0, 1);

        Button enter = new Button("Enter");
        HBox hEnter = new HBox(10);
        enter.setMaxSize(300, 50);
        enter.setMinSize(300, 50);
        hEnter.getChildren().add(enter);
        enter.setOnAction((ActionEvent event) -> {
            onEnter(input.getText());
        });
        pane.add(enter, 0, 2);

        Scene scene = new Scene(pane, 300, 60);
        setScene(scene);
    }

    private void onEnter(String text) {
        s = text;
        hide();
    }

}
