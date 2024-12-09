package com.rpgcornerteam.rpgcorner.domain;

import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

public class SupplierTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Supplier getSupplierSample1() {
        return new Supplier().id(1L);
    }

    public static Supplier getSupplierSample2() {
        return new Supplier().id(2L);
    }

    public static Supplier getSupplierRandomSampleGenerator() {
        return new Supplier().id(longCount.incrementAndGet());
    }
}
