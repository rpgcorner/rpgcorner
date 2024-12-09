package com.rpgcornerteam.rpgcorner.domain;

import static com.rpgcornerteam.rpgcorner.domain.CategoryTestSamples.*;
import static com.rpgcornerteam.rpgcorner.domain.DisposedStockTestSamples.*;
import static com.rpgcornerteam.rpgcorner.domain.InventoryTestSamples.*;
import static com.rpgcornerteam.rpgcorner.domain.PurchasedStockTestSamples.*;
import static com.rpgcornerteam.rpgcorner.domain.ReturnedStockTestSamples.*;
import static com.rpgcornerteam.rpgcorner.domain.SoldStockTestSamples.*;
import static com.rpgcornerteam.rpgcorner.domain.WareTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.rpgcornerteam.rpgcorner.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class WareTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Ware.class);
        Ware ware1 = getWareSample1();
        Ware ware2 = new Ware();
        assertThat(ware1).isNotEqualTo(ware2);

        ware2.setId(ware1.getId());
        assertThat(ware1).isEqualTo(ware2);

        ware2 = getWareSample2();
        assertThat(ware1).isNotEqualTo(ware2);
    }

    @Test
    void mainCategoryTest() {
        Ware ware = getWareRandomSampleGenerator();
        Category categoryBack = getCategoryRandomSampleGenerator();

        ware.setMainCategory(categoryBack);
        assertThat(ware.getMainCategory()).isEqualTo(categoryBack);

        ware.mainCategory(null);
        assertThat(ware.getMainCategory()).isNull();
    }

    @Test
    void subCategoryTest() {
        Ware ware = getWareRandomSampleGenerator();
        Category categoryBack = getCategoryRandomSampleGenerator();

        ware.setSubCategory(categoryBack);
        assertThat(ware.getSubCategory()).isEqualTo(categoryBack);

        ware.subCategory(null);
        assertThat(ware.getSubCategory()).isNull();
    }

    @Test
    void inventoryTest() {
        Ware ware = getWareRandomSampleGenerator();
        Inventory inventoryBack = getInventoryRandomSampleGenerator();

        ware.setInventory(inventoryBack);
        assertThat(ware.getInventory()).isEqualTo(inventoryBack);
        assertThat(inventoryBack.getWare()).isEqualTo(ware);

        ware.inventory(null);
        assertThat(ware.getInventory()).isNull();
        assertThat(inventoryBack.getWare()).isNull();
    }

    @Test
    void disposedStockTest() {
        Ware ware = getWareRandomSampleGenerator();
        DisposedStock disposedStockBack = getDisposedStockRandomSampleGenerator();

        ware.setDisposedStock(disposedStockBack);
        assertThat(ware.getDisposedStock()).isEqualTo(disposedStockBack);
        assertThat(disposedStockBack.getDisposedWare()).isEqualTo(ware);

        ware.disposedStock(null);
        assertThat(ware.getDisposedStock()).isNull();
        assertThat(disposedStockBack.getDisposedWare()).isNull();
    }

    @Test
    void purchasedStockTest() {
        Ware ware = getWareRandomSampleGenerator();
        PurchasedStock purchasedStockBack = getPurchasedStockRandomSampleGenerator();

        ware.setPurchasedStock(purchasedStockBack);
        assertThat(ware.getPurchasedStock()).isEqualTo(purchasedStockBack);
        assertThat(purchasedStockBack.getPurchasedWare()).isEqualTo(ware);

        ware.purchasedStock(null);
        assertThat(ware.getPurchasedStock()).isNull();
        assertThat(purchasedStockBack.getPurchasedWare()).isNull();
    }

    @Test
    void returnedStockTest() {
        Ware ware = getWareRandomSampleGenerator();
        ReturnedStock returnedStockBack = getReturnedStockRandomSampleGenerator();

        ware.setReturnedStock(returnedStockBack);
        assertThat(ware.getReturnedStock()).isEqualTo(returnedStockBack);
        assertThat(returnedStockBack.getReturnedWare()).isEqualTo(ware);

        ware.returnedStock(null);
        assertThat(ware.getReturnedStock()).isNull();
        assertThat(returnedStockBack.getReturnedWare()).isNull();
    }

    @Test
    void soldStockTest() {
        Ware ware = getWareRandomSampleGenerator();
        SoldStock soldStockBack = getSoldStockRandomSampleGenerator();

        ware.setSoldStock(soldStockBack);
        assertThat(ware.getSoldStock()).isEqualTo(soldStockBack);
        assertThat(soldStockBack.getSoldWare()).isEqualTo(ware);

        ware.soldStock(null);
        assertThat(ware.getSoldStock()).isNull();
        assertThat(soldStockBack.getSoldWare()).isNull();
    }
}
