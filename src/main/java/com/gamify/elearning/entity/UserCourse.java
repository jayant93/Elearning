package com.gamify.elearning.entity;

import java.util.Date;

import javax.persistence.*;

import com.ideyatech.opentides.core.entity.BaseEntity;

@Entity
@Table(name="USER_COURSE")
public class UserCourse extends BaseEntity {

    private static final long serialVersionUID = -3288698111595431927L;

    @ManyToOne
    @JoinColumn(name = "USER_ID")
    private ELearningUser user;
   
    @OneToOne
    private Course course;

    @Column(name = "DATE_TAKEN")
    private Date dateTaken;

    public ELearningUser getUser() {
        return user;
    }

    public void setUser(ELearningUser user) {
        this.user = user;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public Date getDateTaken() {
        return dateTaken;
    }

    public void setDateTaken(Date dateTaken) {
        this.dateTaken = dateTaken;
    }
}