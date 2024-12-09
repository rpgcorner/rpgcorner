package com.rpgcornerteam.rpgcorner.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class CategoryTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Category getCategorySample1() {
        return new Category().id(1L).description("description1");
    }

    public static Category getCategorySample2() {
        return new Category().id(2L).description("description2");
    }

    public static Category getCategoryRandomSampleGenerator() {
        return new Category().id(longCount.incrementAndGet()).description(UUID.randomUUID().toString());
    }
}
