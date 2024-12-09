package com.rpgcornerteam.rpgcorner.domain;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class PurchasedStockTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static PurchasedStock getPurchasedStockSample1() {
        return new PurchasedStock().id(1L).supplie(1).unitPrice(1);
    }

    public static PurchasedStock getPurchasedStockSample2() {
        return new PurchasedStock().id(2L).supplie(2).unitPrice(2);
    }

    public static PurchasedStock getPurchasedStockRandomSampleGenerator() {
        return new PurchasedStock()
            .id(longCount.incrementAndGet())
            .supplie(intCount.incrementAndGet())
            .unitPrice(intCount.incrementAndGet());
    }
}
