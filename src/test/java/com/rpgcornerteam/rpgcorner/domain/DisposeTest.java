package com.rpgcornerteam.rpgcorner.domain;

import static com.rpgcornerteam.rpgcorner.domain.DisposeTestSamples.*;
import static com.rpgcornerteam.rpgcorner.domain.DisposedStockTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.rpgcornerteam.rpgcorner.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class DisposeTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Dispose.class);
        Dispose dispose1 = getDisposeSample1();
        Dispose dispose2 = new Dispose();
        assertThat(dispose1).isNotEqualTo(dispose2);

        dispose2.setId(dispose1.getId());
        assertThat(dispose1).isEqualTo(dispose2);

        dispose2 = getDisposeSample2();
        assertThat(dispose1).isNotEqualTo(dispose2);
    }

    @Test
    void disposedStockTest() {
        Dispose dispose = getDisposeRandomSampleGenerator();
        DisposedStock disposedStockBack = getDisposedStockRandomSampleGenerator();

        dispose.addDisposedStock(disposedStockBack);
        assertThat(dispose.getDisposedStocks()).containsOnly(disposedStockBack);
        assertThat(disposedStockBack.getDispose()).isEqualTo(dispose);

        dispose.removeDisposedStock(disposedStockBack);
        assertThat(dispose.getDisposedStocks()).doesNotContain(disposedStockBack);
        assertThat(disposedStockBack.getDispose()).isNull();

        dispose.disposedStocks(new HashSet<>(Set.of(disposedStockBack)));
        assertThat(dispose.getDisposedStocks()).containsOnly(disposedStockBack);
        assertThat(disposedStockBack.getDispose()).isEqualTo(dispose);

        dispose.setDisposedStocks(new HashSet<>());
        assertThat(dispose.getDisposedStocks()).doesNotContain(disposedStockBack);
        assertThat(disposedStockBack.getDispose()).isNull();
    }
}
