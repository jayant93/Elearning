package com.ideyatech.opentides.um.security.event;

import org.springframework.security.core.Authentication;

/**
 * UmAuthenticationSuccessEvent for opentides 4.
 *
 * Created by Gino on 9/5/2016.
 */
public class UmAuthenticationSuccessEvent extends AbstractUMAuthenticationEvent {

    public UmAuthenticationSuccessEvent(Authentication authentication, String appSecret) {
        super(authentication, appSecret);
    }

}
