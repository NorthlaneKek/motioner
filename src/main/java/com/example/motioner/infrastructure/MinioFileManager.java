package com.example.motioner.infrastructure;

import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import io.minio.RemoveObjectsArgs;
import io.minio.Result;
import io.minio.messages.DeleteError;
import io.minio.messages.DeleteObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public class MinioFileManager implements FileManager {

    @Value("${minio.bucket}")
    private String bucket;

    private final MinioClient client;

    private int filesize;

    private final Logger logger = LoggerFactory.getLogger(MinioFileManager.class);

    private final String VIDEO = "/video";

    public MinioFileManager(MinioClient mc) {
        client = mc;
    }

    public ResponseEntity<byte[]> getFile(String filename, String fileType, String range) {
        long start = 0;
        long end;
        filesize = 0;
        byte[] data;
        try {
            if (range == null) {
                data = readFile(filename, 0, -1);
                return ResponseEntity.status(HttpStatus.OK)
                        .header("Content-type", "video/" + fileType)
                        .header("Content-Length", String.valueOf(data.length))
                        .header("Accept-Ranges", "bytes")

                        .header("X-Cache", "Hit from cloudfront")
                        .header("Via", "1.1 6da67a85460a493ba4aab4d94239d022.cloudfront.net (CloudFront)")
                        .header("X-Amz-Cf-Pop", "HEL50-C1")
                        .header("X-Amz-Cf-Id", "JZJ8X_Gy8wHtngEr-WHg8M0AC8lAS6WQ_JNVYkTrZQQIl-aLUbhYvg==")
                        .header("Age", "46569")
                        .header("ETag", "\"d2517ca1a5d4feee5dc3732cddb71949\"")
                        .header("Last-Modified", "Tue, 04 Sep 2018 18:39:18 GMT")
                        .body(data);
            } else {
                long[] ranges = parseRanges(range);
                start = ranges[0];
                end = ranges[1];
                data = readFile(filename, start, end);
                long rangeEnd = end;
                if (end == -1) {
                    rangeEnd = filesize - 1;
                }
                return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT)
                        .header("Content-Type", "video/" + fileType)
                        .header("Accept-Ranges", "bytes")
                        .header("Content-Length", String.valueOf(data.length))
                        .header("Content-Range", "bytes" + " " + start + "-" + rangeEnd + "/" + filesize)
                        .body(data);
            }

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
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

    private byte[] readFile(String filename, long start, long end) throws Exception {
        try (InputStream is = client.getObject(
                GetObjectArgs.builder()
                        .bucket(bucket)
                        .object(filename)
                        .build())) {
            ByteArrayOutputStream bufferedOutputStream = new ByteArrayOutputStream();
            byte[] data = new byte[1024];
            int nRead;
            while ((nRead = is.read(data, 0, data.length)) != -1) {
                bufferedOutputStream.write(data, 0, nRead);
                filesize += nRead;
            }

            int resultLength;
            if (filesize < end || end == -1) {
                resultLength = filesize - (int) start;
            } else {
                resultLength = (int) (end - start) + 1;
            }
            bufferedOutputStream.flush();
            byte[] result = new byte[resultLength];
            System.arraycopy(bufferedOutputStream.toByteArray(), (int) start, result, 0, result.length);
            return result;
        }
    }

    public void removeFile(String filename) {
        List<DeleteObject> objects = new LinkedList<>();
        objects.add(new DeleteObject(filename));
        Iterable<Result<DeleteError>> results =
                client.removeObjects(
                        RemoveObjectsArgs.builder().bucket(bucket).objects(objects).build());
        try {
            for (Result<DeleteError> result : results) {
                DeleteError error = result.get();
                System.out.println(
                        "Error in deleting object " + error.objectName() + "; " + error.message());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
