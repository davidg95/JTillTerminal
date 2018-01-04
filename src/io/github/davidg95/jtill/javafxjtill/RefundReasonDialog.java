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
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

/**
 *
 * @author David
 */
public class RefundReasonDialog extends Stage {

    private static Stage dialog;

    private static RefundReason reason;

    private final DataConnect dc;

    private TableView reasonsTable;
    private ObservableList<RefundReason> obReasons;

    public RefundReasonDialog(Window parent) {
        this.dc = DataConnect.dataconnect;
        init();
        setTitle("Select Reason");
        initOwner(parent);
        initModality(Modality.APPLICATION_MODAL);
    }

    public static RefundReason showDialog(Window parent) {
        dialog = new RefundReasonDialog(parent);
        reason = null;
        dialog.showAndWait();
        return reason;
    }

    private void init() {
        GridPane pane = new GridPane();
        Button selectButton = new Button("Select");
        Button close = new Button("Cancel");

        obReasons = FXCollections.observableArrayList();

        try {
            List<RefundReason> reasons = dc.getUsedRefundReasons();
            obReasons.setAll(reasons);
        } catch (IOException | SQLException ex) {
            MessageDialog.showMessage(this, "Refund Reasons", ex.getMessage());
        }

        reasonsTable = new TableView();
        reasonsTable.setEditable(false);
        reasonsTable.setItems(obReasons);
        TableColumn colID = new TableColumn("ID");
        TableColumn colName = new TableColumn("Name");
        colID.setCellValueFactory(new PropertyValueFactory<>("id"));
        colName.setCellValueFactory(new PropertyValueFactory<>("reason"));
        colID.setMinWidth(30);
        colID.setMaxWidth(30);
        colName.setMinWidth(270);
        colName.setMaxWidth(270);
        reasonsTable.getColumns().addAll(colID, colName);
        reasonsTable.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<RefundReason>() {
            @Override
            public void changed(ObservableValue<? extends RefundReason> observable, RefundReason oldValue, RefundReason newValue) {
                reason = newValue;
                selectButton.setDisable(false);
            }
        });

        reasonsTable.setRowFactory(tv -> {
            TableRow<Product> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    hide();
                }
            });
            return row;
        });

        pane.add(reasonsTable, 0, 0, 3, 3);

        selectButton.setMinSize(150, 150);
        selectButton.setMaxSize(150, 150);
        selectButton.setDisable(true);
        selectButton.setOnAction((ActionEvent event) -> {
            hide();
        });
        pane.add(selectButton, 0, 3);

        close.setMinSize(150, 150);
        close.setMaxSize(150, 150);
        close.setOnAction((ActionEvent event) -> {
            reason = null;
            hide();
        });
        pane.add(close, 2, 3);
        Scene scene = new Scene(pane, 300, 800);

        String stylesheet = MainStage.class.getResource("style.css").toExternalForm();
        scene.getStylesheets().add(stylesheet);
        setScene(scene);
    }

}
