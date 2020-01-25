package com.ideyatech.opentides.um.repository.cb;

import com.ideyatech.opentides.um.entity.BaseUser;
import com.ideyatech.opentides.um.entity.SyncGatewayUser;
import com.ideyatech.opentides.um.event.ChangePasswordEvent;
import com.ideyatech.opentides.um.event.UserEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

/**
 * Created by Gino on 10/7/2016.
 */
public class UserEventCbListener implements ApplicationListener<UserEvent> {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserEventCbListener.class);

    private RestTemplate restTemplate;

    @Value("${cb.sg.adminUrl}")
    private String sgAdminUrl;

    @Value("${cb.sg.dbName}")
    private String sgDbName;

    public UserEventCbListener() {
        restTemplate = new RestTemplate();
    }

    @Override
    public void onApplicationEvent(UserEvent event) {
        if(event instanceof ChangePasswordEvent) {
            BaseUser user = (BaseUser)event.getSource();
            ChangePasswordEvent changePasswordEvent = (ChangePasswordEvent)event;
            String url = sgAdminUrl + "/" + sgDbName + "/_user/" + user.getCredential().getUsername();
            LOGGER.debug("Connecting to url {}", url);

            SyncGatewayUser sgUser = new SyncGatewayUser();
            sgUser.setPassword(changePasswordEvent.getNewPassword());

            HttpEntity<SyncGatewayUser> entity = new HttpEntity<>(sgUser);
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.PUT, entity, String.class);
        }
    }

}
