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

    /**
     * The data connection.
     */
    private static DataConnect dc;
    /**
     * The properties for the terminal.
     */
    private static Properties properties;
    /**
     * The name of the terminal.
     */
    public static String NAME;
    /**
     * The server address.
     */
    public static String SERVER;
    /**
     * The server port number. Default is 52341.
     */
    public static int PORT = 52341;
    /**
     * The graphics user interface object.
     */
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
        LOG.log(Level.INFO, "Loading properties file");
        properties = new Properties();
        InputStream in;

        try {
            in = new FileInputStream("server.properties");

            properties.load(in);

            NAME = properties.getProperty("name");
            SERVER = properties.getProperty("host");
            PORT = Integer.parseInt(properties.getProperty("port", Integer.toString(PORT)));

            in.close();
        } catch (FileNotFoundException | UnknownHostException ex) {
            LOG.log(Level.INFO, "Properties file not found, going into initial configuration");
            SetupDialog.showDialog(null);
            saveProperties();
        } catch (IOException ex) {
        }
    }

    public static void saveProperties() {
        LOG.log(Level.INFO, "Saving properties file");
        properties = new Properties();
        OutputStream out;

        try {
            out = new FileOutputStream("server.properties");

            properties.setProperty("name", NAME);
            properties.setProperty("host", SERVER);
            properties.setProperty("port", Integer.toString(PORT));

            properties.store(out, null);
            out.close();
        } catch (FileNotFoundException | UnknownHostException ex) {
        } catch (IOException ex) {
        }
    }

}
