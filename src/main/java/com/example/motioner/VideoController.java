package com.example.motioner;

import com.example.motioner.infrastructure.FileManager;
import com.example.motioner.infrastructure.MinioFileManager;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/video")
public class VideoController {

    private FileManager fm;

    public VideoController(MinioFileManager manager) {
        fm = manager;
    }

    @GetMapping("/stream/{filename}/{filetype}")
    public Mono<ResponseEntity<byte[]>> streamVideo(@RequestHeader(value = "Range", required = false) String httpRangeList,
                                                    @PathVariable("filename") String filename,
                                                    @PathVariable("filetype") String fileType) {
        return Mono.just(fm.getFile(filename, fileType, httpRangeList));
    }

//    @GetMapping("/stream/disc/{filename}/{filetype}")
//    public Mono<ResponseEntity<byte[]>> streamVideoFromDisk(@RequestHeader(value = "Range", required = false) String httpRangeList,
//                                                    @PathVariable("filename") String filename,
//                                                    @PathVariable("filetype") String fileType) {
//        return Mono.just(fm.getFileFromDisc(filename, fileType, httpRangeList));
//    }
}
