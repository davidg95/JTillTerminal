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

    private List<Product> products; //The products in the cache.

    private final StampedLock lock;

    /**
     * Default constructor.
     */
    public ProductCache() {
        products = new ArrayList<>();
        lock = new StampedLock();
    }

    /**
     * Static initaliser.
     */
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

    /**
     * Adds a product to the cache.
     *
     * @param p the product to add.
     */
    public void addProductToCache(Product p) {
        long stamp = lock.writeLock();
        LOG.log(Level.INFO, "Adding product to cache");
        products.add(p);
        lock.unlockWrite(stamp);
    }

    /**
     * Searches for a product in the cache. will return a
     * ProductNotFonudException if none was found.
     *
     * @param id the ID to search for.
     * @return the Product that was found.
     * @throws ProductNotFoundException if no product was found.
     */
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

    /**
     * Searches the cache for a product matching the barcode. Will throw a
     * ProductNotfoundException if none was fonud.
     *
     * @param barcode the barcode to search.
     * @return the Product matching the barcode.
     * @throws ProductNotFoundException if no product was found.
     */
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

    /**
     * Empties the cache of all products.
     */
    public void clearCache() {
        long stamp = lock.writeLock();
        LOG.log(Level.INFO, "Clearing product cache");
        products.clear();
        LOG.log(Level.INFO, "Cache clear");
        lock.unlockWrite(stamp);
    }

    /**
     * Returns a List of all products in the cache.
     *
     * @return a List of type product.
     */
    public List<Product> getAllProducts() {
        return products;
    }

    /**
     * Sets the List of products in the cache.
     *
     * @param products the List to set.
     */
    public void setProducts(List<Product> products) {
        long stamp = lock.writeLock();
        this.products = products;
        lock.unlockWrite(stamp);
    }
}
