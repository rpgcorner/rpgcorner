package com.rpgcornerteam.rpgcorner.web.rest.errors;

@SuppressWarnings("java:S110") // Inheritance tree of classes should not be too deep
public class CustomerEmailAlreadyUsedException extends BadRequestAlertException {

    private static final long serialVersionUID = 1L;

    public CustomerEmailAlreadyUsedException() {
        super(ErrorConstants.EMAIL_ALREADY_USED_TYPE, "Email address already used!", "userManagement", "customeremailexists");
    }
}
