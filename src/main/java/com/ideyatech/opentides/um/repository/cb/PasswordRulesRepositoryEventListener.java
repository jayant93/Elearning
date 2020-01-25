package com.ideyatech.opentides.um.repository.cb;

import com.ideyatech.opentides.um.entity.PasswordRules;
import org.springframework.data.rest.core.annotation.HandleAfterCreate;
import org.springframework.data.rest.core.annotation.HandleAfterSave;
import org.springframework.data.rest.core.annotation.RepositoryEventHandler;

/**
 * Created by Gino on 10/4/2016.
 */
@RepositoryEventHandler
public class PasswordRulesRepositoryEventListener {

    @HandleAfterCreate
    @HandleAfterSave
    public void handleSave(PasswordRules passwordRules) {
        
    }

}
