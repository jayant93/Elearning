package com.gamify.elearning.service;

import org.springframework.web.multipart.MultipartFile;

public interface GameService {
    /**
     * Upload Game Files to S3
     * @param id
     * @param gameZip
     * @return
     */
    String uploadGame(String id, MultipartFile gameZip);

    /**
     * Upload Thumbnail of Game to S3
     * @param id
     * @param thumbnail
     */
    String uploadThumbnail(String id, MultipartFile thumbnail);
}
