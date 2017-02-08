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
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
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
public class ProductSelectDialog extends Stage {

    private static Stage dialog;
    private static Product product;

    private final DataConnectInterface dc;

    private TableView productTable;
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
        Button searchBarcode = new Button("Barcode");
        Button searchTerms = new Button("Search Terms");
        Button close = new Button("Close");
        
        obProducts = FXCollections.observableArrayList();

        productTable = new TableView();
        productTable.setEditable(false);
        productTable.setItems(obProducts);
        productTable.setMinSize(300, 300);
        productTable.setMaxSize(300, 300);
        TableColumn colID = new TableColumn("ID");
        TableColumn colName = new TableColumn("Name");
        TableColumn colPrice = new TableColumn("Price");
        colID.setCellValueFactory(new PropertyValueFactory<>("productCode"));
        colName.setCellValueFactory(new PropertyValueFactory<>("shortName"));
        colPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
        colID.setMinWidth(30);
        colID.setMaxWidth(30);
        colName.setMinWidth(200);
        colName.setMaxWidth(200);
        colPrice.setMinWidth(70);
        colPrice.setMaxWidth(70);
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

        pane.add(productTable, 0, 0, 3, 3);
        
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
    }

    private void updateList(List<Product> p) {
        obProducts.setAll(p);
    }
}
