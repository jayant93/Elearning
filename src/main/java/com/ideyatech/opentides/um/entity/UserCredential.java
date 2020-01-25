package com.ideyatech.opentides.um.entity;

import com.couchbase.client.java.repository.annotation.Field;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ideyatech.opentides.core.entity.BaseEntity;
import org.springframework.data.couchbase.core.mapping.Document;

import javax.persistence.*;

/**
 * Entity representing the User Credential. Contains the username and password of the user.
 *
 * @author allantan
 */
@Document
@Entity
@Table(name = "USERS")
public class UserCredential extends BaseUMEntity {

    @Field
    @Column(name = "USERNAME", unique = true)
    private String username;

    @Field
    @Column(name = "PASSWORD", nullable=false)
    private String password;

    @Field
    @Column(name = "TEMP_PASSWORD")
    private String tempPassword; // Will be used for storing temporary generated password when a user registered on his own

    private transient String newPassword;

    private transient String confirmPassword;

    @Field
    @Column(name = "ENABLED")
    private Boolean enabled;

    @Field
    @Column(name = "STATUS")
    private String status;

    @OneToOne
    @JoinColumn(name = "USERID", nullable = false)
    @JsonIgnore
    @org.springframework.data.annotation.Transient
    private BaseUser user;

    /**
     * @return the user
     */
    public BaseUser getUser() {
        return user;
    }
    /**
     * @param user the user to set
     */
    public void setUser(BaseUser user) {
        this.user = user;
    }
    public UserCredential() {
        enabled=true;
    }

    /**
     * Getter method for username.
     *
     * @return the username
     */
    public String getUsername() {
        return username;
    }
    /**
     * Setter method for username.
     *
     * @param username the username to set
     */
    public void setUsername(String username) {
        this.username = username;
    }
    /**
     * Getter method for newPassword.
     *
     * @return the newPassword
     */
    public String getNewPassword() {
        return newPassword;
    }
    /**
     * Setter method for newPassword.
     *
     * @param newPassword the newPassword to set
     */
    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
    /**
     * Getter method for confirmPassword.
     *
     * @return the confirmPassword
     */
    public String getConfirmPassword() {
        return confirmPassword;
    }
    /**
     * Setter method for confirmPassword.
     *
     * @param confirmPassword the confirmPassword to set
     */
    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }
    /**
     * Getter method for enabled.
     *
     * @return the enabled
     */
    public Boolean getEnabled() {
        return enabled;
    }
    /**
     * Setter method for enabled.
     *
     * @param enabled the enabled to set
     */
    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public String getPassword() {
        return password;
    }

    /**
     * Ensures that password is enrypted according to configured passwordEncoder.
     * @param password
     */
    public void setPassword(String password) {
        this.password = password;
    }

    public String getTempPassword() {
        return tempPassword;
    }

    public void setTempPassword(String tempPassword) {
        this.tempPassword = tempPassword;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
