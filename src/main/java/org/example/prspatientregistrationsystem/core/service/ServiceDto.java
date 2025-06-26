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
        return new ServiceEntity(
                serviceDto.getId(),
                serviceDto.getName(),
                serviceDto.getPrice(),
                serviceDto.getDescription()
        );
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