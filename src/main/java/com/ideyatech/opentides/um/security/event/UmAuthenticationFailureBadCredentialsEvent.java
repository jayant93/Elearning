package com.ideyatech.opentides.um.security.event;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

/**
 * Created by Gino on 9/5/2016.
 */
public class UmAuthenticationFailureBadCredentialsEvent extends AbstractUMAuthenticationFailureEvent {

    public UmAuthenticationFailureBadCredentialsEvent(
            Authentication authentication, String appSecret, AuthenticationException exception) {
        super(authentication, appSecret, exception);
    }
}
