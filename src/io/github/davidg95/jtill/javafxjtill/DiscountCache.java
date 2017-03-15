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
 *
 * @author David
 */
public class DiscountCache {

    private static final DiscountCache CACHE;
    private static final Logger LOG = Logger.getGlobal();

    private List<Discount> discounts;
    private final List<DiscountChecker> checkers;
    private final StampedLock lock;

    public DiscountCache() {
        discounts = new ArrayList<>();
        checkers = new ArrayList<>();
        lock = new StampedLock();
    }

    static {
        CACHE = new DiscountCache();
    }

    public static DiscountCache getInstance() {
        return CACHE;
    }

    public void addDiscount(Discount d) {
        long stamp = lock.writeLock();
        discounts.add(d);
        LOG.log(Level.INFO, "Added discount " + d.getId());
        lock.unlockWrite(stamp);
    }

    public Discount getDiscount(int id) throws DiscountNotFoundException {
        long stamp = lock.readLock();
        for (Discount d : discounts) {
            if (d.getId() == id) {
                LOG.log(Level.INFO, "Found discount " + id);
                lock.unlockRead(stamp);
                return d;
            }
        }
        LOG.log(Level.WARNING, "Could not find discount " + id + " in cache");
        throw new DiscountNotFoundException("");
    }

    public void setDiscounts(List<Discount> discounts, MainStage ms) {
        long stamp = lock.writeLock();
        this.discounts = discounts;
        for (Discount d : discounts) {
            DiscountChecker dc = new DiscountChecker(ms, d);
            checkers.add(dc);
        }
        LOG.log(Level.INFO, "Set discounts in cache");
        lock.unlockWrite(stamp);
    }

    public List<Discount> getAllDiscounts() {
        LOG.log(Level.INFO, "Getting all discounts from cache");
        return discounts;
    }

    public void clearAll() {
        long stamp = lock.writeLock();
        discounts.clear();
        LOG.log(Level.INFO, "Discounts cleared from cache");
        lock.unlockWrite(stamp);
    }
}
