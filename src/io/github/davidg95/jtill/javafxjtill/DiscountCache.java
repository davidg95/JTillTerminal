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
 * Class which models the DiscountCache. It stores the discounts in an
 * LinkedList and uses a StampedLock for concurrency control.
 *
 * @author David
 */
public class DiscountCache {

    private static final DiscountCache CACHE; //Static reference to the DiscountCache.
    private static final Logger LOG = Logger.getGlobal(); //Logger

    private List<Discount> discounts; //Discount list.
    private final StampedLock lock; //Lock for discount list.

    /**
     * Default constructor.
     */
    public DiscountCache() {
        discounts = new LinkedList<>();
        lock = new StampedLock();
    }

    /**
     * Static initialiser.
     */
    static {
        CACHE = new DiscountCache();
    }

    /**
     * Gets an instance of the DiscountCache.
     *
     * @return instance of the DiscountCache.
     */
    public static DiscountCache getInstance() {
        return CACHE;
    }

    /**
     * Add a discount to the cache.
     *
     * @param d the Discount to add.
     */
    public void addDiscount(Discount d) {
        LOG.log(Level.INFO, "Adding discount " + d.getId());
        long stamp = lock.writeLock();
        try {
            discounts.add(d);
        } finally {
            lock.unlockWrite(stamp);
        }
    }

    /**
     * Searches the cache for a discount.
     *
     * @param id the id to search for.
     * @return the discount it found.
     * @throws DiscountNotFoundException If not discount was found.
     */
    public Discount getDiscount(int id) throws DiscountNotFoundException {
        long stamp = lock.readLock();
        try {
            for (Discount d : discounts) {
                if (d.getId() == id) {
                    LOG.log(Level.INFO, "Found discount " + id);
                    return d;
                }
            }
        } finally {
            lock.unlockRead(stamp);
        }
        LOG.log(Level.WARNING, "Could not find discount " + id + " in cache");
        throw new DiscountNotFoundException("");
    }

    /**
     * Set the list of discounts to a new one.
     *
     * @param discounts the discounts to set.
     */
    public void setDiscounts(List<Discount> discounts) {
        LOG.log(Level.INFO, "Set discounts in cache");
        long stamp = lock.writeLock();
        try {
            this.discounts = discounts;
        } finally {
            lock.unlockWrite(stamp);
        }
    }

    /**
     * Get a list of all the discounts in the cache.
     *
     * @return a List of type Discount.
     */
    public List<Discount> getAllDiscounts() {
        LOG.log(Level.INFO, "Getting all discounts from cache");
        long stamp = lock.readLock();
        try {
            return discounts;
        } finally {
            lock.unlockRead(stamp);
        }
    }

    /**
     * Clear the cache.
     */
    public void clearAll() {
        long stamp = lock.writeLock();
        try {
            discounts.clear();
            LOG.log(Level.INFO, "Discounts cleared from cache");
        } finally {
            lock.unlockWrite(stamp);
        }
    }
}
