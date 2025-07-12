package com.domiledge.service;

import com.domiledge.dto.PropertyDto;
import com.domiledge.mapper.PropertyMapper;
import com.domiledge.model.Property;
import com.domiledge.model.User;
import com.domiledge.repository.PropertyRepository;
import com.domiledge.service.image.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

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
    private final ImageService imageService;

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

        // Validate original file extension
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || !originalFilename.matches("(?i)^.*\\.(jpg|jpeg|png|webp)$")) {
            throw new IllegalArgumentException("Invalid image file type. Only JPG, JPEG, PNG, and WEBP are allowed.");
        }

        // Convert the image and save it as PNG
        String imageUrl = imageService.convertToPngAndSave(file, user.getId(), propertyId);

        // Update the property with the new image URL
        property.setCoverImageUrl(imageUrl);
        propertyRepository.save(property);

        return imageUrl;
    }


    public ResponseEntity<?> loadCoverImage(UUID propertyId, User user) {
        // Verify the property exists and belongs to the authenticated user
        Property property = propertyRepository.findById(propertyId)
                .filter(p -> p.getOwner().getId().equals(user.getId()))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Property not found or unauthorized"));

        String coverImageUrl = property.getCoverImageUrl();
        if (coverImageUrl == null || coverImageUrl.isBlank()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Cover image not set");
        }

        try {
            // Resolve the file path securely
            Path filePath = Paths.get("uploads")
                    .resolve(user.getId().toString())
                    .resolve(property.getId().toString())
                    .resolve(Paths.get(coverImageUrl).getFileName().toString());

            Resource resource = new UrlResource(filePath.toUri());

            if (!resource.exists() || !resource.isReadable()) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Image not found or unreadable");
            }

            // Return the image with appropriate headers
            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_PNG)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                    .body(resource);

        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Could not load image", e);
        }
    }

}
