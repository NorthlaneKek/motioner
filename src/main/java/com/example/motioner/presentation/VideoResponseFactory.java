package com.example.motioner.presentation;

import com.example.motioner.domain.entity.Video;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class VideoResponseFactory {

    private final String contentType = "video/mp4";

    private final String CONTENT_TYPE = "Content-Type";

    private final String ACCEPT_RANGES = "Accept-Ranges";

    private final String CONTENT_LENGTH = "Content-length";

    private final String CONTENT_RANGE = "Content-Range";

//    public ResponseEntity<byte[]> toPartialResponse(byte[] video, String stringRanges, int fileSize) {
//        long[] ranges = parseRanges(stringRanges);
//        long start = ranges[0];
//        long end = ranges[1];
//        long rangeEnd = end;
//        if (end == -1) {
//            rangeEnd = fileSize - 1;
//        }
//
//        return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT)
//                .header(CONTENT_TYPE, contentType)
//                .header(ACCEPT_RANGES, "bytes")
//                .header(CONTENT_LENGTH, String.valueOf(video.length))
//                .header(CONTENT_RANGE, "bytes" + " " + start + "-" + rangeEnd + "/" + fileSize)
//                .body(video);
//    }

    private ResponseEntity<byte[]> toPartialResponse(Video video, String stringRanges) {
        long[] ranges = parseRanges(stringRanges);
        long start = ranges[0];
        long end = ranges[1];
        long rangeEnd = end;
        if (end == -1) {
            rangeEnd = video.originalSize() - 1;
        }

        return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT)
                .header(CONTENT_TYPE, contentType)
                .header(ACCEPT_RANGES, "bytes")
                .header(CONTENT_LENGTH, String.valueOf(video.getSize()))
                .header(CONTENT_RANGE, "bytes" + " " + start + "-" + rangeEnd + "/" + video.originalSize())
                .body(video.asArray());
    }

    private long[] parseRanges(String stringRanges) {
        String[] ranges = stringRanges.split("-");
        long start = Long.parseLong(ranges[0].substring(6));
        long end;
        if (ranges.length > 1) {
            end = Long.parseLong(ranges[1]);
        } else {
            end = -1;
        }
        return new long[] {start, end};
    }

    public ResponseEntity<byte[]> toResponse(Video video, String ranges) {
        if (video.isFull()) {
            return toFullResponse(video.asArray());
        } else {
            return toPartialResponse(video, ranges);
        }
    }

    private ResponseEntity<byte[]> toFullResponse(byte[] video) {
        return ResponseEntity.status(HttpStatus.OK)
                .header(CONTENT_TYPE, contentType)
                .header(CONTENT_LENGTH, String.valueOf(video.length))
                .header(ACCEPT_RANGES, "bytes")
                .body(video);
    }
}
