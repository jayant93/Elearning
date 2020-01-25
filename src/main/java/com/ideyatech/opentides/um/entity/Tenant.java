package com.ideyatech.opentides.um.entity;

import javax.persistence.*;
import java.util.Date;
import java.util.Set;

/**
 * Created by Gino on 8/30/2016.
 */
@Entity
@Table(name = "TENANT")
public class Tenant extends BaseUMEntity {

    @Column(name="COMPANY")
    private String company;

    /**
     * The owner of this tenant
     */
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "OWNER_ID", nullable = false)
    private BaseUser owner;

    @OneToMany(mappedBy = "tenant")
    private Set<BaseUser> users;

    @Column(name="_SCHEMA")
    private String schema;

    @Column(name="DB_VERSION")
    private Long dbVersion;

    @ManyToOne
    @JoinColumn(name = "ACCOUNT_TYPE_ID")
    private AccountType accountType;

    @Column(name = "EXPIRATION_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date expirationDate;

    @Transient
    private transient String name;

    @Transient
    private transient String template;

    public BaseUser getOwner() {
        return owner;
    }

    public void setOwner(BaseUser owner) {
        this.owner = owner;
    }

    public Set<BaseUser> getUsers() {
        return users;
    }

    public void setUsers(Set<BaseUser> users) {
        this.users = users;
    }

    /**
     * @return the schema
     */
    public final String getSchema() {
        return schema;
    }

    /**
     * @param schema the schema to set
     */
    public final void setSchema(final String schema) {
        this.schema = schema;
    }

    /**
     * @return the dbVersion
     */
    public final Long getDbVersion() {
        return dbVersion;
    }

    /**
     * @param dbVersion the dbVersion to set
     */
    public final void setDbVersion(final Long dbVersion) {
        this.dbVersion = dbVersion;
    }

    /**
     * @return the accountType
     */
    public final AccountType getAccountType() {
        return accountType;
    }

    /**
     * Returns the display of account type.
     * @return
     */
    public final String getAccountTypeDisplay() {
        if (accountType == null)
            return "";
        else
            return accountType.getName();
    }

    /**
     * @param accountType the accountType to set
     */
    public final void setAccountType(final AccountType accountType) {
        this.accountType = accountType;
    }

    /**
     * @return the expirationDate
     */
    public final Date getExpirationDate() {
        return expirationDate;
    }

    /**
     * @param expirationDate the expirationDate to set
     */
    public final void setExpirationDate(final Date expirationDate) {
        this.expirationDate = expirationDate;
    }

    /**
     * @return the company
     */
    public final String getCompany() {
        return company;
    }

    /**
     * @param company the company to set
     */
    public final void setCompany(final String company) {
        this.company = company;
    }

    /**
     * @return the name
     */
    public final String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public final void setName(final String name) {
        this.name = name;
    }

    /**
     * @return the template
     */
    public String getTemplate() {
        return template;
    }

    /**
     * @param template the template to set
     */
    public void setTemplate(String template) {
        this.template = template;
    }

    // payment details go here as well.

}
