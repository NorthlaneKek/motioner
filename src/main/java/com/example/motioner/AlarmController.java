package com.example.motioner;

import com.example.motioner.domain.entity.Alarm;
import com.example.motioner.infrastructure.AlarmRepository;
import com.example.motioner.presentation.response.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
public class AlarmController {

    private final AlarmRepository repository;

    @Autowired
    public AlarmController(AlarmRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/alarms")
    public List<Alarm> index() {
        return repository.findAll();
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
            repository.deleteById(UUID.fromString(id));
            return new Response("Successfully deleted");
        } catch (EmptyResultDataAccessException e) {
            return new Response("Alarm with this ID is not presented in DB");
        }
    }
}
