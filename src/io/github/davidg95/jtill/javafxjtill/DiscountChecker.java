/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.davidg95.jtill.javafxjtill;

import io.github.davidg95.JTill.jtill.*;
import javafx.application.Platform;

/**
 * Class which checks if the conditions for a given discounts have been met.
 *
 * @author David
 */
public class DiscountChecker implements ProductListener {

    private final MainStage ms;
    private final Discount d;

    private int hits;

    /**
     * Creates the new discount checker.
     *
     * @param ms the Main Stage.
     * @param d the discount this check is checking for.
     */
    public DiscountChecker(MainStage ms, Discount d) {
        this.ms = ms;
        this.d = d;
        ms.addListener(this);
    }

    @Override
    public void onProductAdd(ProductEvent pe) {
        for (Trigger t : d.getTriggers()) {
            if (t.getProduct() == pe.getProduct().getId()) {
                hits++;
                break;
            }
        }
        if (d.getCondition() == 1) {
            if ((d.getConditionValue() * d.getTriggers().size()) <= hits) {
                reset();
                Platform.runLater(() -> {
                    ms.addItemToSale(d);
                });
            }
        } else {
            if (d.getConditionValue() <= hits) {
                reset();
                Platform.runLater(() -> {
                    ms.addItemToSale(d);
                });
            }
        }
    }

    /**
     * Resets this discount checker for a new sale or if the condition was met.
     */
    public void reset() {
        hits = 0;
    }
}
