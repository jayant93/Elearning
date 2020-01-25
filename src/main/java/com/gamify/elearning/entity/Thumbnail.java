package com.gamify.elearning.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.ideyatech.opentides.core.entity.BaseEntity;

@Entity
@Table(name="THUMBNAIL")
public class Thumbnail extends BaseEntity {

    private static final long serialVersionUID = -1475845518413582414L;

    @Column
    private String thumbnailId;

    @Column
    private String url;

    @Column
    private Boolean active;

    @Column
    private Boolean custom;

    @ManyToOne
    @JoinColumn(name = "VIDEO_ID")
    private Video video;

    @ManyToOne
    @JoinColumn(name = "PREVIEW_VIDEO_ID")
    private PreviewVideo previewVideo;

    public Video getVideo() {
        return video;
    }

    public void setVideo(Video video) {
        this.video = video;
    }

    public String getThumbnailId() {
        return thumbnailId;
    }

    public void setThumbnailId(String thumbnailId) {
        this.thumbnailId = thumbnailId;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public Boolean getCustom() {
        return custom;
    }

    public void setCustom(Boolean custom) {
        this.custom = custom;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public PreviewVideo getPreviewVideo() {
        return previewVideo;
    }

    public void setPreviewVideo(PreviewVideo previewVideo) {
        this.previewVideo = previewVideo;
    }
}