package com.rpgcornerteam.rpgcorner.domain;

import static com.rpgcornerteam.rpgcorner.domain.CustomerTestSamples.*;
import static com.rpgcornerteam.rpgcorner.domain.ProductReturnTestSamples.*;
import static com.rpgcornerteam.rpgcorner.domain.SaleTestSamples.*;
import static com.rpgcornerteam.rpgcorner.domain.SoldStockTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.rpgcornerteam.rpgcorner.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class SaleTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Sale.class);
        Sale sale1 = getSaleSample1();
        Sale sale2 = new Sale();
        assertThat(sale1).isNotEqualTo(sale2);

        sale2.setId(sale1.getId());
        assertThat(sale1).isEqualTo(sale2);

        sale2 = getSaleSample2();
        assertThat(sale1).isNotEqualTo(sale2);
    }

    @Test
    void soldStockTest() {
        Sale sale = getSaleRandomSampleGenerator();
        SoldStock soldStockBack = getSoldStockRandomSampleGenerator();

        sale.addSoldStock(soldStockBack);
        assertThat(sale.getSoldStocks()).containsOnly(soldStockBack);
        assertThat(soldStockBack.getSale()).isEqualTo(sale);

        sale.removeSoldStock(soldStockBack);
        assertThat(sale.getSoldStocks()).doesNotContain(soldStockBack);
        assertThat(soldStockBack.getSale()).isNull();

        sale.soldStocks(new HashSet<>(Set.of(soldStockBack)));
        assertThat(sale.getSoldStocks()).containsOnly(soldStockBack);
        assertThat(soldStockBack.getSale()).isEqualTo(sale);

        sale.setSoldStocks(new HashSet<>());
        assertThat(sale.getSoldStocks()).doesNotContain(soldStockBack);
        assertThat(soldStockBack.getSale()).isNull();
    }

    @Test
    void soldForCustomerTest() {
        Sale sale = getSaleRandomSampleGenerator();
        Customer customerBack = getCustomerRandomSampleGenerator();

        sale.setSoldForCustomer(customerBack);
        assertThat(sale.getSoldForCustomer()).isEqualTo(customerBack);

        sale.soldForCustomer(null);
        assertThat(sale.getSoldForCustomer()).isNull();
    }

    @Test
    void productReturnTest() {
        Sale sale = getSaleRandomSampleGenerator();
        ProductReturn productReturnBack = getProductReturnRandomSampleGenerator();

        sale.addProductReturn(productReturnBack);
        assertThat(sale.getProductReturns()).containsOnly(productReturnBack);
        assertThat(productReturnBack.getSale()).isEqualTo(sale);

        sale.removeProductReturn(productReturnBack);
        assertThat(sale.getProductReturns()).doesNotContain(productReturnBack);
        assertThat(productReturnBack.getSale()).isNull();

        sale.productReturns(new HashSet<>(Set.of(productReturnBack)));
        assertThat(sale.getProductReturns()).containsOnly(productReturnBack);
        assertThat(productReturnBack.getSale()).isEqualTo(sale);

        sale.setProductReturns(new HashSet<>());
        assertThat(sale.getProductReturns()).doesNotContain(productReturnBack);
        assertThat(productReturnBack.getSale()).isNull();
    }
}
