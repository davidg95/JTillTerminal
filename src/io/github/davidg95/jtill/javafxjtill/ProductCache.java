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
 * Class which models the till cache. This class uses an LinkedList to store the
 * Products and an LinkedList to store the Plus. It uses StampedLocks for
 * concurrency control for the the ArrayLists.
 *
 * @author David
 */
public class ProductCache {

    private static final ProductCache CACHE; //Static reference to the ProductCache.
    private static final Logger LOG = Logger.getGlobal(); //Logger.

    private List<Product> products; //The Products in the cache.

    private final StampedLock lock; //Lock for products.

    /**
     * Default constructor.
     */
    public ProductCache() {
        products = new LinkedList<>();
        lock = new StampedLock();
    }

    /**
     * Static initialiser.
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
        try {
            products.add(p);
        } finally {
            lock.unlockWrite(stamp);
        }
    }

    /**
     * Searches for a product in the cache. Will return a
     * ProductNotFoundException if none was found.
     *
     * @param barcode the ID to search for.
     * @return the Product that was found.
     * @throws ProductNotFoundException if no product was found.
     */
    public Product getProduct(String barcode) throws ProductNotFoundException {
        LOG.log(Level.INFO, "Check cache for {0}", barcode);
        long stamp = lock.readLock();
        try {
            for (Product p : products) {
                if (p.getBarcode().equals(barcode)) {
                    LOG.log(Level.INFO, "Product found in cache");
                    return p;
                }
            }
        } finally {
            lock.unlockRead(stamp);
        }
        LOG.log(Level.INFO, "Product not found in cache");
        throw new ProductNotFoundException("Product " + barcode + " not found");
    }

    /**
     * Searches the cache for a product matching the barcode. Will throw a
     * ProductNotfoundException if none was found.
     *
     * @param barcode the barcode to search.
     * @return the Product matching the barcode.
     * @throws ProductNotFoundException if no product was found.
     * @throws io.github.davidg95.JTill.jtill.JTillException if the barcode was
     * not found.
     */
    public Product getProductByBarcode(String barcode) throws ProductNotFoundException, JTillException {
        long stamp = lock.readLock();
        try {
            LOG.log(Level.INFO, "Checking cache for {0}", barcode);
            for (Product p : products) {
                if (p.getBarcode().equals(barcode)) {
                    LOG.log(Level.INFO, "Product found in cache");
                    return p;
                }
            }
        } finally {
            lock.unlockRead(stamp);
        }
        LOG.log(Level.INFO, "Product not found in cache");
        throw new ProductNotFoundException("");
    }

    /**
     * Empties the cache of all products.
     */
    public void clearCache() {
        LOG.log(Level.INFO, "Clearing cache");
        long stamp = lock.writeLock();
        try {
            products.clear();
        } finally {
            lock.unlockWrite(stamp);
        }
        LOG.log(Level.INFO, "Cache clear");
    }

    /**
     * Returns a List of all products in the cache.
     *
     * @return a List of type product.
     */
    public List<Product> getAllProducts() {
        long stamp = lock.readLock();
        try {
            return products;
        } finally {
            lock.unlockRead(stamp);
        }
    }

    /**
     * Sets the List of products in the cache.
     *
     * @param products the List to set.
     */
    public void setProducts(List<Product> products) {
        long stamp = lock.writeLock();
        try {
            this.products = products;
        } finally {
            lock.unlockWrite(stamp);
        }
    }
}
