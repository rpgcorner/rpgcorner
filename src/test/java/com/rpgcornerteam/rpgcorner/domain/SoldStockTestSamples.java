package com.rpgcornerteam.rpgcorner.domain;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class SoldStockTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static SoldStock getSoldStockSample1() {
        return new SoldStock().id(1L).supplie(1).unitPrice(1).returnedSupplie(1);
    }

    public static SoldStock getSoldStockSample2() {
        return new SoldStock().id(2L).supplie(2).unitPrice(2).returnedSupplie(2);
    }

    public static SoldStock getSoldStockRandomSampleGenerator() {
        return new SoldStock()
            .id(longCount.incrementAndGet())
            .supplie(intCount.incrementAndGet())
            .unitPrice(intCount.incrementAndGet())
            .returnedSupplie(intCount.incrementAndGet());
    }
}
