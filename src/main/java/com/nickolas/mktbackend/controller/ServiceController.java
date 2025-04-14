package com.nickolas.mktbackend.controller;


import com.nickolas.mktbackend.model.RepairService;
import com.nickolas.mktbackend.repository.RepairServiceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/services")
public class ServiceController {
    @Autowired
    private RepairServiceRepository serviceRepository;

    @GetMapping
    public List<RepairService> getAll() {
        return serviceRepository.findAll();
    }

    @PostMapping
    public RepairService create(@RequestBody RepairService service) {
        return serviceRepository.save(service);
    }

    @PutMapping("/{id}")
    public RepairService update(@PathVariable("id") Long id, @RequestBody RepairService updated) {
        RepairService service = serviceRepository.findById(id).orElseThrow();
        service.setTitle(updated.getTitle());
        service.setDescription(updated.getDescription());
        service.setPrice(updated.getPrice());
        return serviceRepository.save(service);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") Long id) {
        serviceRepository.deleteById(id);
    }
}
