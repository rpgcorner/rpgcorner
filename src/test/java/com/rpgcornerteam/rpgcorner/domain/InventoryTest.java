package com.rpgcornerteam.rpgcorner.domain;

import static com.rpgcornerteam.rpgcorner.domain.InventoryTestSamples.*;
import static com.rpgcornerteam.rpgcorner.domain.WareTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.rpgcornerteam.rpgcorner.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class InventoryTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Inventory.class);
        Inventory inventory1 = getInventorySample1();
        Inventory inventory2 = new Inventory();
        assertThat(inventory1).isNotEqualTo(inventory2);

        inventory2.setId(inventory1.getId());
        assertThat(inventory1).isEqualTo(inventory2);

        inventory2 = getInventorySample2();
        assertThat(inventory1).isNotEqualTo(inventory2);
    }

    @Test
    void wareTest() {
        Inventory inventory = getInventoryRandomSampleGenerator();
        Ware wareBack = getWareRandomSampleGenerator();

        inventory.setWare(wareBack);
        assertThat(inventory.getWare()).isEqualTo(wareBack);

        inventory.ware(null);
        assertThat(inventory.getWare()).isNull();
    }
}
