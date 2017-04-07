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
 * Class which models the sale cache. This class stores the sales in an
 * ArrayList and uses a StampedLock for concurrency control.
 *
 * @author David
 */
public class SaleCache {

    private static final SaleCache CACHE; //Static reference to the SaleCache.
    private static final Logger LOG = Logger.getGlobal(); //Logger

    private final List<Sale> sales; //The List of the sales in the cache.
    private final StampedLock lock; //The StampedLock for concurrency control.

    /**
     * Default Constructor.
     */
    public SaleCache() {
        sales = new ArrayList<>();
        lock = new StampedLock();
    }

    /**
     * Static initialiser.
     */
    static {
        CACHE = new SaleCache();
    }

    /**
     * Gets an instance of the sale cache.
     *
     * @return SaleCache instance.
     */
    public static SaleCache getInstance() {
        return CACHE;
    }

    /**
     * Add a sale to the cache.
     *
     * @param s the sale to add.
     */
    public void addSale(Sale s) {
        LOG.log(Level.INFO, "Adding sale");
        long stamp = lock.writeLock();
        try {
            sales.add(s);
        } finally {
            lock.unlockWrite(stamp);
        }
    }

    /**
     * Get all sales from the cache.
     *
     * @return a List of all sales.
     */
    public List<Sale> getAllSales() {
        LOG.log(Level.INFO, "Getting all sales");
        long stamp = lock.readLock();
        try {
            return sales;
        } finally {
            lock.unlockRead(stamp);
        }
    }

    /**
     * Clear the cache of all sales.
     */
    public void clearAll() {
        LOG.log(Level.INFO, "Clearing all sales");
        long stamp = lock.writeLock();
        try {
            sales.clear();
        } finally {
            lock.unlockWrite(stamp);
        }
    }
}
