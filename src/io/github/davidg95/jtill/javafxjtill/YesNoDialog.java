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
    private static Result result;

    private String message;

    public enum Result {
        YES, NO,
    }

    public YesNoDialog(Window parent, String title, String message) {
        this.message = message;
        init();
        setTitle(title);
        initOwner(parent);
        initModality(Modality.APPLICATION_MODAL);
    }

    public static Result showDialog(Window parent, String title, String message) {
        dialog = new YesNoDialog(parent, title, message);
        result = Result.NO;
        dialog.showAndWait();
        return result;
    }

    private void init() {
        GridPane pane = new GridPane();
        Label label = new Label(message);
        label.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
        label.setMinSize(400, 40);
        label.setMaxSize(400, 40);
        pane.add(label, 0, 0, 2, 1);

        Button yes = new Button("Yes");
        yes.setMinSize(200, 50);
        yes.setMaxSize(200, 50);
        HBox hYes = new HBox(0);
        hYes.getChildren().add(yes);
        yes.setOnAction((ActionEvent event) -> {
            result = Result.YES;
            hide();
        });
        pane.add(hYes, 0, 1);

        Button no = new Button("No");
        no.setMinSize(200, 50);
        no.setMaxSize(200, 50);
        HBox hNo = new HBox(0);
        hNo.getChildren().add(no);
        no.setOnAction((ActionEvent event) -> {
            result = Result.NO;
            hide();
        });
        pane.add(hNo, 1, 1);

        Scene scene = new Scene(pane, 400, 100);
        String stylesheet = MainStage.class.getResource("style.css").toExternalForm();
        scene.getStylesheets().add(stylesheet);
        setScene(scene);
    }
}
