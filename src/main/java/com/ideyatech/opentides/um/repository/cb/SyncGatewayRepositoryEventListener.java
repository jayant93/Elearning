package com.ideyatech.opentides.um.repository.cb;

import com.ideyatech.opentides.um.entity.BaseUser;
import com.ideyatech.opentides.um.entity.SyncGatewayUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.rest.core.annotation.HandleAfterCreate;
import org.springframework.data.rest.core.annotation.RepositoryEventHandler;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Gino on 10/4/2016.
 */
@RepositoryEventHandler
public class SyncGatewayRepositoryEventListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(SyncGatewayRepositoryEventListener.class);

    private RestTemplate restTemplate;

    @Value("${cb.sg.adminUrl}")
    private String sgAdminUrl;

    @Value("${cb.sg.dbName}")
    private String sgDbName;

    public SyncGatewayRepositoryEventListener() {
        this.restTemplate = new RestTemplate();
    }

    @HandleAfterCreate
    public void afterUserRegistration(BaseUser baseUser) {
        SyncGatewayUser sgUser = new SyncGatewayUser();
        sgUser.setEmail(baseUser.getEmailAddress());
        sgUser.setName(baseUser.getCredential().getUsername());
        sgUser.setPassword(baseUser.getCredential().getNewPassword());
        sgUser.setAdminRoles(new ArrayList<>(baseUser.getAuths()));
        sgUser.setAdminChannels(Arrays.asList(
                "com.ideyatech.opentides.um.entity.UserGroup",
                "SystemCodes"));

        String url = sgAdminUrl + "/" + sgDbName + "/_user/";
        LOGGER.debug("Connecting to url {}", url);

        HttpEntity<SyncGatewayUser> entity = new HttpEntity<>(sgUser);
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
        System.out.println(response.getBody());
    }

}
