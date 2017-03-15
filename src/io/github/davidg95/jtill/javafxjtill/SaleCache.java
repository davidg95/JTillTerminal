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
 * Class which models the sale cache.
 *
 * @author David
 */
public class SaleCache {

    private static final SaleCache CACHE;
    private static final Logger LOG = Logger.getGlobal();

    private final List<Sale> sales;
    private final StampedLock lock;

    public SaleCache() {
        sales = new ArrayList<>();
        lock = new StampedLock();
    }

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

    public void addSale(Sale s) {
        long stamp = lock.writeLock();
        LOG.log(Level.INFO, "Adding sale");
        sales.add(s);
        lock.unlockWrite(stamp);
    }

    public List<Sale> getAllSales() {
        LOG.log(Level.INFO, "Getting all sales");
        return sales;
    }

    public void clearAll() {
        long stamp = lock.writeLock();
        LOG.log(Level.INFO, "Clearing all sales");
        sales.clear();
        lock.unlockWrite(stamp);
    }
}
