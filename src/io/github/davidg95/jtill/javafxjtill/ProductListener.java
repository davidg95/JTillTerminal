/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.davidg95.jtill.javafxjtill;

/**
 *
 * @author David
 */
public interface ProductListener {

    /**
     * Method which gets called when an item has been added to the sale.
     *
     * @param pe the ProductEvent.
     */
    public void onProductAdd(ProductEvent pe);
}
