package com.example.motioner.domain.entity;

public class Video {

    private byte[] data;

    private int size;

    private boolean isFull;

    private int originalSize;

    public Video(byte[] data) {
        this.data = data;
        this.size = data.length;
        this.isFull = true;
        this.originalSize = data.length;
    }

    public Video(byte[] data, boolean isFull, int originalSize) {
        this.data = data;
        this.size = data.length;
        this.isFull = isFull;
        this.originalSize = originalSize;
    }

    public void full(boolean isFull) {
        this.isFull = isFull;
    }

    public int originalSize() {
        return originalSize;
    }

    public boolean isFull() {
        return this.isFull;
    }

    public byte[] asArray() {
        return data;
    }

    public boolean shorterThan(long point) {
        return this.size < point;
    }

    public int getSize() {
        return size;
    }
}
