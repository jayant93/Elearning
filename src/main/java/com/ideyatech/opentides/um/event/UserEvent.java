package com.ideyatech.opentides.um.event;

import org.springframework.context.ApplicationEvent;

/**
 * Created by Gino on 10/7/2016.
 */
public abstract class UserEvent extends ApplicationEvent {

    /**
     * Create a new ApplicationEvent.
     *
     * @param source the object on which the event initially occurred (never {@code null})
     */
    public UserEvent(Object source) {
        super(source);
    }

}
