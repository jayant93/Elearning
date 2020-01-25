package com.gamify.elearning.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.ideyatech.opentides.core.entity.BaseEntity;

@Entity
@Table(name="PREVIEW_VIDEO")
public class PreviewVideo extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Column
    private String vimeoId;

    @OneToOne
    @JoinColumn(name = "COURSE_ID")
    private Course course;

    @Column
    private String fileName;

    public String getVimeoId() {
        return vimeoId;
    }

    public void setVimeoId(String vimeoId) {
        this.vimeoId = vimeoId;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}