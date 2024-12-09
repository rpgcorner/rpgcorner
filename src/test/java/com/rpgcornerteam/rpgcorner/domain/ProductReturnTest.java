package com.rpgcornerteam.rpgcorner.domain;

import static com.rpgcornerteam.rpgcorner.domain.CustomerTestSamples.*;
import static com.rpgcornerteam.rpgcorner.domain.ProductReturnTestSamples.*;
import static com.rpgcornerteam.rpgcorner.domain.ReturnedStockTestSamples.*;
import static com.rpgcornerteam.rpgcorner.domain.SaleTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.rpgcornerteam.rpgcorner.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class ProductReturnTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ProductReturn.class);
        ProductReturn productReturn1 = getProductReturnSample1();
        ProductReturn productReturn2 = new ProductReturn();
        assertThat(productReturn1).isNotEqualTo(productReturn2);

        productReturn2.setId(productReturn1.getId());
        assertThat(productReturn1).isEqualTo(productReturn2);

        productReturn2 = getProductReturnSample2();
        assertThat(productReturn1).isNotEqualTo(productReturn2);
    }

    @Test
    void returnedStockTest() {
        ProductReturn productReturn = getProductReturnRandomSampleGenerator();
        ReturnedStock returnedStockBack = getReturnedStockRandomSampleGenerator();

        productReturn.addReturnedStock(returnedStockBack);
        assertThat(productReturn.getReturnedStocks()).containsOnly(returnedStockBack);
        assertThat(returnedStockBack.getProductReturn()).isEqualTo(productReturn);

        productReturn.removeReturnedStock(returnedStockBack);
        assertThat(productReturn.getReturnedStocks()).doesNotContain(returnedStockBack);
        assertThat(returnedStockBack.getProductReturn()).isNull();

        productReturn.returnedStocks(new HashSet<>(Set.of(returnedStockBack)));
        assertThat(productReturn.getReturnedStocks()).containsOnly(returnedStockBack);
        assertThat(returnedStockBack.getProductReturn()).isEqualTo(productReturn);

        productReturn.setReturnedStocks(new HashSet<>());
        assertThat(productReturn.getReturnedStocks()).doesNotContain(returnedStockBack);
        assertThat(returnedStockBack.getProductReturn()).isNull();
    }

    @Test
    void saleTest() {
        ProductReturn productReturn = getProductReturnRandomSampleGenerator();
        Sale saleBack = getSaleRandomSampleGenerator();

        productReturn.setSale(saleBack);
        assertThat(productReturn.getSale()).isEqualTo(saleBack);

        productReturn.sale(null);
        assertThat(productReturn.getSale()).isNull();
    }

    @Test
    void returnedByCustomerTest() {
        ProductReturn productReturn = getProductReturnRandomSampleGenerator();
        Customer customerBack = getCustomerRandomSampleGenerator();

        productReturn.setReturnedByCustomer(customerBack);
        assertThat(productReturn.getReturnedByCustomer()).isEqualTo(customerBack);

        productReturn.returnedByCustomer(null);
        assertThat(productReturn.getReturnedByCustomer()).isNull();
    }
}
