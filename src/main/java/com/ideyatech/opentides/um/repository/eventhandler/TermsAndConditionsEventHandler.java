package com.ideyatech.opentides.um.repository.eventhandler;

import com.ideyatech.opentides.core.entity.Template;
import com.ideyatech.opentides.core.repository.TemplateRepository;
import com.ideyatech.opentides.um.entity.TermsAndConditions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.core.annotation.HandleBeforeCreate;
import org.springframework.data.rest.core.annotation.HandleBeforeSave;
import org.springframework.data.rest.core.annotation.RepositoryEventHandler;
import org.springframework.stereotype.Component;

/**
 * Created by Gino on 9/7/2016.
 */
@Component
@RepositoryEventHandler
public class TermsAndConditionsEventHandler {

    @Autowired
    private TemplateRepository templateRepository;

    @HandleBeforeCreate
    @HandleBeforeSave
    public void onBeforeCreateUpdate(TermsAndConditions tac) {
        Template template = tac.getBody();
        if(template == null) {
            template = new Template();
            template.setCategory(TermsAndConditions.TAC_CATEGORY);
            template.setKey("APPLICATION_TAC_" + tac.getApplication().getId());
            tac.setBody(template);
        }
        template.setBody(tac.getTemplateBody());
        templateRepository.save(tac.getBody());
    }

}
