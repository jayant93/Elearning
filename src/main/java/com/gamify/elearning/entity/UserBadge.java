package com.gamify.elearning.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.ideyatech.opentides.core.entity.BaseEntity;

/**
 * @author marvin
 */
@Entity
@Table(name="USER_BADGE")
public class UserBadge extends BaseEntity {

    private static final long serialVersionUID = 1910405493120090845L;

    @OneToOne
    private Badge badge;
    
    @ManyToOne
    @JoinColumn(name = "USER_ID")
    private ELearningUser user;

    @Column(name="DATE_OBTAINED")
    private Date dateObtained;

    @OneToOne
    private Course course;

    public Badge getBadge() {
        return badge;
    }

    public void setBadge(Badge badge) {
        this.badge = badge;
    }

    public ELearningUser getUser() {
        return user;
    }

    public void setUser(ELearningUser user) {
        this.user = user;
    }

    public Date getDateObtained() {
        return dateObtained;
    }

    public void setDateObtained(Date dateObtained) {
        this.dateObtained = dateObtained;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }
}