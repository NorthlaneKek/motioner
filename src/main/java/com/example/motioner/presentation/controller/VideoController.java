package com.example.motioner.presentation.controller;

import com.example.motioner.domain.entity.Alarm;
import com.example.motioner.domain.entity.Video;
import com.example.motioner.domain.valueObject.VideoRange;
import com.example.motioner.infrastructure.AlarmRepository;
import com.example.motioner.infrastructure.FileManager;
import com.example.motioner.infrastructure.MinioFileManager;
import com.example.motioner.presentation.VideoResponseFactory;
import com.example.motioner.presentation.response.Response;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.Optional;

@RestController
@RequestMapping("/video")
public class VideoController {

    private FileManager fm;

    private AlarmRepository repository;

    private VideoResponseFactory rf;

    public VideoController(MinioFileManager manager, AlarmRepository repo, VideoResponseFactory rf) {
        fm = manager;
        repository = repo;
        this.rf = rf;
    }

//    @GetMapping("/stream/{filename}/{filetype}")
//    public Mono<ResponseEntity<byte[]>> streamVideo(@RequestHeader(value = "Range", required = false) String httpRangeList,
//                                                    @PathVariable("filename") String filename,
//                                                    @PathVariable("filetype") String fileType) throws Exception {
//        ResponseEntity<byte[]> videoResponse = fm.getFile(filename, fileType, httpRangeList);
//        System.out.println("Range: " + httpRangeList);
//        Video video = fm.getVideo(filename, VideoRange.of(httpRangeList));
//        VideoResponseFactory factory = new VideoResponseFactory();
//        ResponseEntity<byte[]> response = factory.toResponse(video, httpRangeList);
//        Optional<Alarm> stored = repository.findAlarmByFilename(filename);
//        if (stored.isPresent()) {
//            Alarm alarm = stored.get();
//            alarm.seen();
//            repository.saveAndFlush(alarm);
//        }
//        System.out.println("Old : " + videoResponse.getHeaders().toString());
//        return Mono.just(videoResponse);
//    }

    @GetMapping("/stream/{filename}/{filetype}")
    public Mono<ResponseEntity<byte[]>> streamVideo(@RequestHeader(value = "Range", required = false) String httpRangeList,
                                                    @PathVariable("filename") String filename,
                                                    @PathVariable("filetype") String fileType) throws Exception {
        Video video = fm.getVideo(filename, VideoRange.of(httpRangeList));
        ResponseEntity<byte[]> response = rf.toResponse(video, httpRangeList);
        Optional<Alarm> stored = repository.findAlarmByFilename(filename);
        if (stored.isPresent()) {
            Alarm alarm = stored.get();
            alarm.seen();
            repository.saveAndFlush(alarm);
        }
        return Mono.just(response);
    }


}
