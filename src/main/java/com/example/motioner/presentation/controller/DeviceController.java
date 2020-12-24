package com.example.motioner.presentation.controller;

import com.example.motioner.domain.entity.Device;
import com.example.motioner.infrastructure.DeviceRepository;
import com.example.motioner.presentation.response.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
public class DeviceController {

    private final DeviceRepository repository;

    @Autowired
    public DeviceController(DeviceRepository repo) {
        this.repository = repo;
    }

    @GetMapping("/devices")
    public List<Device> index() {
        return repository.findAll();
    }

    @PostMapping("/devices/create")
    public Device create(@RequestBody Device device) {
        Optional<Device> savedDevice = repository.findById(device.getId());
        return savedDevice.orElseGet(() -> repository.saveAndFlush(device));
    }

    @PutMapping("/devices/{id}")
    public Device replace(@RequestBody Device device, @PathVariable String id) {
        return repository.saveAndFlush(device);
    }

    @DeleteMapping("/devices/{id}")
    public Response delete(@PathVariable String id) {
        try {
            repository.deleteById(UUID.fromString(id));
            return new Response("Device successfully deleted");
        } catch (EmptyResultDataAccessException e) {
            return new Response("Device with this ID is not presented in DB");
        }
    }
}
