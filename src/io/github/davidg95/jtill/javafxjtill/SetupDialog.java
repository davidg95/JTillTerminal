/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.davidg95.jtill.javafxjtill;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
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
import javax.print.attribute.Attribute;

/**
 * The setup dialog for the terminal system.
 *
 * @author David
 */
public class SetupDialog extends Stage {

    private static Stage dialog;

    /**
     * Constructor which initialises the SetupDialog.
     *
     * @param parent the parent stage.
     */
    public SetupDialog(MainStage parent) {
        this(parent, null);
    }

    /**
     * Constructor which initialises the SetupDialog. It uses the properties to
     * fill the text boxes with default values.
     *
     * @param parent the parent stage.
     * @param properties the properties for filling the text boxes.
     */
    public SetupDialog(MainStage parent, Properties properties) {
        init();
        setTitle("JTill Setup");
        initOwner(parent);
        initModality(Modality.APPLICATION_MODAL);
    }

    /**
     * Method which creates and shows an instance of the SetupDialog
     *
     * @param parent the parent stage.
     */
    public static void showDialog(MainStage parent) {
        showDialog(parent, null);
    }

    /**
     * Method which creates and shows an instance of the SetupDialog. The
     * properties object will be used to fill the text boxes with default
     * values.
     *
     * @param parent the parent stage.
     * @param properties the properties for filling the text boxes.
     */
    public static void showDialog(MainStage parent, Properties properties) {
        dialog = new SetupDialog(parent);
        dialog.showAndWait();
    }

    /**
     * Create the components.
     */
    private void init() {
        //Create the grid pane of 10x10 and set the insets to 25.
        GridPane pane = new GridPane();
        pane.setHgap(10);
        pane.setVgap(10);
        pane.setPadding(new Insets(25));

        //Create the labels.
        Label terminalLabel = new Label("Enter Terminal Name:"); //Label for the terminal name.
        Label serverLabel = new Label("Enter Server Address:"); //Label for the server address.
        Label portLabel = new Label("Enter Server Port Number:"); //Label for the server port.

        if (JavaFXJTill.NAME == null) {
            try {
                JavaFXJTill.NAME = InetAddress.getLocalHost().getHostName();
            } catch (UnknownHostException ex) {
                JavaFXJTill.NAME = "";
            }
        }

        //Create the text fields.
        TextField terminalName = new TextField(JavaFXJTill.NAME); //Text field for the terminal name.
        TextField serverAddress = new TextField(JavaFXJTill.SERVER); //Text field for the server address.
        TextField serverPort = new TextField(Integer.toString(JavaFXJTill.PORT)); //Text field for the server port.

        //Button for displaying the printer settings.
        Button printerSettings = new Button("Printer settings");
        printerSettings.setOnAction((ActionEvent evt) -> {
            ReceiptPrinter.initPrinter();
            File file = new File("printer.settings");
            Attribute[] set = ReceiptPrinter.getJob().getPrintService().getAttributes().toArray();
            FileWriter fw = null;
            try {
                fw = new FileWriter(file);
                for (Attribute a : set) {
                    fw.append(a.toString());
                }
            } catch (IOException ex) {
                Logger.getLogger(SetupDialog.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                try {
                    if (fw != null) {
                        fw.flush();
                        fw.close();
                    }
                } catch (IOException ex) {
                    Logger.getLogger(SetupDialog.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });

        //Enter button which saves the changes and hides the frame.
        Button enter = new Button("Enter");
        HBox hEnter = new HBox(10);
        hEnter.getChildren().add(enter);
        enter.setOnAction((ActionEvent event) -> {
            JavaFXJTill.NAME = terminalName.getText();
            JavaFXJTill.SERVER = serverAddress.getText();
            JavaFXJTill.PORT = Integer.parseInt(serverPort.getText());
            hide();
        });

        //Add all the components.
        pane.add(terminalLabel, 1, 1);
        pane.add(terminalName, 2, 1);
        pane.add(serverLabel, 1, 2);
        pane.add(serverAddress, 2, 2);
        pane.add(portLabel, 1, 3);
        pane.add(serverPort, 2, 3);
        pane.add(printerSettings, 1, 4);
        pane.add(hEnter, 2, 4);

        //Construct the scene.
        Scene scene = new Scene(pane, 360, 200);
        String stylesheet = MainStage.class.getResource("style.css").toExternalForm();
        scene.getStylesheets().add(stylesheet);
        setScene(scene);
    }
}
