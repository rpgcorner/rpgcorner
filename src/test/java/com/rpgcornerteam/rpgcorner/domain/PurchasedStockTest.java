package com.rpgcornerteam.rpgcorner.domain;

import static com.rpgcornerteam.rpgcorner.domain.PurchaseTestSamples.*;
import static com.rpgcornerteam.rpgcorner.domain.PurchasedStockTestSamples.*;
import static com.rpgcornerteam.rpgcorner.domain.WareTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.rpgcornerteam.rpgcorner.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class PurchasedStockTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(PurchasedStock.class);
        PurchasedStock purchasedStock1 = getPurchasedStockSample1();
        PurchasedStock purchasedStock2 = new PurchasedStock();
        assertThat(purchasedStock1).isNotEqualTo(purchasedStock2);

        purchasedStock2.setId(purchasedStock1.getId());
        assertThat(purchasedStock1).isEqualTo(purchasedStock2);

        purchasedStock2 = getPurchasedStockSample2();
        assertThat(purchasedStock1).isNotEqualTo(purchasedStock2);
    }

    @Test
    void purchasedWareTest() {
        PurchasedStock purchasedStock = getPurchasedStockRandomSampleGenerator();
        Ware wareBack = getWareRandomSampleGenerator();

        purchasedStock.setPurchasedWare(wareBack);
        assertThat(purchasedStock.getPurchasedWare()).isEqualTo(wareBack);

        purchasedStock.purchasedWare(null);
        assertThat(purchasedStock.getPurchasedWare()).isNull();
    }

    @Test
    void purchaseTest() {
        PurchasedStock purchasedStock = getPurchasedStockRandomSampleGenerator();
        Purchase purchaseBack = getPurchaseRandomSampleGenerator();

        purchasedStock.setPurchase(purchaseBack);
        assertThat(purchasedStock.getPurchase()).isEqualTo(purchaseBack);

        purchasedStock.purchase(null);
        assertThat(purchasedStock.getPurchase()).isNull();
    }
}
