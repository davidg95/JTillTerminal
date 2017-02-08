/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.davidg95.jtill.javafxjtill;

import io.github.davidg95.JTill.jtill.DataConnectInterface;
import io.github.davidg95.JTill.jtill.Discount;
import io.github.davidg95.JTill.jtill.DiscountNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
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
public class DiscountSelectDialog extends Stage {

    private static Stage dialog;
    private static Discount discount;

    private final DataConnectInterface dc;

    private ListView<Discount> discountList;
    private ObservableList<Discount> obDiscounts;

    public DiscountSelectDialog(Window parent, DataConnectInterface dc) {
        this.dc = dc;
        init();
        setTitle("Select Discount");
        initOwner(parent);
        initModality(Modality.APPLICATION_MODAL);

    }

    public static Discount showDialog(Window parent, DataConnectInterface dc) {
        dialog = new DiscountSelectDialog(parent, dc);
        discount = null;
        dialog.showAndWait();
        return discount;
    }

    private void init() {
        try {
            GridPane pane = new GridPane();
            discountList = new ListView<>();
            obDiscounts = FXCollections.observableArrayList();
            List<Discount> d = dc.getAllDiscounts();
            updateList(d);
            discountList.setItems(obDiscounts);
            discountList.setMinSize(300, 300);
            discountList.setMaxSize(300, 300);
            discountList.getSelectionModel().selectedItemProperty().addListener((ObservableValue<? extends Discount> observable, Discount oldValue, Discount newValue) -> {
                discount = newValue;
            });
            pane.add(discountList, 0, 0, 3, 3);

            Button searchID = new Button("Search ID");
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

            Button searchTerms = new Button("Search Terms");
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
        } catch (IOException | SQLException ex) {
            MessageDialog.showMessage(this, "Error", ex.getMessage());
            hide();
        }
    }

    private void updateList(List<Discount> d) {
        obDiscounts.setAll(d);
    }
}
