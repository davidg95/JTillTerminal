/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.davidg95.jtill.javafxjtill;

import io.github.davidg95.JTill.jtill.Product;
import io.github.davidg95.JTill.jtill.ProductNotFoundException;
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
import io.github.davidg95.JTill.jtill.Utilities;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;

/**
 *
 * @author David
 */
public class ProductSelectDialog extends Stage {

    private static Stage dialog;
    private static Product product;

    private final DataConnect dc;

    private TableView productTable;
    private ObservableList<Product> obProducts;

    public ProductSelectDialog(Window parent, DataConnect dc) {
        this.dc = dc;
        init();
        setTitle("Select Product");
        initOwner(parent);
        initModality(Modality.APPLICATION_MODAL);
    }

    public static Product showDialog(Window parent, DataConnect dc) {
        dialog = new ProductSelectDialog(parent, dc);
        product = null;
        dialog.showAndWait();
        return product;
    }

    private void init() {
        GridPane pane = new GridPane();
        Button searchBarcode = new Button("Barcode");
        Button searchTerms = new Button("Search Terms");
        Button close = new Button("Close");

        obProducts = FXCollections.observableArrayList();

        productTable = new TableView();
        productTable.setEditable(false);
        productTable.setItems(obProducts);
//        productTable.setMinSize(1200, 650);
//        productTable.setMinSize(300, 300);
//        productTable.setMaxSize(300, 300);
        TableColumn colID = new TableColumn("ID");
        TableColumn colName = new TableColumn("Name");
        TableColumn colPrice = new TableColumn("Price");
        colID.setCellValueFactory(new PropertyValueFactory<>("id"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
//        colID.setMinWidth(30);
//        colID.setMaxWidth(30);
//        colPrice.setMinWidth(70);
//        colPrice.setMaxWidth(70);
        productTable.getColumns().addAll(colID, colName, colPrice);
        productTable.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Product>() {
            @Override
            public void changed(ObservableValue<? extends Product> observable, Product oldValue, Product newValue) {
                product = newValue;
                close.setText("Select");
            }
        });

        productTable.setRowFactory(tv -> {
            TableRow<Product> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    hide();
                }
            });
            return row;
        });

        pane.add(productTable, 0, 0, 8, 3);

        searchBarcode.setMinSize(150, 150);
        searchBarcode.setMaxSize(150, 150);
        searchBarcode.setOnAction((ActionEvent event) -> {
            String barcode = EntryDialog.show(this, "Product Lookup", "Enter or scan barcode");
            if (barcode == null) {
                return;
            }
            if (barcode.length() == 0) {
                MessageDialog.showMessage(this, "Product Lookup", "A barcode must be entered");
                return;
            }
            if (!Utilities.isNumber(barcode)) {
                MessageDialog.showMessage(this, "Product Lookup", "Only numbers must be entered");
                return;
            }
            try {
                Product p = dc.getProductByBarcode(barcode);
                List<Product> res = new ArrayList<>();
                res.add(p);
                updateList(res);
            } catch (IOException | ProductNotFoundException | SQLException ex) {
                MessageDialog.showMessage(this, "Product Search", ex.getMessage());
            }
        });
        pane.add(searchBarcode, 0, 3);

        searchTerms.setMinSize(150, 150);
        searchTerms.setMaxSize(150, 150);
        searchTerms.setOnAction((ActionEvent event) -> {
            String terms = EntryDialog.show(this, "Product Search", "Enter search terms");
            try {
                List<Product> results = dc.productLookup(terms);
                updateList(results);
            } catch (IOException | SQLException ex) {
                MessageDialog.showMessage(this, "Error", ex.getMessage());
            }
        });
        pane.add(searchTerms, 1, 3);

        close.setMinSize(150, 150);
        close.setMaxSize(150, 150);
        close.setOnAction((ActionEvent event) -> {
            hide();
        });
        pane.add(close, 7, 3);

//        for (int i = 1; i <= 8; i++) {
//            ColumnConstraints col = new ColumnConstraints();
//            col.setPercentWidth(12.5);
//            col.setFillWidth(true);
//            col.setHgrow(Priority.ALWAYS);
//            pane.getColumnConstraints().add(col);
//        }

//        for (int i = 1; i <= 4; i++) {
//            RowConstraints row = new RowConstraints();
//            row.setPercentHeight(25);
//            row.setFillHeight(true);
//            row.setVgrow(Priority.ALWAYS);
//            pane.getRowConstraints().add(row);
//        }

        Scene scene = new Scene(pane, 1200, 800);

        String stylesheet = MainStage.class.getResource("style.css").toExternalForm();
        scene.getStylesheets().add(stylesheet);
        setScene(scene);
    }

    private void updateList(List<Product> p) {
        obProducts.setAll(p);
    }
}
