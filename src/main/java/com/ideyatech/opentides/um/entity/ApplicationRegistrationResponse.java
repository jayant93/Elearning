package com.ideyatech.opentides.um.entity;

import com.fasterxml.jackson.annotation.JsonCreator;

/**
 * Created by Gino on 8/30/2016.
 */
public class ApplicationRegistrationResponse {

    private String appName;

    private String appSecret;

    private String adminPassword;

    public ApplicationRegistrationResponse() {

    }

    public ApplicationRegistrationResponse(Application application) {
        this.appName = application.getName();
        this.appSecret = application.getAppSecret();
        this.adminPassword = application.getAdminPassword();
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getAppSecret() {
        return appSecret;
    }

    public void setAppSecret(String appSecret) {
        this.appSecret = appSecret;
    }

    public String getAdminPassword() {
        return adminPassword;
    }

    public void setAdminPassword(String adminPassword) {
        this.adminPassword = adminPassword;
    }
}
