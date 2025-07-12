package com.domiledge.mapper;

import com.domiledge.dto.PropertyDto;
import com.domiledge.model.Property;
import org.springframework.stereotype.Component;

@Component
public class PropertyMapper {

    public PropertyDto toDto(Property property) {
        return PropertyDto.builder()
                .id(property.getId())
                .title(property.getTitle())
                .address(property.getAddress())
                .description(property.getDescription())
                .coverImageUrl(property.getCoverImageUrl())
                .build();
    }

    public Property toEntity(PropertyDto dto) {
        return Property.builder()
                .id(dto.getId())
                .title(dto.getTitle())
                .address(dto.getAddress())
                .description(dto.getDescription())
                .coverImageUrl(dto.getCoverImageUrl())
                .build();
    }
}