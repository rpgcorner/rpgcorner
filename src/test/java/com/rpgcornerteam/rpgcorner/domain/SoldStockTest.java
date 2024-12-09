package com.rpgcornerteam.rpgcorner.domain;

import static com.rpgcornerteam.rpgcorner.domain.SaleTestSamples.*;
import static com.rpgcornerteam.rpgcorner.domain.SoldStockTestSamples.*;
import static com.rpgcornerteam.rpgcorner.domain.WareTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.rpgcornerteam.rpgcorner.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class SoldStockTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(SoldStock.class);
        SoldStock soldStock1 = getSoldStockSample1();
        SoldStock soldStock2 = new SoldStock();
        assertThat(soldStock1).isNotEqualTo(soldStock2);

        soldStock2.setId(soldStock1.getId());
        assertThat(soldStock1).isEqualTo(soldStock2);

        soldStock2 = getSoldStockSample2();
        assertThat(soldStock1).isNotEqualTo(soldStock2);
    }

    @Test
    void soldWareTest() {
        SoldStock soldStock = getSoldStockRandomSampleGenerator();
        Ware wareBack = getWareRandomSampleGenerator();

        soldStock.setSoldWare(wareBack);
        assertThat(soldStock.getSoldWare()).isEqualTo(wareBack);

        soldStock.soldWare(null);
        assertThat(soldStock.getSoldWare()).isNull();
    }

    @Test
    void saleTest() {
        SoldStock soldStock = getSoldStockRandomSampleGenerator();
        Sale saleBack = getSaleRandomSampleGenerator();

        soldStock.setSale(saleBack);
        assertThat(soldStock.getSale()).isEqualTo(saleBack);

        soldStock.sale(null);
        assertThat(soldStock.getSale()).isNull();
    }
}
