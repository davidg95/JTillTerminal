/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.davidg95.jtill.javafxjtill;

import io.github.davidg95.JTill.jtill.*;

/**
 *
 * @author David
 */
public class ProductEvent {
    private final Product product;

    public ProductEvent(Product p) {
        this.product = p;
    }

    public Product getProduct() {
        return product;
    }

    @Override
    public String toString() {
        return "ProductEvent{" + "product=" + product + '}';
    }
}
