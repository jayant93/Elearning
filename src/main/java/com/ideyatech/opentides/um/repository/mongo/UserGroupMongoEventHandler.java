package com.ideyatech.opentides.um.repository.mongo;

import com.ideyatech.opentides.um.entity.BaseUser;
import com.ideyatech.opentides.um.entity.UserGroup;
import com.ideyatech.opentides.um.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.core.annotation.HandleAfterCreate;
import org.springframework.data.rest.core.annotation.HandleBeforeDelete;
import org.springframework.data.rest.core.annotation.HandleBeforeSave;
import org.springframework.data.rest.core.annotation.RepositoryEventHandler;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by user on 30/01/2017.
 */
@Component
@RepositoryEventHandler
public class UserGroupMongoEventHandler {
    @Autowired
    private UserRepository userRepository;


    @HandleAfterCreate
    public void onBeforeCreate(UserGroup userGroup) {
        //Set new name as name
        userGroup.setName(userGroup.getNewName());
    }

    @HandleBeforeDelete
    public void onBeforeDelete(UserGroup userGroup){

    List<BaseUser> userList = ((UserMongoRepository)userRepository)
            .findByUsergroupId(userGroup.getNosqlId());

    String userGroupName = userGroup.getName();

    //Iterate all users in group
    for(BaseUser user : userList){
        List<UserGroup> groups =
                user.getGroups().stream()
                        .filter(group -> group.getName().contains(userGroupName))
                        .collect(Collectors.toList());
        //Remove all same user group
        groups.forEach(group -> {
            user.removeGroup(group);
        });
    }

    //Save All
    userRepository.save(userList);
    }

    @HandleBeforeSave
    public void onBeforeUpdate(UserGroup userGroup) {
        //Update user group added
        List<BaseUser> userList = userRepository
                .findByUsergroupName(userGroup.getName());

        String oldName = userGroup.getName();

        //Set new name as name
        userGroup.setName(userGroup.getNewName());

        //Iterate all users in group
        for(BaseUser user : userList){
            List<UserGroup> groups =
                    user.getGroups().stream()
                            .filter(group -> group.getName().contains(oldName))
                            .collect(Collectors.toList());

            //Remove all same user group
            groups.forEach(group -> {
                user.removeGroup(group);
            });

            //Add new group
            user.addGroup(userGroup);
        }

        //Save All
        userRepository.save(userList);
    }
}
