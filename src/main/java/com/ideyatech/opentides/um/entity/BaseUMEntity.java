package com.ideyatech.opentides.um.entity;

import com.couchbase.client.java.repository.annotation.Field;
import com.ideyatech.opentides.core.entity.BaseEntity;

import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;

/**
 * Base class for User Management
 *
 * Created by Gino on 8/30/2016.
 */
@MappedSuperclass
public abstract class BaseUMEntity extends BaseEntity {

    /**
     * The application where this entity belongs
     */
    @Field
    @ManyToOne
    @JoinColumn(name = "APPLICATION_ID")
    private Application application;

    /**
     * ID of the application. For JPA can be use for searching, for couchbase for actual relationship to application
     */
    @Field
    @Column(name = "APPLICATION_ID", updatable = false, insertable = false)
    private String applicationId;


    /**
     * Sets new The application where this entity belongs.
     *
     * @param application New value of The application where this entity belongs.
     */
    public void setApplication(Application application) {
        this.application = application;
    }

    /**
     * Gets The application where this entity belongs.
     *
     * @return Value of The application where this entity belongs.
     */
    public Application getApplication() {
        return application;
    }

    public String getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(String applicationId) {
        this.applicationId = applicationId;
    }
}
