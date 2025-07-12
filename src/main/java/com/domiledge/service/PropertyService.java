package com.domiledge.service;

import com.domiledge.dto.PropertyDto;
import com.domiledge.mapper.PropertyMapper;
import com.domiledge.model.Property;
import com.domiledge.model.User;
import com.domiledge.repository.PropertyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PropertyService {

    private final PropertyRepository propertyRepository;
    private final PropertyMapper propertyMapper;

    public List<PropertyDto> getAllForUser(User user) {
        return propertyRepository.findAllByOwner(user)
                .stream()
                .map(propertyMapper::toDto)
                .collect(Collectors.toList());
    }

    public PropertyDto create(PropertyDto dto, User user) {
        Property property = propertyMapper.toEntity(dto);
        property.setOwner(user);
        return propertyMapper.toDto(propertyRepository.save(property));
    }

    public void delete(UUID id, User user) {
        Property property = propertyRepository.findById(id)
                .filter(p -> p.getOwner().getId().equals(user.getId()))
                .orElseThrow(() -> new RuntimeException("Property not found or unauthorized"));
        propertyRepository.delete(property);
    }

    public PropertyDto update(UUID id, PropertyDto dto, User user) {
        Property property = propertyRepository.findById(id)
                .filter(p -> p.getOwner().getId().equals(user.getId()))
                .orElseThrow(() -> new RuntimeException("Property not found or unauthorized"));

        property.setTitle(dto.getTitle());
        property.setAddress(dto.getAddress());
        property.setDescription(dto.getDescription());
        property.setCoverImageUrl(dto.getCoverImageUrl());

        return propertyMapper.toDto(propertyRepository.save(property));
    }

    public String uploadCoverImage(UUID propertyId, MultipartFile file, User user) {
        // Check if property exists and is owned by the current user
        Property property = propertyRepository.findById(propertyId)
                .filter(p -> p.getOwner().getId().equals(user.getId()))
                .orElseThrow(() -> new RuntimeException("Property not found or unauthorized"));

        // Validate file extension
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || !originalFilename.matches("(?i)^.*\\.(jpg|jpeg|png|webp)$")) {
            throw new IllegalArgumentException("Invalid image file type. Only JPG, JPEG, PNG, and WEBP are allowed.");
        }

        // Generate a safe and unique filename
        String filename = UUID.randomUUID() + "_" + originalFilename.replaceAll("\\s+", "_");

        // Define directory structure: uploads/{userId}/{propertyId}/
        Path uploadDir = Paths.get("uploads", user.getId().toString(), propertyId.toString());
        Path fullPath = uploadDir.resolve(filename);

        try {
            // Create the directory if it doesn't exist
            Files.createDirectories(uploadDir);

            // Write file bytes to disk
            Files.write(fullPath, file.getBytes());
        } catch (IOException e) {
            throw new RuntimeException("Failed to save image", e);
        }

        // Set relative URL for accessing the image
        String url = "/uploads/" + user.getId() + "/" + propertyId + "/" + filename;
        property.setCoverImageUrl(url);
        propertyRepository.save(property);

        return url;
    }



}
