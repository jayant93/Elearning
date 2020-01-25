package com.ideyatech.opentides.um.event;

import com.ideyatech.opentides.um.entity.BaseUser;

/**
 * Created by Gino on 10/7/2016.
 */
public class ChangePasswordEvent extends UserEvent {

    private String newPassword;

    /**
     * Create a new ApplicationEvent.
     *
     * @param source the object on which the event initially occurred (never {@code null})
     */
    public ChangePasswordEvent(Object source, String newPassword) {
        super(source);
        this.newPassword = newPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }
}
