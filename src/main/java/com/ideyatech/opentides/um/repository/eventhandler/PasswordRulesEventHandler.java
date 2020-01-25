package com.ideyatech.opentides.um.repository.eventhandler;

import com.ideyatech.opentides.um.entity.PasswordRules;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.rest.core.annotation.HandleBeforeCreate;
import org.springframework.data.rest.core.annotation.HandleBeforeSave;
import org.springframework.data.rest.core.annotation.RepositoryEventHandler;
import org.springframework.stereotype.Component;

/**
 * Created by Gino on 9/6/2016.
 */
@Component
@RepositoryEventHandler
public class PasswordRulesEventHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(PasswordRulesEventHandler.class);

    @HandleBeforeCreate
    public void onBeforeCreate(PasswordRules passwordRules) {
        String minCharType = passwordRules.buildMinimumCharTypes();
        LOGGER.debug("[Before Create] Setting minimum character types to {}...", minCharType);
        passwordRules.setMinimumCharTypes(minCharType);
    }

    @HandleBeforeSave
    public void onBeforeUpdate(PasswordRules passwordRules) {
        String minCharType = passwordRules.buildMinimumCharTypes();
        LOGGER.debug("[Before Update] {}C {}N {}S", passwordRules.getMinCapitalLetter(),
                passwordRules.getMinNumbers(), passwordRules.getMinSpecialChar());
        LOGGER.debug("[Before Update] Setting minimum character types to {}...", minCharType);
        if(!minCharType.equals(passwordRules.getMinimumCharTypes())) {
            passwordRules.setMinimumCharTypes(minCharType);
        }
    }

}
