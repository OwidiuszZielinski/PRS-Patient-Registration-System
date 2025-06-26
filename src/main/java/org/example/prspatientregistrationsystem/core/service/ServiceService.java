package org.example.prspatientregistrationsystem.core.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ServiceService {

    private final ServiceRepository serviceRepository;

    public List<ServiceDto> findAll() {
        return serviceRepository.findAll()
                .stream()
                .map(ServiceDto::mapToServiceDto)
                .toList();
    }

    public ServiceDto findById(Long id) {
        return serviceRepository.findById(id)
                .map(ServiceDto::mapToServiceDto)
                .orElseThrow(() -> new RuntimeException("Service with id " + id + " not found"));
    }
} 