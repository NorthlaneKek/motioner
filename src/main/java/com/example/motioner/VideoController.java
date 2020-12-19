package com.example.motioner;

import com.example.motioner.infrastructure.FileManager;
import com.example.motioner.infrastructure.MinioFileManager;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class VideoController {

    private FileManager fm;

    public VideoController(MinioFileManager manager) {
        fm = manager;
    }

    @GetMapping("/video")
    public void test(@PathVariable String filename) {
        try {
            fm.getFile("f68fd18c-5a82-4b82-bc6b-88849a4fc038_alarm_19.12.2020 20:35:28.h264");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
