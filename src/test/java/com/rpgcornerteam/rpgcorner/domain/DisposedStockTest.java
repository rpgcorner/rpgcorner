package com.rpgcornerteam.rpgcorner.domain;

import static com.rpgcornerteam.rpgcorner.domain.DisposeTestSamples.*;
import static com.rpgcornerteam.rpgcorner.domain.DisposedStockTestSamples.*;
import static com.rpgcornerteam.rpgcorner.domain.WareTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.rpgcornerteam.rpgcorner.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class DisposedStockTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(DisposedStock.class);
        DisposedStock disposedStock1 = getDisposedStockSample1();
        DisposedStock disposedStock2 = new DisposedStock();
        assertThat(disposedStock1).isNotEqualTo(disposedStock2);

        disposedStock2.setId(disposedStock1.getId());
        assertThat(disposedStock1).isEqualTo(disposedStock2);

        disposedStock2 = getDisposedStockSample2();
        assertThat(disposedStock1).isNotEqualTo(disposedStock2);
    }

    @Test
    void disposedWareTest() {
        DisposedStock disposedStock = getDisposedStockRandomSampleGenerator();
        Ware wareBack = getWareRandomSampleGenerator();

        disposedStock.setDisposedWare(wareBack);
        assertThat(disposedStock.getDisposedWare()).isEqualTo(wareBack);

        disposedStock.disposedWare(null);
        assertThat(disposedStock.getDisposedWare()).isNull();
    }

    @Test
    void disposeTest() {
        DisposedStock disposedStock = getDisposedStockRandomSampleGenerator();
        Dispose disposeBack = getDisposeRandomSampleGenerator();

        disposedStock.setDispose(disposeBack);
        assertThat(disposedStock.getDispose()).isEqualTo(disposeBack);

        disposedStock.dispose(null);
        assertThat(disposedStock.getDispose()).isNull();
    }
}
