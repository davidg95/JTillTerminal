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
        for (DiscountBucket b : d.getBuckets()) {
            for (Trigger t : b.getTriggers()) {
                if (t.getProduct() == pe.getProduct().getId()) { //Check if the product matches
                    t.addHit(); //Increase the trigger hit count
                    if(t.getCurrentQuantity() >= t.getQuantityRequired()){ //Check if the trigger has been triggered
                        b.addHit(); //If so, increase the bucket hit count
                        t.resetQuantity(); //Then reset the trigger hit counter
                        if(b.getCurrentTriggers() >= b.getRequiredTriggers()){ //Check if the bucket has been triggered
                            d.addHit(); //Add a hit to the discount
                            b.reset(); //Then, reset the bucket hit count
                            if(d.getCurrentHits() >= d.getCondition()){ //check if the discount has been triggered
                                d.reset(); //If so, reset the discount hit count
                                Platform.runLater(() ->{
                                    ms.addItemToSale(d); //Add the discount to the sale.
                                });
                            }
                        }
                    }
                    break;
                }
            }
        }
    }
}
