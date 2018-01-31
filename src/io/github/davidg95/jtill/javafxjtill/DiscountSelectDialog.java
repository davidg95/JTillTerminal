/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.davidg95.jtill.javafxjtill;

import io.github.davidg95.JTill.jtill.Discount;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
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
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import io.github.davidg95.JTill.jtill.DataConnect;

/**
 *
 * @author David
 */
public class DiscountSelectDialog extends Stage {

    private static Stage dialog;
    private static Discount discount;

    private final DataConnect dc;

    private TableView discountsTable;
    private ObservableList<Discount> obDiscounts;

    public DiscountSelectDialog(Window parent, DataConnect dc) {
        this.dc = dc;
        init();
        setTitle("Select Discount");
        initOwner(parent);
        initModality(Modality.APPLICATION_MODAL);

    }

    public static Discount showDialog(Window parent, DataConnect dc) {
        dialog = new DiscountSelectDialog(parent, dc);
        discount = null;
        dialog.showAndWait();
        return discount;
    }

    private void init() {
        try {
            GridPane pane = new GridPane();
            Button searchID = new Button("Search ID");
            Button searchTerms = new Button("Search Terms");
            Button close = new Button("Close");

            obDiscounts = FXCollections.observableArrayList();

            discountsTable = new TableView();
            discountsTable.setEditable(false);
            discountsTable.setItems(obDiscounts);
            discountsTable.setMinSize(300, 300);
            discountsTable.setMaxSize(300, 300);
            TableColumn colID = new TableColumn("ID");
            TableColumn colName = new TableColumn("Name");
            TableColumn colValue = new TableColumn("Value");
            colID.setCellValueFactory(new PropertyValueFactory<>("id"));
            colName.setCellValueFactory(new PropertyValueFactory<>("name"));
            colValue.setCellValueFactory(new PropertyValueFactory<>("percentage"));
            colID.setMinWidth(30);
            colID.setMaxWidth(30);
            colName.setMinWidth(200);
            colName.setMaxWidth(200);
            colValue.setMinWidth(70);
            colValue.setMaxWidth(70);
            discountsTable.getColumns().addAll(colID, colName, colValue);
            discountsTable.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Discount>() {
                @Override
                public void changed(ObservableValue<? extends Discount> observable, Discount oldValue, Discount newValue) {
                    discount = newValue;
                    close.setText("Select");
                }
            });

            discountsTable.setRowFactory(tv -> {
                TableRow<Discount> row = new TableRow<>();
                row.setOnMouseClicked(event -> {
                    if (event.getClickCount() == 2 && (!row.isEmpty())) {
                        hide();
                    }
                });
                return row;
            });

            List<Discount> d = dc.getAllDiscounts();
            updateList(d);

            pane.add(discountsTable, 0, 0, 3, 3);

            searchID.setMinSize(100, 100);
            searchID.setMaxSize(100, 100);
            HBox hSearchID = new HBox(0);
            hSearchID.getChildren().add(searchID);
            searchID.setOnAction((ActionEvent event) -> {
                int id = NumberEntry.showNumberEntryDialog(this, "Enter ID");
                for (Discount dis : obDiscounts) {
                    if (dis.getId() == id) {
                        discount = dis;
                        hide();
                        return;
                    }
                }
                MessageDialog.showMessage(this, "Discounts", "No match");
            });
            pane.add(hSearchID, 0, 3);

            searchTerms.setMinSize(100, 100);
            searchTerms.setMaxSize(100, 100);
            HBox hSearchTerms = new HBox(0);
            hSearchTerms.getChildren().add(searchTerms);
            searchTerms.setOnAction((ActionEvent event) -> {
                String terms = EntryDialog.show(this, "Discount Search", "Enter terms");
                List<Discount> newList = new ArrayList<>();
                for (Discount dis : obDiscounts) {
                    if (dis.getName().toLowerCase().contains(terms.toLowerCase())) {
                        newList.add(dis);
                    }
                }
                if (newList.isEmpty()) {
                    MessageDialog.showMessage(this, "Discounts", "No matches");
                    return;
                }
                updateList(newList);
            });
            pane.add(hSearchTerms, 1, 3);

            close.setMinSize(100, 100);
            close.setMaxSize(100, 100);
            HBox hClose = new HBox(0);
            hClose.getChildren().add(close);
            close.setOnAction((ActionEvent event) -> {
                hide();
            });
            pane.add(hClose, 2, 3);

            Scene scene = new Scene(pane, 300, 400);
            String stylesheet = MainStage.class.getResource("style.css").toExternalForm();
            scene.getStylesheets().add(stylesheet);
            setScene(scene);
        } catch (IOException | SQLException ex) {
            MessageDialog.showMessage(this, "Error", ex.getMessage());
            hide();
        }
    }

    private void updateList(List<Discount> d) {
        obDiscounts.setAll(d);
    }
}
