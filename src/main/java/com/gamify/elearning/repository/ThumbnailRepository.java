package com.gamify.elearning.repository;

import java.util.List;

import com.gamify.elearning.entity.Thumbnail;
import com.ideyatech.opentides.core.repository.BaseEntityRepository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ThumbnailRepository extends BaseEntityRepository<Thumbnail, String> {

    @Query(value="select thumbnail from Thumbnail thumbnail where thumbnail.video.id = :videoId and thumbnail.thumbnailId = :thumbId")
    List<Thumbnail> findByVideoIdAndThumbnailId(@Param("videoId") String videoId, @Param("thumbId") String thumbId);
    
    @Query(value="select thumbnail from Thumbnail thumbnail where thumbnail.video.id = :videoId")
    List<Thumbnail> findByVideoId(@Param("videoId") String videoId);

    @Query(value="select thumbnail from Thumbnail thumbnail where thumbnail.video.vimeoId = :vimeoId")
    List<Thumbnail> findByVimeoId(@Param("vimeoId") String vimeoId);
}