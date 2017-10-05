/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.davidg95.jtill.javafxjtill;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.text.DecimalFormat;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import io.github.davidg95.JTill.jtill.*;
import java.math.RoundingMode;
import java.util.List;
import javafx.application.Platform;

/**
 *
 * @author David
 */
public class CashUpDialog extends Stage {

    private static Stage dialog;

    private final DataConnect dc;
    private final Till till;

    private Label cashLabel;
    private TextField cashValue;
    private Button declare;
    private Label takingsLabel;
    private TextField takingsField;
    private Label differenceLabel;
    private TextField differenceField;
    private Button close;

    private static int result;

    public CashUpDialog(Window parent, DataConnect dc, Till t) {
        this.dc = dc;
        this.till = t;
        init();
        setTitle("Cash Up");
        initOwner(parent);
        initModality(Modality.APPLICATION_MODAL);
    }

    public static int showDialog(Window parent, DataConnect dc, Till t) {
        dialog = new CashUpDialog(parent, dc, t);
        result = 0;
        dialog.showAndWait();
        return result;
    }

    private void init() {
        GridPane pane = new GridPane();
        pane.setVgap(10);
        pane.setHgap(10);
        pane.setPadding(new Insets(10));

        cashLabel = new Label("Enter value of cash:");
        cashLabel.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));

        cashValue = new TextField("0");
        cashValue.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
        cashValue.setOnMousePressed((MouseEvent event) -> {
            double d;
            if (cashValue.getText().equals("")) {
                d = ((double) NumberEntry.showNumberEntryDialog(this, "Enter cash counted")) / 100;
            } else {
                d = ((double) NumberEntry.showNumberEntryDialog(this, "Enter cash counted", (int) (Double.parseDouble(cashValue.getText()) * 100))) / 100;
            }
            DecimalFormat df;
            if (d >= 1) {
                df = new DecimalFormat("#.00");
            } else {
                df = new DecimalFormat("0.00");
            }
            cashValue.setText(df.format(d) + "");
        });
        cashValue.setOnAction((ActionEvent event) -> {
            declare.fire();
        });

        takingsLabel = new Label("Takings:");
        takingsLabel.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));

        takingsField = new TextField();
        takingsField.setEditable(false);
        takingsField.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));

        differenceLabel = new Label("Difference:");
        differenceLabel.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));

        differenceField = new TextField();
        differenceField.setEditable(false);
        differenceField.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));

        declare = new Button("Declare");
        declare.setMinSize(100, 60);
        declare.setMaxSize(100, 60);
        HBox hDeclare = new HBox(0);
        hDeclare.getChildren().add(declare);
        declare.setOnAction((ActionEvent event) -> {
            new Thread() {
                @Override
                public void run() {
                    declare();
                }
            }.start();
        });

        close = new Button("Close");
        close.setMinSize(100, 60);
        close.setMaxSize(100, 60);
        HBox hClose = new HBox(0);
        hClose.getChildren().add(close);
        close.setOnAction((ActionEvent event) -> {
            hide();
        });

        pane.add(cashLabel, 1, 1);
        pane.add(cashValue, 2, 1);
        pane.add(hDeclare, 2, 2);
        pane.add(takingsLabel, 1, 3);
        pane.add(takingsField, 2, 3);
        pane.add(differenceLabel, 1, 4);
        pane.add(differenceField, 2, 4);
        pane.add(hClose, 3, 5);

        Scene scene = new Scene(pane);
        String stylesheet = MainStage.class.getResource("style.css").toExternalForm();
        scene.getStylesheets().add(stylesheet);
        setScene(scene);

    }

    private void declare() {
        try {
            Platform.runLater(() -> {
                cashValue.setDisable(true);
                MessageScreen.changeMessage("Declaring...");
                MessageScreen.showWindow();
            });
            List<Sale> sales = dc.getTerminalSales(till.getId(), true);
            TillReport tr = new TillReport();
            for (Sale s : sales) {
                tr.actualTakings = tr.actualTakings.add(s.getTotal());
                for (SaleItem si : s.getSaleItems()) {
                    tr.tax = tr.tax.add(si.getTaxValue());
                }
            }
            tr.terminal = till.getName();
            tr.transactions = sales.size();
            tr.actualTakings = tr.actualTakings.setScale(2);
            tr.tax = tr.tax.setScale(2);
            tr.declared = new BigDecimal(cashValue.getText());
            tr.declared = tr.declared.setScale(2);
            tr.difference = tr.declared.subtract(tr.actualTakings);
            final DecimalFormat df = new DecimalFormat("0.00");
            dc.cashUncashedSales(till.getId());
            result = 1;
            final BigDecimal fTakings = tr.actualTakings;
            final BigDecimal fDiff = tr.difference;
            Platform.runLater(() -> {
                takingsField.setText(df.format(fTakings.doubleValue()));
                differenceField.setText(df.format(fDiff.doubleValue()));
                MessageScreen.hideWindow();
            });
            tr.averageSpend = tr.actualTakings.divide(new BigDecimal(tr.transactions), RoundingMode.HALF_DOWN);
            final BigDecimal fValCou = tr.declared;
            Platform.runLater(() -> {
                if (YesNoDialog.showDialog(this, "Cash up", "Do you want the report emailed?") == YesNoDialog.YES) {
                    try {
                        String message = "Cashup for terminal " + JavaFXJTill.NAME
                                + "\nValue counted: £" + fValCou.toString()
                                + "\nActual takings: £" + fTakings.toString()
                                + "\nDifference: £" + fDiff.toString();
                        dc.sendEmail(message);
                    } catch (IOException ex) {
                        MessageDialog.showMessage(this, "Cash Up", "Error sending email");
                    }
                }
            });

            ReportPrinter.initPrinter();
            ReportPrinter.print(tr);
        } catch (Exception ex) {
            Platform.runLater(() -> {
                MessageScreen.hideWindow();
            });
            MessageDialog.showMessage(this, "Cash Up", ex.getMessage());
        } finally {
            Platform.runLater(() -> {
                MessageScreen.hideWindow();
            });
        }
    }
}
