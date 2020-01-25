package com.ideyatech.opentides.um.repository.eventhandler;

import com.ideyatech.opentides.um.entity.UserAuthority;
import com.ideyatech.opentides.um.entity.UserGroup;
import com.ideyatech.opentides.um.repository.UserAuthorityRepository;
import com.ideyatech.opentides.um.repository.UserGroupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.core.annotation.HandleBeforeSave;
import org.springframework.data.rest.core.annotation.RepositoryEventHandler;
import org.springframework.stereotype.Component;

/**
 * Created by Gino on 9/15/2016.
 */
@Component
@RepositoryEventHandler
public class UserGroupEventHandler {

    @Autowired
    private UserGroupRepository userGroupRepository;

    @Autowired
    private UserAuthorityRepository userAuthorityRepository;

    @HandleBeforeSave
    public void onBeforeUpdate(UserGroup userGroup) {
        if(userGroup.getRemoveList() != null && !userGroup.getRemoveList().isEmpty()) {
            for (UserAuthority deleteRole : userGroup.getRemoveList()) {
                if(deleteRole.getId() != null)
                    userAuthorityRepository.delete(deleteRole.getId());
            }
        }
    }

}
