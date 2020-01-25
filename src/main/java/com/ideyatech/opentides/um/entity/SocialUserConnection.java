package com.ideyatech.opentides.um.entity;

import com.ideyatech.opentides.core.entity.BaseEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;

/**
 * Created by Gino on 10/10/2016.
 */
@Entity
@Table(name = "UserConnection", indexes = {
        @Index(name = "01_social_uk", columnList = "userId,providerId,providerUserId"),
        @Index(name = "02_social_ucr_uk", columnList = "userId,providerId,rank")
})
public class SocialUserConnection extends BaseEntity {

    @Column(name = "userId")
    private String userId;

    @Column(name = "providerId")
    private String providerId;

    @Column(name = "providerUserId")
    private String providerUserId;

    @Column(name = "rank", nullable = false)
    private Integer rank;

    @Column(name = "displayName")
    private String displayName;

    @Column(name = "profileUrl", length = 512)
    private String profileUrl;

    @Column(name = "imageUrl", length = 512)
    private String imageUrl;

    @Column(name = "accessToken", nullable = false)
    private String accessToken;

    @Column(name = "secret")
    private String secret;

    @Column(name = "refreshToken")
    private String refreshToken;

    @Column(name = "expireTime")
    private Long expireTime;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getProviderId() {
        return providerId;
    }

    public void setProviderId(String providerId) {
        this.providerId = providerId;
    }

    public String getProviderUserId() {
        return providerUserId;
    }

    public void setProviderUserId(String providerUserId) {
        this.providerUserId = providerUserId;
    }

    public Integer getRank() {
        return rank;
    }

    public void setRank(Integer rank) {
        this.rank = rank;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getProfileUrl() {
        return profileUrl;
    }

    public void setProfileUrl(String profileUrl) {
        this.profileUrl = profileUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public Long getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(Long expireTime) {
        this.expireTime = expireTime;
    }
}
