/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.davidg95.jtill.javafxjtill;

import javafx.event.ActionEvent;
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
public class YesNoDialog extends Stage {

    private static Stage dialog;
    private static int result;

    public static final int YES = 1;
    public static final int NO = 0;

    private final String message;

    public YesNoDialog(Window parent, String title, String message) {
        this.message = message;
        init();
        setTitle(title);
        initOwner(parent);
        initModality(Modality.APPLICATION_MODAL);
    }

    public static int showDialog(Window parent, String title, String message) {
        dialog = new YesNoDialog(parent, title, message);
        result = NO;
        dialog.showAndWait();
        return result;
    }

    private void init() {
        GridPane pane = new GridPane();
        Label label = new Label(message);
        label.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
        label.setMinSize(600, 50);
        label.setMaxSize(600, 50);
        pane.add(label, 0, 0, 2, 1);

        Button yes = new Button("Yes");
        yes.setId("blue");
        yes.setMinSize(300, 100);
        yes.setMaxSize(300, 100);
        HBox hYes = new HBox(0);
        hYes.getChildren().add(yes);
        yes.setOnAction((ActionEvent event) -> {
            result = YES;
            hide();
        });
        pane.add(hYes, 0, 1);

        Button no = new Button("No");
        no.setId("blue");
        no.setMinSize(300, 100);
        no.setMaxSize(300, 100);
        HBox hNo = new HBox(0);
        hNo.getChildren().add(no);
        no.setOnAction((ActionEvent event) -> {
            result = NO;
            hide();
        });
        pane.add(hNo, 1, 1);

        Scene scene = new Scene(pane, 600, 150);
        String stylesheet = MainStage.class.getResource("style.css").toExternalForm();
        scene.getStylesheets().add(stylesheet);
        setScene(scene);
    }
}
