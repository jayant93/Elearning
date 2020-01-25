package com.gamify.elearning.repository;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.gamify.elearning.entity.ELearningUser;
import com.ideyatech.opentides.core.repository.BaseEntityRepository;

import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.Query;

public interface ELearningUserRepository extends BaseEntityRepository<ELearningUser, String> {

    @Query(value="select count(user) as count, year(user.createDate) as year, month(user.createDate) as month from ELearningUser user " +
                "where user.createDate between :startDate and :endDate " +
                "group by year(user.createDate), month(user.createDate) " +
                "order by year asc, month asc")
    List<Map<String, Object>> getUserCountPerMonth(@Param("startDate") Date start, @Param("endDate") Date end);

}
