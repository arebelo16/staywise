package com.domiledge.controller;

import com.domiledge.dto.PropertyDto;
import com.domiledge.service.AuthenticatedUserProvider;
import com.domiledge.service.PropertyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/properties")
@RequiredArgsConstructor
public class PropertyController {

    private final PropertyService propertyService;
    private final AuthenticatedUserProvider userProvider;

    @GetMapping
    public List<PropertyDto> getAll() {
        return propertyService.getAllForUser(userProvider.getAuthenticatedUser());
    }

    @PostMapping
    public PropertyDto create(@RequestBody PropertyDto dto) {
        return propertyService.create(dto, userProvider.getAuthenticatedUser());
    }

    @PutMapping("/{id}")
    public PropertyDto update(@PathVariable UUID id, @RequestBody PropertyDto dto) {
        return propertyService.update(id, dto, userProvider.getAuthenticatedUser());
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id) {
        propertyService.delete(id, userProvider.getAuthenticatedUser());
    }

    @PostMapping("/{id}/upload-cover")
    public ResponseEntity<String> uploadCoverImage(
            @PathVariable UUID id,
            @RequestParam("file") MultipartFile file) {

        String imageUrl = propertyService.uploadCoverImage(id, file, userProvider.getAuthenticatedUser());

        return ResponseEntity.ok(imageUrl);
    }

}
