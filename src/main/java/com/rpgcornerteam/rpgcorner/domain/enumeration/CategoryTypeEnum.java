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
    MAIN_CATEGORY("Main category"),
    /**
     * 
   * Alkategória

     */
    SUB_CATEGORY("Sub category");

    private final String value;

    CategoryTypeEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
