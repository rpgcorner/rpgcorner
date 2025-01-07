package com.rpgcornerteam.rpgcorner.web.rest.errors;

@SuppressWarnings("java:S110") // Inheritance tree of classes should not be too deep
public class NameConflictException extends BadRequestAlertException {

    private static final long serialVersionUID = 1L;

    public NameConflictException() {
        super(ErrorConstants.NAME_CONFLICT_TYPE, "Name already exists!", "userManagement", "nameexists");
    }
}
