package com.example.motioner.infrastructure;

import com.example.motioner.domain.entity.Video;
import com.example.motioner.domain.valueObject.VideoRange;
import org.springframework.http.ResponseEntity;

public interface FileManager {

    public void removeFile(String filename);

    public Video getVideo(String filename, VideoRange of) throws Exception;
}
