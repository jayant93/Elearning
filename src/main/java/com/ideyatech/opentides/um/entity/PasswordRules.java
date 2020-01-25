package com.ideyatech.opentides.um.entity;

import com.couchbase.client.java.repository.annotation.Field;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.ideyatech.opentides.core.entity.BaseEntity;
import com.ideyatech.opentides.um.entity.Application;
import com.ideyatech.opentides.um.entity.BaseUMEntity;
import org.springframework.data.annotation.*;
import org.springframework.data.couchbase.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DBRef;

import javax.persistence.*;
import javax.persistence.Transient;

/**
 * Created by Gino on 8/30/2016.
 */
@Document
//@org.springframework.data.mongodb.core.mapping.Document
@Entity
@Table(name = "PASSWORD_RULES")
public class PasswordRules extends BaseEntity {

    public static final String DEFAULT_MIN_CHAR_TYPES = "NONE";
    public static final Integer NO_RESTRICTION = -1;

    @JsonBackReference
    @OneToOne
    @JoinColumn(name = "APPLICATION_ID")
    @DBRef
    private Application application;

    /**
     * The minimum password length. Default is 8
     */
    @Column(name = "MIN_LENGTH")
    @Field
    private Integer minimumLength;

    /**
     * The number of capital letters, numeric and special characters required for a password.
     * Format is: nC:nN:nS (Number of capital letters:Number of Numerical characters:Number of special characters
     *
     */
    @Column(name = "MIN_CHAR_TYPES")
    @Field
    private String minimumCharTypes;

    /**
     * The password validity. Default is 1M for 1 month. Possible formats:
     * nW - number of weeks
     * nM - number of months
     * nY - number of years
     * NONE - no expiration
     */
    @Column(name = "PASSWORD_EXPIRATION")
    @Field
    private String passwordExpiration;

    /**
     * Number of change password before allowing the same password. Default -1 or No Restriction.
     */
    @Field
    private Integer passwordRepeat;

    /**
     * Transient field to set minimum Capital Letters
     */
    @Transient
    private Integer minCapitalLetter;

    /**
     * Transient field to set minimum numbers
     */
    @Transient
    private Integer minNumbers;

    /**
     * Transient field to set minimum special characters
     */
    @Transient
    private Integer minSpecialChar;

    public Application getApplication() {
        return application;
    }

    public void setApplication(Application application) {
        this.application = application;
    }

    public Integer getMinimumLength() {
        return minimumLength;
    }

    public void setMinimumLength(Integer minimumLength) {
        this.minimumLength = minimumLength;
    }

    public String getMinimumCharTypes() {
        return minimumCharTypes;
    }

    public void setMinimumCharTypes(String minimumCharTypes) {
        this.minimumCharTypes = minimumCharTypes;
    }

    public String getPasswordExpiration() {
        return passwordExpiration;
    }

    public void setPasswordExpiration(String passwordExpiration) {
        this.passwordExpiration = passwordExpiration;
    }

    public Integer getPasswordRepeat() {
        return passwordRepeat;
    }

    public void setPasswordRepeat(Integer passwordRepeat) {
        this.passwordRepeat = passwordRepeat;
    }

    public Integer getMinCapitalLetter() {
        return minCapitalLetter;
    }

    public void setMinCapitalLetter(Integer minCapitalLetter) {
        this.minCapitalLetter = minCapitalLetter;
    }

    public Integer getMinNumbers() {
        return minNumbers;
    }

    public void setMinNumbers(Integer minNumbers) {
        this.minNumbers = minNumbers;
    }

    public Integer getMinSpecialChar() {
        return minSpecialChar;
    }

    public void setMinSpecialChar(Integer minSpecialChar) {
        this.minSpecialChar = minSpecialChar;
    }

    @PrePersist
    @PreUpdate
    public void prePersist() {
        if(this.minimumLength == null) {
            this.setMinimumLength(8);
        }
        if(this.minimumCharTypes == null) {
            this.setMinimumCharTypes(DEFAULT_MIN_CHAR_TYPES);
        }
        if(this.passwordExpiration == null) {
            this.setPasswordExpiration("1M");
        }
        if(this.passwordRepeat == null) {
            this.setPasswordRepeat(NO_RESTRICTION);
        }
    }

    public String buildCharacterRules() {
        StringBuilder sb = new StringBuilder();
        sb.append(getMinimumLength() + "M");
        sb.append(":");
        sb.append(getMinimumCharTypes());
        return sb.toString();
    }

    /**
     * Build minimum char type from the transient fields of each rule.
     * @return
     */
    public String buildMinimumCharTypes() {
        StringBuilder sb = new StringBuilder();
        if(getMinCapitalLetter() != null && getMinCapitalLetter() > 0) {
            sb.append(getMinCapitalLetter() + "C");
        } else {
            sb.append("0C");
        }
        sb.append(":");
        if(getMinNumbers() != null && getMinNumbers() > 0) {
            sb.append(getMinNumbers() + "N");
        } else {
            sb.append("0N");
        }
        sb.append(":");
        if(getMinSpecialChar() != null && getMinSpecialChar() > 0) {
            sb.append(getMinSpecialChar() + "S");
        } else {
            sb.append("0S");
        }
        return sb.toString();
    }
}
