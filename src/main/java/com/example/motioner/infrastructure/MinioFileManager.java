package com.example.motioner.infrastructure;

import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import io.minio.RemoveObjectsArgs;
import io.minio.Result;
import io.minio.messages.DeleteError;
import io.minio.messages.DeleteObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;

public class MinioFileManager implements FileManager {

    @Value("${minio.bucket}")
    private String bucket;

    private final MinioClient client;

    private final VideoResponseFactory rf;

    private int filesize;

    public MinioFileManager(MinioClient mc, VideoResponseFactory rf) {
        client = mc;
        this.rf = rf;
    }

    public ResponseEntity<byte[]> getFile(String filename, String fileType, String range) {
        filesize = 0;
        byte[] data;
        try {
            if (range == null) {
                data = readFile(filename, 0, -1);
                return rf.toFullResponse(data);
            } else {
                long[] ranges = parseRanges(range);
                long start = ranges[0];
                long end = ranges[1];
                data = readFile(filename, start, end);
                return rf.toPartialResponse(data, range, filesize);
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
