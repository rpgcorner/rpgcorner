package com.rpgcornerteam.rpgcorner.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class ContactTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Contact getContactSample1() {
        return new Contact()
            .id(1L)
            .companyName("companyName1")
            .taxNumber("taxNumber1")
            .contactName("contactName1")
            .address("address1")
            .email("email1")
            .fax("fax1")
            .mobile("mobile1")
            .phone("phone1")
            .note("note1");
    }

    public static Contact getContactSample2() {
        return new Contact()
            .id(2L)
            .companyName("companyName2")
            .taxNumber("taxNumber2")
            .contactName("contactName2")
            .address("address2")
            .email("email2")
            .fax("fax2")
            .mobile("mobile2")
            .phone("phone2")
            .note("note2");
    }

    public static Contact getContactRandomSampleGenerator() {
        return new Contact()
            .id(longCount.incrementAndGet())
            .companyName(UUID.randomUUID().toString())
            .taxNumber(UUID.randomUUID().toString())
            .contactName(UUID.randomUUID().toString())
            .address(UUID.randomUUID().toString())
            .email(UUID.randomUUID().toString())
            .fax(UUID.randomUUID().toString())
            .mobile(UUID.randomUUID().toString())
            .phone(UUID.randomUUID().toString())
            .note(UUID.randomUUID().toString());
    }
}
