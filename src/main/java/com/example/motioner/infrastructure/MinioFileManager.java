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
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;

public class MinioFileManager implements FileManager {

    @Value("${minio.bucket}")
    private String bucket;

    private final MinioClient client;

    private final Logger logger = LoggerFactory.getLogger(MinioFileManager.class);

    public MinioFileManager(MinioClient mc) {
        client = mc;
    }

    public ResponseEntity<byte[]> getFile(String filename, String fileType, String range) {
//        long end = Long.parseLong(range.split("-")[1]);
        try {
            byte[] data = readFile(filename);
            return ResponseEntity.status(HttpStatus.OK)
                    .header("Content-type", "video/" + fileType)
                    .header("Content-Length", String.valueOf(data.length - 1))
                    .body(data);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private byte[] readFile(String filename) throws Exception {
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
            }
            bufferedOutputStream.flush();
            byte[] result = new byte[bufferedOutputStream.size()];
            System.arraycopy(bufferedOutputStream.toByteArray(), 0, result, 0, result.length);
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
