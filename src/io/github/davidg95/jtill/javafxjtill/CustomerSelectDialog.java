/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.davidg95.jtill.javafxjtill;

import io.github.davidg95.JTill.jtill.Customer;
import io.github.davidg95.JTill.jtill.CustomerNotFoundException;
import io.github.davidg95.JTill.jtill.DataConnectInterface;
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
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

/**
 *
 * @author David
 */
public class CustomerSelectDialog extends Stage {

    private static Stage dialog;
    private static Customer customer;

    private final DataConnectInterface dc;

    private TableView customerTable;
    private ObservableList<Customer> obCustomers;

    public CustomerSelectDialog(Window parent, DataConnectInterface dc, String title) {
        this.dc = dc;
        init();
        setTitle(title);
        initOwner(parent);
        initModality(Modality.APPLICATION_MODAL);
    }

    public static Customer showDialog(Window parent, DataConnectInterface dc, String title) {
        dialog = new CustomerSelectDialog(parent, dc, title);
        customer = null;
        dialog.showAndWait();
        return customer;
    }

    private void init() {
        GridPane pane = new GridPane();
        
        obCustomers = FXCollections.observableArrayList();
        
        customerTable = new TableView();
        customerTable.setItems(obCustomers);
        customerTable.setEditable(false);
        customerTable.setMinSize(300, 300);
        customerTable.setMaxSize(300, 300);
        TableColumn cusID = new TableColumn("ID");
        TableColumn cusName = new TableColumn("Name");
        cusID.setCellValueFactory(new PropertyValueFactory<>("id"));
        cusName.setCellValueFactory(new PropertyValueFactory<>("name"));
        cusID.setMinWidth(50);
        cusID.setMaxWidth(50);
        cusName.setMinWidth(250);
        cusName.setMaxWidth(250);
        customerTable.getColumns().addAll(cusID, cusName);
        customerTable.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Customer>() {
            @Override
            public void changed(ObservableValue<? extends Customer> observable, Customer oldValue, Customer newValue) {
                customer = newValue;
            }
        });

        pane.add(customerTable, 0, 0, 3, 3);

        Button searchID = new Button("Search ID");
        searchID.setMinSize(100, 100);
        searchID.setMaxSize(100, 100);
        HBox hSearch = new HBox(0);
        hSearch.getChildren().add(searchID);
        searchID.setOnAction((ActionEvent event) -> {
            int id = NumberEntry.showNumberEntryDialog(this, "Enter Customer ID");
            try {
                Customer c = dc.getCustomer(id);
                List<Customer> res = new ArrayList<>();
                res.add(c);
                updateList(res);
            } catch (IOException | CustomerNotFoundException | SQLException ex) {
                MessageDialog.showMessage(this, "Customer Search", ex.getMessage());
            }
        });
        pane.add(searchID, 0, 3);

        Button searchTerms = new Button("Search Terms");
        searchTerms.setMinSize(100, 100);
        searchTerms.setMaxSize(100, 100);
        HBox hSearchTerms = new HBox(0);
        hSearchTerms.getChildren().add(searchTerms);
        searchTerms.setOnAction((ActionEvent event) -> {
            String terms = EntryDialog.show(this, "Customer Search", "Enter search terms");
            try {
                List<Customer> customers = dc.customerLookup(terms);
                updateList(customers);
            } catch (IOException | SQLException ex) {
                MessageDialog.showMessage(this, "Customer Search", ex.getMessage());
            }
        });
        pane.add(searchTerms, 1, 3);

        Button close = new Button("Close");
        close.setMinSize(100, 100);
        close.setMaxSize(100, 100);
        HBox hClose = new HBox(0);
        hClose.getChildren().add(close);
        close.setOnAction((ActionEvent event) -> {
            hide();
        });
        pane.add(close, 2, 3);

        Scene scene = new Scene(pane, 300, 400);
        String stylesheet = MainStage.class.getResource("style.css").toExternalForm();
        scene.getStylesheets().add(stylesheet);
        setScene(scene);
    }

    private void updateList(List<Customer> c) {
        obCustomers.setAll(c);
    }
}
