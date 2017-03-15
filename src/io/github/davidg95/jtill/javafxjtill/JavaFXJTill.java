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
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.stage.Stage;

/**
 *
 * @author David
 */
public class JavaFXJTill extends Application {
    private static final Logger LOG = Logger.getGlobal();

    private static DataConnect dc;
    private static Properties properties;
    public static String NAME;
    public static String HOST;
    public static int PORT = 52341;
    private static MainStage mainStage;

    @Override
    public void start(Stage primaryStage) {
        LOG.addHandler(new LogFileHandler());
        LOG.log(Level.INFO, "Starting JTill Terminal");
        dc = new ServerConnection();
        //dc = new DummyData();
        mainStage = new MainStage(dc);
        LOG.log(Level.INFO, "Initalising");
        mainStage.initalise();
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

    public static void loadProperties() {
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

    public static void saveProperties() {
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
