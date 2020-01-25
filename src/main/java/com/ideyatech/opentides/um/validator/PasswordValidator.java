package com.ideyatech.opentides.um.validator;

/**
 * @author Gino
 */
public interface PasswordValidator {

    /**
     * Validate the password using the given rule.
     *
     * @param password the password to validate.
     * @param rule the rule to check
     * @return true if valid, false otherwise
     */
    boolean isPasswordValid(String password, String rule);

}
