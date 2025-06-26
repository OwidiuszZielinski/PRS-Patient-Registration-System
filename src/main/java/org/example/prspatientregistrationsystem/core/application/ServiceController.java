package org.example.prspatientregistrationsystem.core.application;

import lombok.RequiredArgsConstructor;
import org.example.prspatientregistrationsystem.core.service.ServiceDto;
import org.example.prspatientregistrationsystem.core.service.ServiceService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/service/")
@CrossOrigin(origins = "http://localhost:3000")
public class ServiceController {

    private final ServiceService serviceService;

    @GetMapping
    public List<ServiceDto> getServices() {
        return serviceService.findAll();
    }

    @GetMapping(path = "{id}/")
    public ServiceDto getService(@PathVariable Long id) {
        return serviceService.findById(id);
    }
} 