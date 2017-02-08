/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.davidg95.jtill.javafxjtill;

import io.github.davidg95.JTill.jtill.DataConnectInterface;
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
import javafx.scene.control.ListView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

/**
 *
 * @author David
 */
public class ProductSelectDialog extends Stage {

    private static Stage dialog;
    private static Product product;

    private final DataConnectInterface dc;

    private ListView<Product> productList;
    private ObservableList<Product> obProducts;

    public ProductSelectDialog(Window parent, DataConnectInterface dc) {
        this.dc = dc;
        init();
        setTitle("Select Product");
        initOwner(parent);
        initModality(Modality.APPLICATION_MODAL);
    }

    public static Product showDialog(Window parent, DataConnectInterface dc) {
        dialog = new ProductSelectDialog(parent, dc);
        product = null;
        dialog.showAndWait();
        return product;
    }

    private void init() {
        GridPane pane = new GridPane();
        productList = new ListView<>();
        obProducts = FXCollections.observableArrayList();
        productList.setItems(obProducts);
        productList.setMinSize(300, 300);
        productList.setMaxSize(300, 300);
        productList.getSelectionModel().selectedItemProperty().addListener((ObservableValue<? extends Product> observable, Product oldValue, Product newValue) -> {
            product = newValue;
        });
        pane.add(productList, 0, 0, 3, 3);

        Button searchBarcode = new Button("Barcode");
        searchBarcode.setMinSize(100, 100);
        searchBarcode.setMaxSize(100, 100);
        HBox hSearchBarcode = new HBox(0);
        hSearchBarcode.getChildren().add(searchBarcode);
        searchBarcode.setOnAction((ActionEvent event) -> {
            String barcode = EntryDialog.show(this, "Product Search", "Enter or scan barcode");
            try {
                Product p = dc.getProductByBarcode(barcode);
                List<Product> res = new ArrayList<>();
                res.add(p);
                updateList(res);
            } catch (IOException | ProductNotFoundException | SQLException ex) {
                MessageDialog.showMessage(this, "Product Search", ex.getMessage());
            }
        });
        pane.add(hSearchBarcode, 0, 3);

        Button searchTerms = new Button("Search Terms");
        searchTerms.setMinSize(100, 100);
        searchTerms.setMaxSize(100, 100);
        HBox hSearchTerms = new HBox(0);
        hSearchTerms.getChildren().add(searchTerms);
        searchTerms.setOnAction((ActionEvent event) -> {
            String terms = EntryDialog.show(this, "Product Search", "Enter search terms");
            try {
                List<Product> results = dc.productLookup(terms);
                updateList(results);
            } catch (IOException | SQLException ex) {
                MessageDialog.showMessage(this, "Error", ex.getMessage());
            }
        });
        pane.add(hSearchTerms, 1, 3);

        Button close = new Button("Close");
        close.setMinSize(100, 100);
        close.setMaxSize(100, 100);
        HBox hClose = new HBox(0);
        hClose.getChildren().add(close);
        close.setOnAction((ActionEvent event) -> {
            hide();
        });
        pane.add(hClose, 2, 3);

        Scene scene = new Scene(pane, 300, 400);
        setScene(scene);
    }

    private void updateList(List<Product> p) {
        obProducts.setAll(p);
    }
}
