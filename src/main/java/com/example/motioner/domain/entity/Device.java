package com.example.motioner.domain.entity;

import javax.persistence.*;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "devices")
public class Device {

    @Id
    private UUID id;

    private String name;

    @Column(nullable = false, updatable = false)
    private long createdAt;

    @Column(nullable = false)
    private Long lastConnect;

    @OneToMany(mappedBy = "device")
    private Set<Alarm> alarms;

    public Device() {}

    public Device(UUID id, String name, long createdAt, Long lastConnect) {
        this.id = id;
        this.name = name;
        this.createdAt = createdAt;
        this.lastConnect = lastConnect;
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public long createdAt() {
        return createdAt;
    }

    public Long lastConnect() {
        return lastConnect;
    }

    public void setLastConnect(long lastConnect) {
        this.lastConnect = lastConnect;
    }

    @Override
    public String toString() {
        return id.toString() + name + lastConnect;
    }
}
