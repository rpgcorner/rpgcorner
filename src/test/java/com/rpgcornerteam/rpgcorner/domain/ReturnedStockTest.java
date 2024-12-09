package com.rpgcornerteam.rpgcorner.domain;

import static com.rpgcornerteam.rpgcorner.domain.ProductReturnTestSamples.*;
import static com.rpgcornerteam.rpgcorner.domain.ReturnedStockTestSamples.*;
import static com.rpgcornerteam.rpgcorner.domain.WareTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.rpgcornerteam.rpgcorner.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ReturnedStockTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ReturnedStock.class);
        ReturnedStock returnedStock1 = getReturnedStockSample1();
        ReturnedStock returnedStock2 = new ReturnedStock();
        assertThat(returnedStock1).isNotEqualTo(returnedStock2);

        returnedStock2.setId(returnedStock1.getId());
        assertThat(returnedStock1).isEqualTo(returnedStock2);

        returnedStock2 = getReturnedStockSample2();
        assertThat(returnedStock1).isNotEqualTo(returnedStock2);
    }

    @Test
    void returnedWareTest() {
        ReturnedStock returnedStock = getReturnedStockRandomSampleGenerator();
        Ware wareBack = getWareRandomSampleGenerator();

        returnedStock.setReturnedWare(wareBack);
        assertThat(returnedStock.getReturnedWare()).isEqualTo(wareBack);

        returnedStock.returnedWare(null);
        assertThat(returnedStock.getReturnedWare()).isNull();
    }

    @Test
    void productReturnTest() {
        ReturnedStock returnedStock = getReturnedStockRandomSampleGenerator();
        ProductReturn productReturnBack = getProductReturnRandomSampleGenerator();

        returnedStock.setProductReturn(productReturnBack);
        assertThat(returnedStock.getProductReturn()).isEqualTo(productReturnBack);

        returnedStock.productReturn(null);
        assertThat(returnedStock.getProductReturn()).isNull();
    }
}
