package com.rpgcornerteam.rpgcorner.domain;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class InventoryTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static Inventory getInventorySample1() {
        return new Inventory().id(1L).supplie(1);
    }

    public static Inventory getInventorySample2() {
        return new Inventory().id(2L).supplie(2);
    }

    public static Inventory getInventoryRandomSampleGenerator() {
        return new Inventory().id(longCount.incrementAndGet()).supplie(intCount.incrementAndGet());
    }
}
