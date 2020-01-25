package com.gamify.elearning.repository;

import com.gamify.elearning.entity.UserSettings;
import com.ideyatech.opentides.core.repository.BaseEntityRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserSettingsRepository extends BaseEntityRepository<UserSettings, String> {

    @Query(value="select usersettings from UserSettings usersettings where usersettings.user.id = :id")
    UserSettings findSettingsByUser(@Param("id") String userId);

}
