package com.ideyatech.opentides.um.repository.cb;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ideyatech.opentides.core.repository.BaseEntityRepository;
import com.ideyatech.opentides.core.repository.BaseEntityRepositoryHelper;
import com.ideyatech.opentides.um.entity.Application;
import com.ideyatech.opentides.um.entity.Authority;
import com.ideyatech.opentides.um.entity.UserGroup;
import com.ideyatech.opentides.um.repository.AuthorityRepository;
import com.ideyatech.opentides.um.repository.UserGroupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Gino on 8/25/2016.
 */
@RepositoryRestResource(path = "/usergroup")
public interface UserGroupCbRepository extends UserGroupRepository<String> {

    /**
     * Find UserGroup by name.
     *
     * @param name the name of the group
     * @return the UserGroup with the given name
     */
    UserGroup findByName(@Param("name") String name);

}

class UserGroupCbRepositoryCustomImpl {

    @Autowired
    private BaseEntityRepositoryHelper baseEntityRepositoryHelper;

    @Autowired
    private AuthorityRepository authorityRepository;

    public List<Authority> createDefaultAuthority(Application application) {
        ClassPathResource classPathResource =
                new ClassPathResource(File.separator + "data" + File.separator + "default_auths.json");

        ObjectMapper mapper = new ObjectMapper();
        List<Authority> auths = new ArrayList<>();
        try {
            InputStream inputStream = classPathResource.getInputStream();
            auths = mapper.readValue(inputStream,new TypeReference<List<Authority>>() {});

            for(Authority auth : auths) {
                auth.setApplication(application);
                authorityRepository.save(auth);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return auths;
    }

    public UserGroup setupAdminGroup(Application application) {
        UserGroup userGroup = new UserGroup();
        userGroup.setName("Administrator");
        userGroup.setDescription("System Administrators (Default)");
        userGroup.setIsDefault(Boolean.TRUE);

        List<Authority> auths = createDefaultAuthority(application);

        List<String> names = new ArrayList<String>();
        for (Authority auth : auths) {
            String key = auth.getKey();
            names.add(key);
        }
        userGroup.setAuthorityNames(names);
        baseEntityRepositoryHelper.saveEntityModel(userGroup);

        return userGroup;
    }
}