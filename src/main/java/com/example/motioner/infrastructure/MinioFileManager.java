package com.example.motioner.infrastructure;

import io.minio.GetObjectArgs;
import io.minio.MinioClient;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

//todo: file converter to mp4?
public class MinioFileManager implements FileManager {

    private MinioClient client;

    public MinioFileManager(MinioClient mc) {
        client = mc;
    }

    public void getFile(String filename) throws Exception {
        try (InputStream is = client.getObject(
                GetObjectArgs.builder()
                    .bucket("raspberrycamera")
                    .object(filename)
                    .build())) {
            byte[] buf = new byte[16384];
            int bytesRead;
            while ((bytesRead = is.read(buf, 0, buf.length)) >= 0) {
                System.out.println(new String(buf, 0, bytesRead, StandardCharsets.UTF_8));
            }
            System.out.println("Readable bytes count : " + is.available());
        }
    }

}
