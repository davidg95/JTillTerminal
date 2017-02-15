/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.davidg95.jtill.javafxjtill;

import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

/**
 *
 * @author David
 */
public class ModalMessage extends Stage {

    private static ModalMessage dialog;

    private final String message;

    public ModalMessage(Window parent, String message, String title) {
        this.message = message;
        init();
        initOwner(parent);
        initModality(Modality.WINDOW_MODAL);
        setTitle(title);
    }

    public static ModalMessage showDialog(Window parent, String title, String message) {
        dialog = new ModalMessage(parent, message, title);
        dialog.show();
        return dialog;
    }

    private void init() {
        GridPane pane = new GridPane();

        Label label = new Label(message);

        pane.add(label, 0, 0);

        Scene scene = new Scene(pane, 400, 300);
        String stylesheet = MainStage.class.getResource("style.css").toExternalForm();
        scene.getStylesheets().add(stylesheet);
        setScene(scene);
    }

    public void clear() {
        hide();
    }
}
