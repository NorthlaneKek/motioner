package com.example.motioner.domain.entity;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "alarms")
public class Alarm {

    @Id
    private UUID id;

    private String place;

    private String type;

    @Column(nullable = false)
    private String filename;

    @Column(nullable = false)
    private Long timestamp;

    @ManyToOne
    @JoinColumn(name = "device_id", nullable = false)
    private Device device;

    public Alarm() {}

    public Alarm(UUID uuid, String place, String filename, String type, Device device, Long timestamp) {
        this.id = uuid;
        this.place = place;
        this.filename = filename;
        this.type = type;
        this.device = device;
        this.timestamp = timestamp;
    }

    public String getId() {
        return id.toString();
    }

    public String getPlace() {
        return place;
    }

    public String getFilename() {
        return filename;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public Device getDevice() {
        return device;
    }

    @Override
    public String toString() {
        return id + place + filename + timestamp;
    }
}
