package com.ideyatech.opentides.um.validator;

import com.ideyatech.opentides.um.entity.PasswordRules;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Handles validation of password using OT4 rules.
 * The rule follows this format :
 * nM:nC:nN:nS (Minimum Length:Number of capital letters:Number of Numerical characters:Number of special characters
 *
 * @author Gino
 */
public class Ot4RulesPasswordValidator implements PasswordValidator {

    @Override
    public boolean isPasswordValid(String password, String rule) {
        String [] rules = rule.split(":");

        String minLengthRule = rules[0];
        int minLength = getNumber(minLengthRule);
        if(password.length() < minLength) {
            return false;
        }
        String nextRule = rules[1];

        if(!PasswordRules.DEFAULT_MIN_CHAR_TYPES.equals((nextRule))) {
            if(!isRuleFollowed(password, rules, 1, "[A-Z]")) {
                return false;
            }

            if(!isRuleFollowed(password, rules, 2, "\\d")) {
                return false;
            }

            if(!isRuleFollowed(password, rules, 3, "[ !\"#$%&'()*+,\\-./:;<=>?@\\[\\]^_`{|}~]")) {
                return false;
            }
        }

        return true;
    }

    /**
     * Checks if the rule is followed.
     *
     * @param password password to check
     * @param rules arrays of rules
     * @param index the index of rule to check
     * @param regex the regex to use for checking
     * @return
     */
    private boolean isRuleFollowed(String password, String [] rules, int index, String regex) {
        String minRule = rules[index];
        int minCount = getNumber(minRule);
        if(minCount > 0) {
            if(!validatePasswordRule(password, minCount, regex)) {
                return false;
            }
        }
        return true;
    }

    private boolean validatePasswordRule(String password, int minCount, String pattern) {
        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(password);
        int count = 0;
        while(m.find()) {
            count++;
        }
        return count >= minCount;
    }

    private int getNumber(String rule) {
        Pattern p = Pattern.compile("\\d+");
        Matcher m = p.matcher(rule);
        while (m.find()) {
            return Integer.valueOf(m.group());
        }
        throw new RuntimeException("Invalid rule.");
    }

}
