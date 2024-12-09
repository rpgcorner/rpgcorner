package com.rpgcornerteam.rpgcorner.domain;

import static com.rpgcornerteam.rpgcorner.domain.PurchaseTestSamples.*;
import static com.rpgcornerteam.rpgcorner.domain.PurchasedStockTestSamples.*;
import static com.rpgcornerteam.rpgcorner.domain.SupplierTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.rpgcornerteam.rpgcorner.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class PurchaseTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Purchase.class);
        Purchase purchase1 = getPurchaseSample1();
        Purchase purchase2 = new Purchase();
        assertThat(purchase1).isNotEqualTo(purchase2);

        purchase2.setId(purchase1.getId());
        assertThat(purchase1).isEqualTo(purchase2);

        purchase2 = getPurchaseSample2();
        assertThat(purchase1).isNotEqualTo(purchase2);
    }

    @Test
    void purchasedStockTest() {
        Purchase purchase = getPurchaseRandomSampleGenerator();
        PurchasedStock purchasedStockBack = getPurchasedStockRandomSampleGenerator();

        purchase.addPurchasedStock(purchasedStockBack);
        assertThat(purchase.getPurchasedStocks()).containsOnly(purchasedStockBack);
        assertThat(purchasedStockBack.getPurchase()).isEqualTo(purchase);

        purchase.removePurchasedStock(purchasedStockBack);
        assertThat(purchase.getPurchasedStocks()).doesNotContain(purchasedStockBack);
        assertThat(purchasedStockBack.getPurchase()).isNull();

        purchase.purchasedStocks(new HashSet<>(Set.of(purchasedStockBack)));
        assertThat(purchase.getPurchasedStocks()).containsOnly(purchasedStockBack);
        assertThat(purchasedStockBack.getPurchase()).isEqualTo(purchase);

        purchase.setPurchasedStocks(new HashSet<>());
        assertThat(purchase.getPurchasedStocks()).doesNotContain(purchasedStockBack);
        assertThat(purchasedStockBack.getPurchase()).isNull();
    }

    @Test
    void purchasedFromSupplierTest() {
        Purchase purchase = getPurchaseRandomSampleGenerator();
        Supplier supplierBack = getSupplierRandomSampleGenerator();

        purchase.setPurchasedFromSupplier(supplierBack);
        assertThat(purchase.getPurchasedFromSupplier()).isEqualTo(supplierBack);

        purchase.purchasedFromSupplier(null);
        assertThat(purchase.getPurchasedFromSupplier()).isNull();
    }
}
