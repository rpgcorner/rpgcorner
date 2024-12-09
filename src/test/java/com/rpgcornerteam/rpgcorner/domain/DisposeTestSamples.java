package com.rpgcornerteam.rpgcorner.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class DisposeTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Dispose getDisposeSample1() {
        return new Dispose().id(1L).note("note1");
    }

    public static Dispose getDisposeSample2() {
        return new Dispose().id(2L).note("note2");
    }

    public static Dispose getDisposeRandomSampleGenerator() {
        return new Dispose().id(longCount.incrementAndGet()).note(UUID.randomUUID().toString());
    }
}
