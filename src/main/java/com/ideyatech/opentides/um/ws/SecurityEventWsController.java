package com.ideyatech.opentides.um.ws;

import com.ideyatech.opentides.core.entity.webhook.SecurityEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

/**
 * Websocket controller for sending {@link com.ideyatech.opentides.core.entity.webhook.SecurityEvent} events
 *
 * @author Gino
 */
@Controller
public class SecurityEventWsController {

    private static Logger LOGGER = LoggerFactory.getLogger(SecurityEventWsController.class);

    /**
     * Sending of security event.
     *
     * @param event
     * @param appLink
     * @return
     */
    @MessageMapping(value = "/security-event/{appLink}")
    @SendTo(value = "/topic/security-event/{appLink}")
    public ResponseEntity<SecurityEvent> sendEvent(SecurityEvent event, @DestinationVariable String appLink) {
        LOGGER.debug("Sending event to {}", appLink);
        return ResponseEntity.ok(event);
    }

}
