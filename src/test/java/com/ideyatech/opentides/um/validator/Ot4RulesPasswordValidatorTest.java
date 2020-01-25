package com.ideyatech.opentides.um.validator;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * Created by Gino on 9/5/2016.
 */
public class Ot4RulesPasswordValidatorTest {

    private Ot4RulesPasswordValidator passwordValidator = new Ot4RulesPasswordValidator();

    @Test
    public void testIsPasswordValid() {
        String rule = "8M:NONE";
        String password = "abcd123";
        assertFalse(passwordValidator.isPasswordValid(password, rule));

        rule = "8M:4C:1N:0S";
        password = "ABCd12456";
        assertFalse(passwordValidator.isPasswordValid(password, rule));

        rule = "8M:4C:1N:0S";
        password = "ABCD12456";
        assertTrue(passwordValidator.isPasswordValid(password, rule));

        rule = "8M:4C:3N:0S";
        password = "ABCD12asdvaA";
        assertFalse(passwordValidator.isPasswordValid(password, rule));

        rule = "8M:4C:3N:0S";
        password = "ABCD123sdvaA";
        assertTrue(passwordValidator.isPasswordValid(password, rule));

        rule = "8M:2C:3N:2S";
        password = "abCD123sdvaA";
        assertFalse(passwordValidator.isPasswordValid(password, rule));

        rule = "8M:2C:3N:2S";
        password = "ABcs123sdva[]";
        assertTrue(passwordValidator.isPasswordValid(password, rule));
    }

}
