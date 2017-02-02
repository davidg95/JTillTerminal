/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.davidg95.jtill.javafxjtill;

import io.github.davidg95.JTill.jtill.*;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.stage.Stage;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

/**
 *
 * @author David
 */
public class JavaFXJTill extends Application {

    private ServerConnection sc;

    @Override
    public void start(Stage primaryStage) {
        sc = new ServerConnection("JavaFX");
//        primaryStage.setTitle("Login to JTill");
//        GridPane grid = new GridPane();
//        grid.setAlignment(Pos.CENTER);
//        grid.setHgap(10);
//        grid.setVgap(10);
//        grid.setPadding(new Insets(25, 25, 25, 25));
//
//        Text scenetitle = new Text("Welcome to JTill");
//        scenetitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
//        grid.add(scenetitle, 0, 0, 2, 1);
//
//        Label userName = new Label("User Name:");
//        grid.add(userName, 0, 1);
//
//        TextField username = new TextField();
//        grid.add(username, 1, 1);
//
//        Label pw = new Label("Password:");
//        grid.add(pw, 0, 2);
//
//        PasswordField pwbox = new PasswordField();
//        grid.add(pwbox, 1, 2);
//
//        Button login = new Button("Sign in");
//        HBox hbBtn = new HBox(10);
//        hbBtn.setAlignment(Pos.BOTTOM_RIGHT);
//        hbBtn.getChildren().add(login);
//        grid.add(hbBtn, 1, 4);
//
//        final Text action = new Text();
//        grid.add(action, 1, 6);
//        action.setId("action");
//
//        pwbox.setOnAction(new EventHandler<ActionEvent>() {
//            @Override
//            public void handle(ActionEvent event) {
//                login.fire();
//            }
//
//        });
//
//        login.setOnAction(new EventHandler<ActionEvent>() {
//            @Override
//            public void handle(ActionEvent e) {
//                String user = username.getText();
//                String pass = pwbox.getText();
//                try {
//                    Staff s = sc.login(user, pass);
//                    action.setFill(Color.FIREBRICK);
//                    action.setText(s.getName() + " has signed in");
////                    Alert alert = new Alert(AlertType.CONFIRMATION);
////                    alert.setTitle("Login Successful");
////                    alert.setHeaderText(s.getName());
////                    alert.setContentText("You have logged into JTill");
////                    alert.showAndWait();
//                    MainStage stage = new MainStage(s, sc);
//                    stage.show();
//                    primaryStage.hide();
//                } catch (IOException | LoginException | SQLException ex) {
//                    action.setFill(Color.FIREBRICK);
//                    action.setText(ex.getMessage());
//                }
//            }
//        });

        try {
            sc.connect("127.0.0.1", 600);
            MainStage mainStage = new MainStage(sc);
            mainStage.show();
        } catch (IOException ex) {
//            action.setFill(Color.FIREBRICK);
//            action.setText("Error connecting to JTill");
//            username.setDisable(true);
//            pwbox.setDisable(true);
//            login.setDisable(true);
        }

//        Scene scene = new Scene(grid, 300, 275);
//        primaryStage.setScene(scene);
//        primaryStage.show();
    }

    private void addButtons(List<io.github.davidg95.JTill.jtill.Button> buttons, GridPane grid) {
        int x = 1;
        int y = 2;
        for (io.github.davidg95.JTill.jtill.Button b : buttons) {
            Button button = new Button(b.getName());
            button.setMaxSize(100, 40);
            button.setMinSize(100, 40);
            HBox hbBtn = new HBox(10);
            hbBtn.setAlignment(Pos.BOTTOM_RIGHT);
            hbBtn.getChildren().add(button);
            grid.add(hbBtn, x, y);

            x++;
            if (x == 5) {
                x = 1;
                y++;
            }

            button.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent e) {
                    Product p = b.getProduct();
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Product");
                    alert.setHeaderText(p.getName());
                    alert.setContentText(p.toString());
                    alert.showAndWait();
                }
            });
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

}
