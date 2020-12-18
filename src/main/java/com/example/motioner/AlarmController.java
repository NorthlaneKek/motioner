package com.example.motioner;

import com.example.motioner.domain.entity.Alarm;
import com.example.motioner.infrastructure.AlarmRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class AlarmController {

    private final AlarmRepository repository;

    public AlarmController(AlarmRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/alarms")
    public List<Alarm> index() {
        return repository.findAll();
    }
}
