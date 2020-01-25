package com.ideyatech.opentides.um.security.event;

import org.springframework.security.authentication.event.AbstractAuthenticationEvent;
import org.springframework.security.core.Authentication;

/**
 * Created by Gino on 9/5/2016.
 */
public abstract class AbstractUMAuthenticationEvent extends AbstractAuthenticationEvent {

    protected String appSecret;

    public AbstractUMAuthenticationEvent(Authentication authentication, String appSecret) {
        super(authentication);
        this.appSecret = appSecret;
    }

    public String getAppSecret() {
        return appSecret;
    }

}
