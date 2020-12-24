package com.example.motioner.infrastructure;

import com.example.motioner.domain.entity.Video;
import com.example.motioner.domain.valueObject.VideoRange;
import com.example.motioner.presentation.VideoResponseFactory;
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

    public MinioFileManager(MinioClient mc) {
        client = mc;
    }

    public Video getVideo(String filename, VideoRange range) throws Exception {
        byte[] data = readFile(filename);
        Video video = new Video(data);
        return slice(video, range);
    }

    private Video slice(Video video, VideoRange range) {
        if (range.wholeVideo()) {
            return video;
        }
        int finalSize;
        if (video.shorterThan(range.getEnd()) || range.withNoEnd()) {
            finalSize = video.getSize() - (int) range.getStart();
        } else {
            finalSize = (int) range.difference();
        }
        byte[] result = new byte[finalSize];
        System.arraycopy(video.asArray(), (int) range.getStart(), result, 0, result.length);
        return new Video(result, false, video.getSize());
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
            int resultLength = bufferedOutputStream.size();
            bufferedOutputStream.flush();
            byte[] result = new byte[resultLength];
            System.arraycopy(bufferedOutputStream.toByteArray(), (int) 0, result, 0, result.length);
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
