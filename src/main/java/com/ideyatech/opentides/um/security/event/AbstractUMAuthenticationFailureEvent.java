package com.ideyatech.opentides.um.security.event;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.util.Assert;

/**
 * Created by Gino on 9/5/2016.
 */
public abstract class AbstractUMAuthenticationFailureEvent extends AbstractUMAuthenticationEvent {

    private final AuthenticationException exception;

    public AbstractUMAuthenticationFailureEvent(Authentication authentication, String appSecret, AuthenticationException exception) {
        super(authentication, appSecret);
        Assert.notNull(exception, "AuthenticationException is required");
        this.exception = exception;
    }

    public AuthenticationException getException() {
        return exception;
    }
}
