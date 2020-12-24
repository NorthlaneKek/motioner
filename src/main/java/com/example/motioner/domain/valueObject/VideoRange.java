package com.example.motioner.domain.valueObject;

import com.example.motioner.domain.entity.Video;

public class VideoRange {

    private final long start;

    private final long end;

    private final boolean nullableRange;

    public VideoRange(long start, long end, boolean nullableRange) {
        this.start = start;
        this.end = end;
        this.nullableRange = nullableRange;
    }

    public boolean wholeVideo() {
        return nullableRange;
    }

    public static VideoRange of(String stringRanges) {
        long start = 0L;
        long end = -1;
        boolean nullableRange = true;
        if (stringRanges != null) {
            nullableRange = false;
            String[] ranges = stringRanges.split("-");
            try {
                start = Long.parseLong(ranges[0].substring(6));
                if (ranges.length > 1) {
                    end = Long.parseLong(ranges[1]);
                }
            } catch (Exception e) {
                e.printStackTrace();
            };
        }
        return new VideoRange(start, end, nullableRange);
    }

    public long getStart() {
        return start;
    }

    public long getEnd() {
        return end;
    }

    public boolean withNoEnd() {
        return end == -1;
    }

    public long difference() {
        return end - start + 1;
    }
}
