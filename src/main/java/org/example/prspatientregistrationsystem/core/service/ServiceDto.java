package org.example.prspatientregistrationsystem.core.service;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Builder
@Data
public class ServiceDto {
    private Long id;
    private String name;
    private BigDecimal price;
    private String description;

    public static ServiceEntity mapToEntity(ServiceDto serviceDto) {
        System.out.println("ServiceDto.mapToEntity called with: " + serviceDto);
        ServiceEntity entity = ServiceEntity.builder()
                .id(serviceDto.getId())
                .name(serviceDto.getName())
                .price(serviceDto.getPrice())
                .description(serviceDto.getDescription())
                .build();
        System.out.println("ServiceDto.mapToEntity created entity: " + entity);
        return entity;
    }

    public static ServiceDto mapToServiceDto(ServiceEntity serviceEntity) {
        return ServiceDto.builder()
                .id(serviceEntity.getId())
                .name(serviceEntity.getName())
                .price(serviceEntity.getPrice())
                .description(serviceEntity.getDescription())
                .build();
    }
} 