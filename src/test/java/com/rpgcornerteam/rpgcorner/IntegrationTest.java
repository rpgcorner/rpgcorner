package com.rpgcornerteam.rpgcorner;

import com.rpgcornerteam.rpgcorner.config.AsyncSyncConfiguration;
import com.rpgcornerteam.rpgcorner.config.EmbeddedSQL;
import com.rpgcornerteam.rpgcorner.config.JacksonConfiguration;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Base composite annotation for integration tests.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@SpringBootTest(classes = { RpgCornerApp.class, JacksonConfiguration.class, AsyncSyncConfiguration.class })
@EmbeddedSQL
public @interface IntegrationTest {
}
