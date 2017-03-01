/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.davidg95.jtill.javafxjtill;

import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * Class for showing a message over the entire application.
 *
 * @author David
 */
public class MessageScreen extends Stage {

    private static final MessageScreen SCREEN;

    private String message;
    private final Label label;
    private final Scene scene;
    private final String stylesheet;

    public MessageScreen() {
        stylesheet = MainStage.class.getResource("messagescreen.css").toExternalForm();
        initStyle(StageStyle.UNDECORATED);
        BorderPane pane = new BorderPane();

        label = new Label("");
        label.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
        pane.setCenter(label);

        scene = new Scene(pane, 400, 300);
        scene.getStylesheets().add(stylesheet);
        scene.setFill(Paint.valueOf("#FF00FF"));
        setScene(scene);
    }

    static {
        SCREEN = new MessageScreen();
        SCREEN.initModality(Modality.APPLICATION_MODAL);
    }

    /**
     * Shows the window with the current message.
     */
    public static void showWindow() {
        SCREEN.hide();
        SCREEN.show();
    }

    /**
     * Hides the window.
     */
    public static void hideWindow() {
        SCREEN.hide();
    }

    /**
     * Changes the message on the window.
     *
     * @param m the message to display.
     */
    public static void changeMessage(String m) {
        SCREEN.setMessage(m);
    }

    private void setMessage(String message) {
        this.message = message;
        label.setText(this.message);
    }

}
