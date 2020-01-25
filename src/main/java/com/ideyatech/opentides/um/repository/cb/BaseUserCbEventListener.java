package com.ideyatech.opentides.um.repository.cb;

import com.ideyatech.opentides.um.entity.BaseUser;
import com.ideyatech.opentides.um.entity.BaseUserCustomValue;
import com.ideyatech.opentides.um.entity.UserGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.core.annotation.HandleBeforeSave;
import org.springframework.data.rest.core.annotation.RepositoryEventHandler;

/**
 * Created by Gino on 10/11/2016.
 */
@RepositoryEventHandler
public class BaseUserCbEventListener  {

    @Autowired
    private UserGroupCbRepository userGroupRepository;

    @HandleBeforeSave
    public void onBeforeSave(BaseUser baseUser) {
        if(baseUser.getUserGroupIds() != null) {
            for(String id : baseUser.getUserGroupIds()) {
                UserGroup userGroup = userGroupRepository.findOne(id);
                baseUser.addGroup(userGroup);
            }
        }
        if(baseUser.getCustomValuesMap() != null) {
            for(String key : baseUser.getCustomValuesMap().keySet()) {
                BaseUserCustomValue customValues = baseUser.getCustomValuesMap().get(key);
                customValues.setCustomFieldKey(key);
            }
        }
    }
}
