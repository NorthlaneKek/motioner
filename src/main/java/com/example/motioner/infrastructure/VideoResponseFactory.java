package com.example.motioner.infrastructure;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class VideoResponseFactory {

    private final String contentType = "video/mp4";

    public ResponseEntity<byte[]> toPartialResponse(byte[] video, String stringRanges, int fileSize) {
        long[] ranges = parseRanges(stringRanges);
        long start = ranges[0];
        long end = ranges[1];
        long rangeEnd = end;
        if (end == -1) {
            rangeEnd = fileSize - 1;
        }

        return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT)
                .header("Content-Type", contentType)
                .header("Accept-Ranges", "bytes")
                .header("Content-Length", String.valueOf(video.length))
                .header("Content-Range", "bytes" + " " + start + "-" + rangeEnd + "/" + fileSize)
                .body(video);
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

    public ResponseEntity<byte[]> toFullResponse(byte[] video) {
        return ResponseEntity.status(HttpStatus.OK)
                .header("Content-type", contentType)
                .header("Content-Length", String.valueOf(video.length))
                .header("Accept-Ranges", "bytes")

//                .header("X-Cache", "Hit from cloudfront")
//                .header("Via", "1.1 6da67a85460a493ba4aab4d94239d022.cloudfront.net (CloudFront)")
//                .header("X-Amz-Cf-Pop", "HEL50-C1")
//                .header("X-Amz-Cf-Id", "JZJ8X_Gy8wHtngEr-WHg8M0AC8lAS6WQ_JNVYkTrZQQIl-aLUbhYvg==")
//                .header("Age", "46569")
//                .header("ETag", "\"d2517ca1a5d4feee5dc3732cddb71949\"")
//                .header("Last-Modified", "Tue, 04 Sep 2018 18:39:18 GMT")
                .body(video);
    }
}
