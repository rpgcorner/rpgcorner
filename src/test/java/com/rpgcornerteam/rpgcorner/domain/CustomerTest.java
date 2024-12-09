package com.rpgcornerteam.rpgcorner.domain;

import static com.rpgcornerteam.rpgcorner.domain.ContactTestSamples.*;
import static com.rpgcornerteam.rpgcorner.domain.CustomerTestSamples.*;
import static com.rpgcornerteam.rpgcorner.domain.ProductReturnTestSamples.*;
import static com.rpgcornerteam.rpgcorner.domain.SaleTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.rpgcornerteam.rpgcorner.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class CustomerTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Customer.class);
        Customer customer1 = getCustomerSample1();
        Customer customer2 = new Customer();
        assertThat(customer1).isNotEqualTo(customer2);

        customer2.setId(customer1.getId());
        assertThat(customer1).isEqualTo(customer2);

        customer2 = getCustomerSample2();
        assertThat(customer1).isNotEqualTo(customer2);
    }

    @Test
    void contactTest() {
        Customer customer = getCustomerRandomSampleGenerator();
        Contact contactBack = getContactRandomSampleGenerator();

        customer.setContact(contactBack);
        assertThat(customer.getContact()).isEqualTo(contactBack);

        customer.contact(null);
        assertThat(customer.getContact()).isNull();
    }

    @Test
    void saleTest() {
        Customer customer = getCustomerRandomSampleGenerator();
        Sale saleBack = getSaleRandomSampleGenerator();

        customer.addSale(saleBack);
        assertThat(customer.getSales()).containsOnly(saleBack);
        assertThat(saleBack.getSoldForCustomer()).isEqualTo(customer);

        customer.removeSale(saleBack);
        assertThat(customer.getSales()).doesNotContain(saleBack);
        assertThat(saleBack.getSoldForCustomer()).isNull();

        customer.sales(new HashSet<>(Set.of(saleBack)));
        assertThat(customer.getSales()).containsOnly(saleBack);
        assertThat(saleBack.getSoldForCustomer()).isEqualTo(customer);

        customer.setSales(new HashSet<>());
        assertThat(customer.getSales()).doesNotContain(saleBack);
        assertThat(saleBack.getSoldForCustomer()).isNull();
    }

    @Test
    void productReturnTest() {
        Customer customer = getCustomerRandomSampleGenerator();
        ProductReturn productReturnBack = getProductReturnRandomSampleGenerator();

        customer.addProductReturn(productReturnBack);
        assertThat(customer.getProductReturns()).containsOnly(productReturnBack);
        assertThat(productReturnBack.getReturnedByCustomer()).isEqualTo(customer);

        customer.removeProductReturn(productReturnBack);
        assertThat(customer.getProductReturns()).doesNotContain(productReturnBack);
        assertThat(productReturnBack.getReturnedByCustomer()).isNull();

        customer.productReturns(new HashSet<>(Set.of(productReturnBack)));
        assertThat(customer.getProductReturns()).containsOnly(productReturnBack);
        assertThat(productReturnBack.getReturnedByCustomer()).isEqualTo(customer);

        customer.setProductReturns(new HashSet<>());
        assertThat(customer.getProductReturns()).doesNotContain(productReturnBack);
        assertThat(productReturnBack.getReturnedByCustomer()).isNull();
    }
}
