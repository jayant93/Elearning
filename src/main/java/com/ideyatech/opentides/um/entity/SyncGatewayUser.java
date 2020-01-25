package com.ideyatech.opentides.um.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * @author Gino
 */
public class SyncGatewayUser {

    @JsonProperty(value = "admin_channels")
    private List<String> adminChannels;

    @JsonProperty(value = "admin_roles")
    private List<String> adminRoles;

    @JsonProperty(value = "all_channels")
    private List<String> allChannels;

    private String email;

    private String name;

    private String password;

    private List<String> roles;

    public List<String> getAdminChannels() {
        return adminChannels;
    }

    public void setAdminChannels(List<String> adminChannels) {
        this.adminChannels = adminChannels;
    }

    public List<String> getAdminRoles() {
        return adminRoles;
    }

    public void setAdminRoles(List<String> adminRoles) {
        this.adminRoles = adminRoles;
    }

    public List<String> getAllChannels() {
        return allChannels;
    }

    public void setAllChannels(List<String> allChannels) {
        this.allChannels = allChannels;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }
}
