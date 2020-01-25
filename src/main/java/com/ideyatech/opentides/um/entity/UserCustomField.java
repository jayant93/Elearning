package com.ideyatech.opentides.um.entity;

import com.ideyatech.opentides.core.entity.CustomField;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

/**
 * @author Gino
 */
@Document
@Entity
public class UserCustomField extends CustomField {

    @ManyToOne
    @JoinColumn(name = "APPLICATION_ID")
    private Application application;

    public Application getApplication() {
        return application;
    }

    public void setApplication(Application application) {
        this.application = application;
    }
}
