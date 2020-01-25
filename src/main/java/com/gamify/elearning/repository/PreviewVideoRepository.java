package com.gamify.elearning.repository;

import com.gamify.elearning.entity.PreviewVideo;
import com.ideyatech.opentides.core.repository.BaseEntityRepository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface PreviewVideoRepository extends BaseEntityRepository<PreviewVideo, String> {

    @Query(value="select video from PreviewVideo video where video.vimeoId = :vimeoId")
    PreviewVideo findByVimeoId(@Param("vimeoId") String vimeoId);

    @Query(value="select video from PreviewVideo video where video.course.id = :courseId")
    PreviewVideo findByCourseId(@Param("courseId") String courseId);
}
