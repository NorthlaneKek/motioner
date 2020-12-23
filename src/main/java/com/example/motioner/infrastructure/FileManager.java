package com.example.motioner.infrastructure;

import org.springframework.http.ResponseEntity;

public interface FileManager {

    public ResponseEntity<byte[]> getFile(String s, String filename, String range);

    public void removeFile(String filename);
}
