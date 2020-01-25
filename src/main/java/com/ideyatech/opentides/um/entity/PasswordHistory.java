package com.ideyatech.opentides.um.entity;

import com.ideyatech.opentides.core.entity.BaseEntity;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.*;

/**
 * Entity representing password history.
 *
 * @author Gino
 */
@Document
@Entity
@Table(name = "PASSWORD_HISTORY")
public class PasswordHistory extends BaseEntity {

    /**
     * The user that owns the password history
     */
    @ManyToOne
    @JoinColumn(name = "USER_ID")
    private BaseUser user;

    /**
     * Encoded password
     */
    @Column(name = "PASSWORD")
    private String password;


    /**
     * Gets The user that owns the password history.
     *
     * @return Value of The user that owns the password history.
     */
    public BaseUser getUser() {
        return user;
    }

    /**
     * Sets new The user that owns the password history.
     *
     * @param user New value of The user that owns the password history.
     */
    public void setUser(BaseUser user) {
        this.user = user;
    }

    /**
     * Gets Encoded password.
     *
     * @return Value of Encoded password.
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets new Encoded password.
     *
     * @param password New value of Encoded password.
     */
    public void setPassword(String password) {
        this.password = password;
    }
}
