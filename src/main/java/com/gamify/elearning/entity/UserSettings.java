package com.gamify.elearning.entity;

import com.ideyatech.opentides.core.entity.BaseEntity;

import javax.persistence.*;
@Entity
@Table(name="USER_SETTINGS")
public class UserSettings extends BaseEntity {

    @OneToOne
    @JoinColumn(name = "user_id", unique = true)
    private ELearningUser user;

    @Column
    private String themeName;

    public ELearningUser getUser() {
        return user;
    }

    public void setUser(ELearningUser user) {
        this.user = user;
    }

    public String getThemeName() {
        return themeName;
    }

    public void setThemeName(String themeName) {
        this.themeName = themeName;
    }
}
