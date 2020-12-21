package com.example.motioner.infrastructure;

import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

//todo: file converter to mp4?
public class MinioFileManager implements FileManager {

    private MinioClient client;

    private Logger logger = LoggerFactory.getLogger(MinioFileManager.class);

    public MinioFileManager(MinioClient mc) {
        client = mc;
    }

    public ResponseEntity<byte[]> getFile(String filename, String fileType, String range) throws Exception {
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
                        .bucket("raspberrycamera")
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

}
