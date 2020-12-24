package com.example.motioner.controller;

import com.example.motioner.domain.entity.Alarm;
import com.example.motioner.infrastructure.AlarmRepository;
import com.example.motioner.infrastructure.FileManager;
import com.example.motioner.infrastructure.MinioFileManager;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.Optional;

@RestController
@RequestMapping("/video")
public class VideoController {

    private FileManager fm;

    private AlarmRepository repository;

    public VideoController(MinioFileManager manager, AlarmRepository repo) {
        fm = manager;
        repository = repo;
    }

    @GetMapping("/stream/{filename}/{filetype}")
    public Mono<ResponseEntity<byte[]>> streamVideo(@RequestHeader(value = "Range", required = false) String httpRangeList,
                                                    @PathVariable("filename") String filename,
                                                    @PathVariable("filetype") String fileType) {
        ResponseEntity<byte[]> videoResponse = fm.getFile(filename, fileType, httpRangeList);
        Optional<Alarm> stored = repository.findAlarmByFilename(filename);
        if (stored.isPresent()) {
            Alarm alarm = stored.get();
            alarm.seen();
            repository.saveAndFlush(alarm);
        }
        return Mono.just(videoResponse);
    }

}
