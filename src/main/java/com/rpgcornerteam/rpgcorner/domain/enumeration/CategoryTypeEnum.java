package com.rpgcornerteam.rpgcorner.domain.enumeration;

/**
 * A CategoryTypeEnum enum.
 * <p>
 * Enum a fő kategóriák (main category) és az alkategóriák (sub category) kezeléséhez.
 *
 * @author Kárpáti Gábor
 */
public enum CategoryTypeEnum {
    /**
     *
   * Fő kategória

     */
    MAIN_CATEGORY("Fő kategória"),
    /**
     *
   * Alkategória

     */
    SUB_CATEGORY("Alkategória");

    private final String value;

    CategoryTypeEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
