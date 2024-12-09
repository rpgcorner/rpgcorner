package com.rpgcornerteam.rpgcorner.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class WareTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Ware getWareSample1() {
        return new Ware().id(1L).name("name1").description("description1").productCode("productCode1");
    }

    public static Ware getWareSample2() {
        return new Ware().id(2L).name("name2").description("description2").productCode("productCode2");
    }

    public static Ware getWareRandomSampleGenerator() {
        return new Ware()
            .id(longCount.incrementAndGet())
            .name(UUID.randomUUID().toString())
            .description(UUID.randomUUID().toString())
            .productCode(UUID.randomUUID().toString());
    }
}
