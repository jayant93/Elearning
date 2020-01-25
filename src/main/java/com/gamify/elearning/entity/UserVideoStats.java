package com.gamify.elearning.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import com.ideyatech.opentides.core.entity.BaseEntity;
import com.ideyatech.opentides.um.entity.BaseUser;

@Entity
@Table(name="USER_VIDEO_STATS")
public class UserVideoStats extends BaseEntity {

	private static final long serialVersionUID = -1367578770648563251L;

	@ManyToOne
    @JoinColumn(name = "USER_ID")
    private BaseUser user;

    @ManyToOne
    @JoinColumn(name = "VIDEO_ID")
    private Video video;
    
    @Column(name = "DATE_LAST_VIEWED")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateLastViewed;
    
    @Column(name = "LAST_PLAYBACK_TIME")
    private String lastPlaybackTime;
    
    @Column(name = "VIEW_COUNT")
    private int viewCount;
    
    @Column(name = "LAST_PLAYED_STATUS")
    private String lastPlayedStatus;
    
    @Column(name = "CAN_VIEW_VIDEO", columnDefinition = "bit(1) DEFAULT true")
    private Boolean canViewVideo;

    @Transient
    private String videoId;
    
    @Transient
    private Long percentViewed;
    
	public BaseUser getUser() {
		return user;
	}

	public void setUser(BaseUser user) {
		this.user = user;
	}

	public Video getVideo() {
		return video;
	}

	public void setVideo(Video video) {
		this.video = video;
	}

	public Date getDateLastViewed() {
		return dateLastViewed;
	}

	public void setDateLastViewed(Date dateLastViewed) {
		this.dateLastViewed = dateLastViewed;
	}

	public String getLastPlaybackTime() {
		return lastPlaybackTime;
	}

	public void setLastPlaybackTime(String lastPlaybackTime) {
		this.lastPlaybackTime = lastPlaybackTime;
	}

	public int getViewCount() {
		return viewCount;
	}

	public void setViewCount(int viewCount) {
		this.viewCount = viewCount;
	}

	public String getLastPlayedStatus() {
		return lastPlayedStatus;
	}

	public void setLastPlayedStatus(String lastPlayedStatus) {
		this.lastPlayedStatus = lastPlayedStatus;
	}

	public Boolean getCanViewVideo() {
		return canViewVideo;
	}

	public void setCanViewVideo(Boolean canViewVideo) {
		this.canViewVideo = canViewVideo;
	}

	public String getVideoId() {
		if(video != null) {
			return video.getId();
		}
		return videoId;
	}

	public void setVideoId(String videoId) {
		this.videoId = videoId;
	}

	public Long getPercentViewed() {
		return percentViewed;
	}

	public void setPercentViewed(long percentViewed) {
		this.percentViewed = percentViewed;
	}

}
