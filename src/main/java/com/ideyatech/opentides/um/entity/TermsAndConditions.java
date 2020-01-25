package com.ideyatech.opentides.um.entity;

import com.ideyatech.opentides.core.entity.Template;
import org.springframework.data.couchbase.core.mapping.Document;

import javax.persistence.*;

/**
 * Created by Gino on 9/7/2016.
 */
@Entity
@Table(name = "TAC")
@Document
public class TermsAndConditions extends BaseUMEntity {

    /**
     * Category of {@link Template}
     */
    public static final String TAC_CATEGORY = "TERMS_AND_CONDITIONS";

    /**
     *
     */
    @ManyToOne
    @JoinColumn(name = "BODY")
    private Template body;

    /**
     * Transient field to store template body
     */
    @Transient
    private String templateBody;

    public Template getBody() {
        return body;
    }

    public void setBody(Template body) {
        this.body = body;
    }

    public String getTemplateBody() {
        return templateBody;
    }

    public void setTemplateBody(String templateBody) {
        this.templateBody = templateBody;
    }
}
