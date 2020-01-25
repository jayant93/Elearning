package com.ideyatech.opentides.um.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.text.NumberFormat;

/**
 * Created by Gino on 8/30/2016.
 */
@Entity
@Table(name = "ACCOUNT_TYPE")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AccountType extends BaseUMEntity {

    private static final NumberFormat format = NumberFormat
            .getCurrencyInstance();

    // duration of subscription, in days.
    public enum Period {
        MONTHLY,
        QUARTERLY,
        ANNUAL
    }

    // name of the account type
    @Column(name="NAME")
    private String name;

    // description of the account type
    @Column(name="DESCRIPTION")
    private String description;

    // amount of subscription
    @Column(name="AMOUNT")
    private Double amount;

    // duration of subscription
    @Column(name="PERIOD")
    private Period period;

    // is account type active for offering or not
    @Column(name="ACTIVE")
    private Boolean active;

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
     * @return the description
     */
    public final String getDescription() {
        return description;
    }

    /**
     * @param description the description to set
     */
    public final void setDescription(final String description) {
        this.description = description;
    }

    /**
     * @return the amount
     */
    public final Double getAmount() {
        return amount;
    }

    /**
     * @param amount the amount to set
     */
    public final void setAmount(final Double amount) {
        this.amount = amount;
    }

    /**
     * @return the period
     */
    public final Period getPeriod() {
        return period;
    }

    /**
     * @param period the period to set
     */
    public final void setPeriod(final Period period) {
        this.period = period;
    }

    /**
     * @return the active
     */
    public final Boolean getActive() {
        return active;
    }

    public final String getActiveDisplay() {
        if (active!=null && active) {
            return "Active";
        } else {
            return "Not Active";
        }
    }
    /**
     * @param active the active to set
     */
    public final void setActive(final Boolean active) {
        this.active = active;
    }

    /**
     * Returns the subscription details.
     */
    public final String getSubscription() {
        final StringBuilder subs = new StringBuilder();
        if (amount != null && amount > 0) {
            subs.append(format.format(amount));
        } else {
            subs.append("Free");
        }
        subs.append(" - ");
        if (period != null) {
            subs.append(period);
        }
        return subs.toString();
    }

}
