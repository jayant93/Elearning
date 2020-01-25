package com.ideyatech.opentides.um.entity;

import com.couchbase.client.java.repository.annotation.Field;
import com.ideyatech.opentides.core.entity.BaseEntity;
import com.ideyatech.opentides.core.entity.CustomField;
import org.springframework.data.couchbase.core.mapping.Document;

import javax.persistence.*;

/**
 * Created by Gino on 10/18/2016.
 */
@Entity
@Table(name = "USER_CUSTOM_VALUES")
@Document
public class BaseUserCustomValue extends BaseEntity {

    @JoinColumn(name = "BASE_USER_ID")
    @ManyToOne
    private BaseUser baseUser;

    @OneToOne
    @JoinColumn(name = "CUSTOM_FIELD_KEY", referencedColumnName = "KEY_")
    private CustomField customField;

    @Field
    @Column(name = "CUSTOM_FIELD_KEY", insertable = false, updatable = false)
    private String customFieldKey;

    /**
     * The value of the custom field.
     */
    @Field
    @Column(name = "VALUE_")
    private String value;

    public BaseUser getBaseUser() {
        return baseUser;
    }

    public void setBaseUser(BaseUser baseUser) {
        this.baseUser = baseUser;
    }

    public CustomField getCustomField() {
        return customField;
    }

    public void setCustomField(CustomField customField) {
        this.customField = customField;
    }

    public String getCustomFieldKey() {
        return customFieldKey;
    }

    public void setCustomFieldKey(String customFieldKey) {
        this.customFieldKey = customFieldKey;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
