/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.davidg95.jtill.javafxjtill;

import io.github.davidg95.JTill.jtill.*;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.UnknownHostException;
import java.util.Properties;
import javafx.application.Application;
import javafx.stage.Stage;

/**
 *
 * @author David
 */
public class JavaFXJTill extends Application {

    private ServerConnection sc;
    private static Properties properties;
    public static String NAME;
    public static String HOST;
    public static int PORT = 600;

    boolean retry;

    @Override
    public void start(Stage primaryStage) {
        loadProperties();
        retry = true;
        tryConnect();
        while (retry) {
            tryConnect();
        }
    }

    private void tryConnect() {
        try {
            sc = new ServerConnection(NAME);
            MainStage mainStage = new MainStage(sc);
            sc.setGUI(mainStage);
            sc.connect(HOST, PORT);
            mainStage.initalise();
            mainStage.show();
            retry = false;
        } catch (IOException ex) {
            if (YesNoDialog.showDialog(null, "Connection Failed", "Do you want to attempt to connect?") == YesNoDialog.NO) {
                System.exit(0);
            }
            SetupDialog.showDialog(null);
            saveProperties();
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

    public void loadProperties() {
        properties = new Properties();
        InputStream in;

        try {
            in = new FileInputStream("server.properties");

            properties.load(in);

            NAME = properties.getProperty("name");
            HOST = properties.getProperty("host");
            PORT = Integer.parseInt(properties.getProperty("port", Integer.toString(PORT)));

            in.close();
        } catch (FileNotFoundException | UnknownHostException ex) {
            SetupDialog.showDialog(null);
            saveProperties();
        } catch (IOException ex) {
        }
    }

    public void saveProperties() {
        properties = new Properties();
        OutputStream out;

        try {
            out = new FileOutputStream("server.properties");

            properties.setProperty("name", NAME);
            properties.setProperty("host", HOST);
            properties.setProperty("port", Integer.toString(PORT));

            properties.store(out, null);
            out.close();
        } catch (FileNotFoundException | UnknownHostException ex) {
        } catch (IOException ex) {
        }
    }

}
