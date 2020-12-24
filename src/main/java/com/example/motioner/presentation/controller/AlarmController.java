`package com.example.motioner.presentation.controller;

import com.example.motioner.domain.entity.Alarm;
import com.example.motioner.infrastructure.AlarmRepository;
import com.example.motioner.infrastructure.FileManager;
import com.example.motioner.presentation.response.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
public class AlarmController {

    private final AlarmRepository repository;

    private final FileManager manager;

    @Autowired
    public AlarmController(AlarmRepository repository, FileManager manager) {
        this.repository = repository;
        this.manager = manager;
    }

    @GetMapping("/alarms")
    public List<Alarm> index() {
        return repository.findAllByOrderByTimestampDesc();
    }

    @PostMapping("/alarms/create")
    public Alarm create(@RequestBody Alarm alarm) {
        Optional<Alarm> savedAlarm = repository.findById(alarm.getId());
        return savedAlarm.orElseGet(() -> repository.saveAndFlush(alarm));
    }

    @PutMapping("alarms/{id}")
    public Alarm replace(@RequestBody Alarm alarm, @PathVariable String id) {
        return repository.saveAndFlush(alarm);
    }

    @DeleteMapping("/alarms/{id}")
    public Response delete(@PathVariable String id) {
        try {
            Optional<Alarm> stored = repository.findById(UUID.fromString(id));
            if (stored.isPresent()) {
                manager.removeFile(stored.get().getFilename());
                repository.deleteById(UUID.fromString(id));
                return new Response("Successfully deleted");
            }
            return new Response("Nothing to delete");
        } catch (Exception e) {
            return new Response("Something went wrong");
        }
    }
}
