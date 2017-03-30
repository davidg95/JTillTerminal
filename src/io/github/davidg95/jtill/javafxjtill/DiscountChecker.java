/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.davidg95.jtill.javafxjtill;

import io.github.davidg95.JTill.jtill.*;
import javafx.application.Platform;

/**
 *
 * @author David
 */
public class DiscountChecker implements ProductListener {

    private final MainStage ms;
    private final Discount d;

    public DiscountChecker(MainStage ms, Discount d) {
        this.ms = ms;
        this.d = d;
        ms.addListener(this);
    }

    @Override
    public void onProductAdd(ProductEvent pe) {
        if (d.getTrigger() == pe.getProduct().getId()) {
            Platform.runLater(() -> {
                ms.addItemToSale(d);
            });
        }
    }
}
