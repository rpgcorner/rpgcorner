package com.rpgcornerteam.rpgcorner.web.rest.errors;

@SuppressWarnings("java:S110") // Inheritance tree of classes should not be too deep
public class NameAndProductCodeAlreadyUsedException extends BadRequestAlertException {

    private static final long serialVersionUID = 1L;

    public NameAndProductCodeAlreadyUsedException() {
        super(
            ErrorConstants.NAME_AND_PRODUCT_CODE_ALREADY_USED_TYPE,
            "Name and/or product code already used!",
            "productManagement",
            "nameandproductcodeexists"
        );
    }
}
