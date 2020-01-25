package com.ideyatech.opentides.um.repository.cb;

import com.ideyatech.opentides.um.entity.BaseUser;
import com.ideyatech.opentides.um.entity.UserAuthority;
import com.ideyatech.opentides.um.entity.UserCredential;
import com.ideyatech.opentides.um.entity.UserGroup;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

/**
 * Created by Gino on 10/4/2016.
 */
@Ignore
public class SyncGatewayRepositoryEventListenerTest {

    @Test
    public void testAfterUserRegistration() {
        RestTemplate restTemplate = new RestTemplate();
        String sgAdminUrl = "http://localhost:4985";
        String sgDbName = "grocery-sync";

        SyncGatewayRepositoryEventListener listener = new SyncGatewayRepositoryEventListener();
        ReflectionTestUtils.setField(listener, "restTemplate", restTemplate);
        ReflectionTestUtils.setField(listener, "sgAdminUrl", sgAdminUrl);
        ReflectionTestUtils.setField(listener, "sgDbName", sgDbName);

        BaseUser baseUser = createBaseUser();
        listener.afterUserRegistration(baseUser);

    }

    private BaseUser createBaseUser() {
        BaseUser baseUser = new BaseUser();

        UserGroup ug1 = new UserGroup();
        ug1.setName("Administrator");
        ug1.setDescription("Administrator");
        ug1.addAuthority(new UserAuthority(ug1, "MANAGE_USER"));
        ug1.addAuthority(new UserAuthority(ug1, "MANAGE_ORGANIZATION"));
        ug1.addAuthority(new UserAuthority(ug1, "MANAGE_USERGROUP"));
        ug1.addAuthority(new UserAuthority(ug1, "MANAGE_SYSTEM_CODES"));
        baseUser.addGroup(ug1);

        UserCredential uc = new UserCredential();
        uc.setUsername("gino");
        uc.setNewPassword("password");
        baseUser.setCredential(uc);

        baseUser.setEmailAddress("gino@ideyatech.com");

        return baseUser;
    }

}
