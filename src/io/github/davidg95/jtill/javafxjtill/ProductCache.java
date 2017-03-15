/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.davidg95.jtill.javafxjtill;

import io.github.davidg95.JTill.jtill.*;
import java.util.*;
import java.util.concurrent.locks.StampedLock;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class which models the till cache.
 *
 * @author David
 */
public class ProductCache {

    private static final ProductCache CACHE;
    private static final Logger LOG = Logger.getGlobal();

    private List<Product> products;

    private final StampedLock lock;

    public ProductCache() {
        products = new ArrayList<>();
        lock = new StampedLock();
    }

    static {
        CACHE = new ProductCache();
    }

    /**
     * Returns an instance of the cache.
     *
     * @return the ProductCache.
     */
    public static ProductCache getInstance() {
        return CACHE;
    }

    public void addProductToCache(Product p) {
        long stamp = lock.writeLock();
        LOG.log(Level.INFO, "Adding product to cache");
        products.add(p);
        lock.unlockWrite(stamp);
    }

    public Product getProduct(int id) throws ProductNotFoundException {
        long stamp = lock.readLock();
        LOG.log(Level.INFO, "Check cache for {0}", id);
        for (Product p : products) {
            if (p.getId() == id) {
                lock.unlockRead(stamp);
                LOG.log(Level.INFO, "Product found in cache");
                return p;
            }
        }
        lock.unlockRead(stamp);
        LOG.log(Level.INFO, "Product not found in cache");
        throw new ProductNotFoundException("");
    }

    public Product getProductByBarcode(String barcode) throws ProductNotFoundException {
        long stamp = lock.readLock();
        LOG.log(Level.INFO, "Checking cache for {0}", barcode);
        for (Product p : products) {
            if (p.getPlu().getCode().equals(barcode)) {
                lock.unlockRead(stamp);
                LOG.log(Level.INFO, "Product found in cache");
                return p;
            }
        }
        lock.unlockRead(stamp);
        LOG.log(Level.INFO, "Product not found in cache");
        throw new ProductNotFoundException("");
    }

    public void clearCache() {
        long stamp = lock.writeLock();
        LOG.log(Level.INFO, "Clearing product cache");
        products.clear();
        LOG.log(Level.INFO, "Cache clear");
        lock.unlockWrite(stamp);
    }

    public List<Product> getAllProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        long stamp = lock.writeLock();
        this.products = products;
        lock.unlockWrite(stamp);
    }
}
